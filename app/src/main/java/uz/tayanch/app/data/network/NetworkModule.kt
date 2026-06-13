package uz.tayanch.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uz.tayanch.app.data.dto.AuthResponse
import uz.tayanch.app.data.dto.OpponentDto
import uz.tayanch.app.data.dto.QuizGradeResponse
import uz.tayanch.app.data.dto.SimpleStatusResponse
import uz.tayanch.app.data.mock.MockData

/**
 * The Ktor client, wired to a [MockEngine]. Every route the app calls is handled
 * here exactly as a real server would: the client code, DTOs, repository and
 * view-models are all production-shaped. Swapping `MockEngine` for `OkHttp` and
 * deleting the `engine { }` block is the only change needed to hit a real backend.
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
     * Server-side state authority (Pillar 9): the running XP total lives on the
     * "server", not the client. The client only submits proof-of-work.
     */
    private var serverTotalXp = 1180

    val client = HttpClient(MockEngine) {
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            url(ApiRoutes.base)
            contentType(ContentType.Application.Json)
            // A real build would add: Authorization, X-App-Signature (HMAC),
            // and pin the server cert via the OkHttp engine.
        }
        engine {
            addHandler { request ->
                val path = request.url.encodedPath.removePrefix("/")
                val params = request.url.parameters
                when {
                    path == ApiRoutes.login || path == ApiRoutes.register ->
                        respond(authJson(onboarded = path == ApiRoutes.login), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.onboarding ->
                        respond(statusJson("OK", "Profil saqlandi"), HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.majors ->
                        respond(MockData.majorsJson, HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.interests -> {
                        val page = params["page"]?.toIntOrNull() ?: 1
                        val body = if (page <= 1) MockData.interestsPage1Json else MockData.interestsPage2Json
                        respond(body, HttpStatusCode.OK, jsonHeaders)
                    }

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
                        respond(MockData.roadmapJson, HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.careerHub ->
                        respond(MockData.careerHubJson, HttpStatusCode.OK, jsonHeaders)

                    path == ApiRoutes.arenaDeck ->
                        respond(MockData.battleDeckJson, HttpStatusCode.OK, jsonHeaders)

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
                        val body = MockData.contentDetails[id]
                        if (body != null) respond(body, HttpStatusCode.OK, jsonHeaders)
                        else respond(statusJson("NOT_FOUND", "No content for $id"), HttpStatusCode.NotFound, jsonHeaders)
                    }

                    else -> respond(
                        statusJson("NOT_FOUND", "Unhandled route: /$path"),
                        HttpStatusCode.NotFound, jsonHeaders,
                    )
                }
            }
        }
    }

    private fun authJson(onboarded: Boolean): String = json.encodeToString(
        AuthResponse(
            access_token = "mock.access.${System.nanoTime()}",
            refresh_token = "mock.refresh.${System.nanoTime()}",
            session_id = "sess-${System.nanoTime()}",
            onboarded = onboarded,
        ),
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
