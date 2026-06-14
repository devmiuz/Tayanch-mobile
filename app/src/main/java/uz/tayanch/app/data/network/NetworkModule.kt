package uz.tayanch.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import uz.tayanch.app.BuildConfig
import uz.tayanch.app.data.dto.AuthResponse
import uz.tayanch.app.data.dto.OpponentDto
import uz.tayanch.app.data.dto.PublicKeyResponse
import uz.tayanch.app.data.dto.QuizGradeResponse
import uz.tayanch.app.data.dto.RefreshRequest
import uz.tayanch.app.data.dto.SimpleStatusResponse
import uz.tayanch.app.data.mock.MockData
import uz.tayanch.app.data.security.AppSignature
import uz.tayanch.app.data.security.SecureSessionStore
import uz.tayanch.app.data.security.SessionManager

/**
 * Builds the Ktor [HttpClient]. Two engines, one config shape:
 *
 *  - [mockClient]  — MockEngine serving the bundled JSON. The default
 *    (`USE_REAL_BACKEND=false`), so the app runs fully offline for a demo.
 *  - [realClient]  — OkHttp against the FastAPI backend, wired with every
 *    transport pillar: certificate pinning (18), the HMAC app-signature gate
 *    (13, secret from the NDK), and bearer auth with refresh-token rotation
 *    (16) that evicts the session on a single-session lock (15).
 *
 * [build] picks one based on BuildConfig. DTOs, repository and view-models are
 * identical for both.
 */
object NetworkModule {

    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val jsonHeaders =
        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

    /**
     * Server-side state authority (Pillar 21): the running XP total lives on the
     * "server", not the client. The client only submits proof-of-work.
     */
    private var serverTotalXp = 1180

    fun build(store: SecureSessionStore, sessions: SessionManager): HttpClient =
        if (BuildConfig.USE_REAL_BACKEND) realClient(store, sessions) else mockClient()

    // --------------------------- real backend --------------------------------

    private fun realClient(store: SecureSessionStore, sessions: SessionManager): HttpClient {
        // Pillar 18 — certificate pinning (+ Pillar 13 app-signature) live on the
        // OkHttp layer, where the request URL is fully resolved so the signed path
        // matches the server's `request.url.path` exactly.
        val pinner = CertificatePinner.Builder()
            .add(BuildConfig.PIN_HOST, BuildConfig.CERT_PIN_PRIMARY, BuildConfig.CERT_PIN_BACKUP)
            .build()
        val ok = OkHttpClient.Builder()
            .certificatePinner(pinner)
            .addInterceptor { chain ->
                val req = chain.request()
                val sig = AppSignature.sign(req.method, req.url.encodedPath)
                chain.proceed(
                    req.newBuilder()
                        .header(AppSignature.HEADER_TS, sig.timestamp)
                        .header(AppSignature.HEADER_NONCE, sig.nonce)
                        .header(AppSignature.HEADER_SIG, sig.signature)
                        .build(),
                )
            }
            .build()

        return HttpClient(OkHttp) {
            engine { preconfigured = ok }
            install(ContentNegotiation) { json(json) }
            install(Auth) {
                bearer {
                    loadTokens {
                        val a = store.accessToken()
                        val r = store.refreshToken()
                        if (a != null && r != null) BearerTokens(a, r) else null
                    }
                    // Pillar 16 — on a 401, rotate via /auth/refresh. If the refresh
                    // itself is rejected (the session was replaced — Pillar 15), wipe
                    // the local session and signal the UI to bounce to the gateway.
                    refreshTokens {
                        val rt = store.refreshToken()
                        if (rt == null) {
                            sessions.invalidate(SessionManager.Reason.REFRESH_FAILED)
                            return@refreshTokens null
                        }
                        val resp = client.post(ApiRoutes.refresh) {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshRequest(rt))
                        }
                        if (resp.status.isSuccess()) {
                            val body = resp.body<AuthResponse>()
                            store.updateTokens(body.access_token, body.refresh_token)
                            BearerTokens(body.access_token, body.refresh_token)
                        } else {
                            sessions.invalidate(SessionManager.Reason.SESSION_REPLACED)
                            null
                        }
                    }
                    sendWithoutRequest { true }
                }
            }
            defaultRequest {
                url(BuildConfig.API_BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // ------------------------------- mock ------------------------------------

    fun mockClient(): HttpClient = HttpClient(MockEngine) {
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            url(ApiRoutes.base)
            contentType(ContentType.Application.Json)
        }
        engine {
            addHandler { request ->
                val path = request.url.encodedPath.removePrefix("/")
                val params = request.url.parameters
                when {
                    path == ApiRoutes.login || path == ApiRoutes.register ->
                        respond(authJson(onboarded = path == ApiRoutes.login), HttpStatusCode.OK, jsonHeaders)

                    // Pillar 7 — public key fetch (mock path uses plaintext, so this
                    // is a clearly-labelled placeholder never used to seal).
                    path == ApiRoutes.publicKey ->
                        respond(publicKeyJson(), HttpStatusCode.OK, jsonHeaders)

                    // Pillar 16 — refresh returns a fresh token pair on the same flow.
                    path == ApiRoutes.refresh ->
                        respond(authJson(onboarded = true), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.onboarding ->
                        respond(statusJson("OK", "Profil saqlandi"), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.majors ->
                        respond(MockData.majorsJson, HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.interests ->
                        respond(MockData.interestsCatalogJson, HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.profile ->
                        respond(MockData.userProfileJson, HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.leaderboard -> {
                        val global = params["scope"] == "global"
                        respond(
                            if (global) MockData.leaderboardGlobalJson else MockData.leaderboardJson,
                            HttpStatusCode.OK, jsonHeaders,
                        )
                    }

                    path == ApiRoutes.roadmap ->
                        respond(MockData.roadmapJson(params["interest"].orEmpty()), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.careerHub ->
                        respond(MockData.careerHubJson, HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.arenaDeck ->
                        respond(MockData.arenaDeckJson(interestList(params["interests"])), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.arenaFind ->
                        respond(opponentJson(), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.assignmentSubmit ->
                        respond(statusJson("PENDING", "Tekshiruv uchun yuborildi"), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.mockRequest ->
                        respond(statusJson("REQUESTED", "Tekshiruvchi vaqtingizni tasdiqlaydi"), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.quizGrade ->
                        respond(gradeJson(params["qid"], params["opts"], params["input"]), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.vacancyApply ->
                        respond(statusJson("APPLIED", "Arizangiz muvaffaqiyatli yuborildi"), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.vacancies ->
                        respond(MockData.vacancyListJson, HttpStatusCode.OK, jsonHeaders)

                    path.startsWith(ApiRoutes.vacancy) -> {
                        val id = path.removePrefix(ApiRoutes.vacancy)
                        val body = MockData.vacancyDetails[id]
                        if (body != null) respond(body, HttpStatusCode.OK, jsonHeaders)
                        else respond(statusJson("NOT_FOUND", "No vacancy $id"), HttpStatusCode.NotFound, jsonHeaders)
                    }

                    path.startsWith(ApiRoutes.content) -> {
                        val id = path.removePrefix(ApiRoutes.content)
                        // The global quiz is assembled from the selected interests.
                        if (id == "quiz-global") {
                            respond(MockData.globalQuizJson(interestList(params["interests"])), HttpStatusCode.OK, jsonHeaders)
                        } else {
                            val body = MockData.contentDetails[id]
                            if (body != null) respond(body, HttpStatusCode.OK, jsonHeaders)
                            else respond(statusJson("NOT_FOUND", "No content for $id"), HttpStatusCode.NotFound, jsonHeaders)
                        }
                    }

                    else -> respond(
                        statusJson("NOT_FOUND", "Unhandled route: /$path"),
                        HttpStatusCode.NotFound, jsonHeaders,
                    )
                }
            }
        }
    }

    /** Parse the `?interests=a,b` query param into a clean id list. */
    private fun interestList(csv: String?): List<String> =
        csv.orEmpty().split(",").map { it.trim() }.filter { it.isNotEmpty() }

    private fun authJson(onboarded: Boolean): String = json.encodeToString(
        AuthResponse(
            access_token = "mock.access.${System.nanoTime()}",
            refresh_token = "mock.refresh.${System.nanoTime()}",
            session_id = "sess-${System.nanoTime()}",
            onboarded = onboarded,
        ),
    )

    private fun publicKeyJson(): String = json.encodeToString(
        PublicKeyResponse(public_key = "MOCK-NO-RSA", key_id = "mock"),
    )

    private fun statusJson(status: String, message: String): String =
        json.encodeToString(SimpleStatusResponse(status, message))

    private fun opponentJson(): String =
        json.encodeToString(OpponentDto(name = "Timur B.", level = "Middle", rank = 2))

    /**
     * The grader. It reads ONLY the submitted answer + the hidden [MockData
     * .answerKey] (server-side); the client never received the key. This is the
     * crux of Zero-Trust grading (Pillar 3).
     */
    private fun gradeJson(qid: String?, opts: String?, input: String?): String {
        val spec = MockData.answerKey[qid]
            ?: return statusJson("BAD_REQUEST", "Unknown question")
        val chosen = opts.orEmpty().split(",").filter { it.isNotBlank() }.toSet()
        val typedAnswer = input.orEmpty().trim().lowercase()

        val correct = when {
            spec.correctInputs.isNotEmpty() -> typedAnswer in spec.correctInputs
            else -> chosen == spec.correctOptionIds
        }
        if (correct) serverTotalXp += spec.xp

        return json.encodeToString(
            QuizGradeResponse(
                question_id = qid!!,
                is_correct = correct,
                correct_option_ids = spec.correctOptionIds.toList(),
                explanation = spec.explanation,
                xp_earned = if (correct) spec.xp else 0,
                new_total_xp = serverTotalXp,
            ),
        )
    }
}
