package uz.tayanch.app.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import uz.tayanch.app.data.dto.AssignmentSubmitRequest
import uz.tayanch.app.data.dto.AuthResponse
import uz.tayanch.app.data.dto.CareerHubDto
import uz.tayanch.app.data.dto.ContentDetailDto
import uz.tayanch.app.data.dto.InterestsResponse
import uz.tayanch.app.data.dto.LeaderboardResponse
import uz.tayanch.app.data.dto.LoginRequest
import uz.tayanch.app.data.dto.MajorsResponse
import uz.tayanch.app.data.dto.OnboardingRequest
import uz.tayanch.app.data.dto.OpponentDto
import uz.tayanch.app.data.dto.PublicKeyResponse
import uz.tayanch.app.data.dto.QuizGradeResponse
import uz.tayanch.app.data.dto.QuizSubmitRequest
import uz.tayanch.app.data.dto.RefreshRequest
import uz.tayanch.app.data.dto.RegisterRequest
import uz.tayanch.app.data.dto.RoadmapResponse
import uz.tayanch.app.data.dto.SimpleStatusResponse
import uz.tayanch.app.data.dto.UserProfileDto
import uz.tayanch.app.data.dto.VacancyDto
import uz.tayanch.app.data.dto.VacancyListResponse
import uz.tayanch.app.data.network.ApiRoutes
import uz.tayanch.app.data.network.NetworkModule
import uz.tayanch.app.data.security.SecureSessionStore

/**
 * The single gateway between UI/view-models and the network. Every call is a
 * normal suspend function; callers wrap them in runCatching. Because the client
 * is injectable, this same class works untouched against MockEngine or a real
 * server.
 */
class TayanchRepository(
    private val store: SecureSessionStore,
    private val client: HttpClient = NetworkModule.mockClient(),
) {

    private fun interestCsv(): String = store.interestIds().joinToString(",")

    /** Pillar 7 — fetch the RSA public key used to seal the password field. */
    suspend fun getPublicKey(): PublicKeyResponse =
        client.get(ApiRoutes.publicKey).body()

    suspend fun register(req: RegisterRequest): AuthResponse =
        client.post(ApiRoutes.register) { setBody(req) }.body()

    suspend fun login(req: LoginRequest): AuthResponse =
        client.post(ApiRoutes.login) { setBody(req) }.body()

    /** Pillar 16 — manual refresh (the OkHttp Auth plugin also rotates on 401). */
    suspend fun refresh(req: RefreshRequest): AuthResponse =
        client.post(ApiRoutes.refresh) { setBody(req) }.body()

    suspend fun submitOnboarding(req: OnboardingRequest): SimpleStatusResponse =
        client.post(ApiRoutes.onboarding) { setBody(req) }.body()

    suspend fun getMajors(): MajorsResponse =
        client.get(ApiRoutes.majors).body()

    suspend fun getInterests(page: Int): InterestsResponse =
        client.get(ApiRoutes.interests) { parameter("page", page) }.body()

    suspend fun getProfile(): UserProfileDto =
        client.get(ApiRoutes.profile).body()

    suspend fun getLeaderboard(scope: String): LeaderboardResponse =
        client.get(ApiRoutes.leaderboard) { parameter("scope", scope) }.body()

    /** Roadmap for a single chosen interest (Home switches between selected ones). */
    suspend fun getRoadmap(interestId: String): RoadmapResponse =
        client.get(ApiRoutes.roadmap) { parameter("interest", interestId) }.body()

    suspend fun getCareerHub(): CareerHubDto =
        client.get(ApiRoutes.careerHub).body()

    suspend fun getContent(contentId: String): ContentDetailDto =
        client.get(ApiRoutes.content + contentId).body()

    /** Global quiz assembled from the user's selected interests. */
    suspend fun getGlobalQuiz(): ContentDetailDto =
        client.get(ApiRoutes.content + "quiz-global") { parameter("interests", interestCsv()) }.body()

    /** Arena deck assembled from the user's selected interests. */
    suspend fun getBattleDeck(): ContentDetailDto =
        client.get(ApiRoutes.arenaDeck) { parameter("interests", interestCsv()) }.body()

    suspend fun findOpponent(): OpponentDto =
        client.post(ApiRoutes.arenaFind).body()

    /** Submits only the user's choice; the server grades it (Pillar 3). */
    suspend fun gradeQuestion(submit: QuizSubmitRequest): QuizGradeResponse =
        client.get(ApiRoutes.quizGrade) {
            parameter("qid", submit.question_id)
            parameter("opts", submit.selected_option_ids.joinToString(","))
            parameter("input", submit.input_answer ?: "")
            parameter("ms", submit.elapsed_ms)
        }.body()

    suspend fun submitAssignment(req: AssignmentSubmitRequest): SimpleStatusResponse =
        client.post(ApiRoutes.assignmentSubmit) { setBody(req) }.body()

    suspend fun requestMockInterview(): SimpleStatusResponse =
        client.post(ApiRoutes.mockRequest).body()

    suspend fun getVacancies(): VacancyListResponse =
        client.get(ApiRoutes.vacancies).body()

    suspend fun getVacancy(id: String): VacancyDto =
        client.get(ApiRoutes.vacancy + id).body()

    suspend fun applyVacancy(id: String): SimpleStatusResponse =
        client.post(ApiRoutes.vacancyApply) { setBody(mapOf("vacancy_id" to id)) }.body()
}
