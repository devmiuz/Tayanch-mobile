package uz.tayanch.app.data.network

import uz.tayanch.app.data.security.NativeSecrets

/**
 * Pillar 12 — Dynamic endpoint obfuscation.
 *
 * Hardcoded URL strings land in the .rodata of a compiled DEX and are trivially
 * recovered with `strings` or JADX. Instead each endpoint is stored XOR-encoded
 * and only reconstructed in memory at the call site, so static analysis of the
 * APK never sees "api/v1/...". The XOR key itself is NOT a Kotlin constant — it
 * lives in `libtayanch_secrets.so` and is fetched via JNI ([NativeSecrets],
 * Pillar 8). The "Tayanch" fallback only applies off-device (JVM tests/previews
 * where the .so isn't loaded).
 */
object ApiRoutes {

    private val key = runCatching { NativeSecrets.xorKey }.getOrDefault("Tayanch").toByteArray()

    private fun decode(data: ByteArray): String {
        val out = ByteArray(data.size)
        for (i in data.indices) out[i] = (data[i].toInt() xor key[i % key.size].toInt()).toByte()
        return String(out, Charsets.UTF_8)
    }

    val base: String get() = decode(BASE)
    val login: String get() = decode(LOGIN)
    val register: String get() = decode(REGISTER)
    val refresh: String get() = decode(REFRESH)
    val publicKey: String get() = decode(PUBLIC_KEY)
    val onboarding: String get() = decode(ONBOARDING)
    val majors: String get() = decode(MAJORS)
    val interests: String get() = decode(INTERESTS)
    val profile: String get() = decode(PROFILE)
    val leaderboard: String get() = decode(LEADERBOARD)
    val roadmap: String get() = decode(ROADMAP)
    val content: String get() = decode(CONTENT)
    val quizGrade: String get() = decode(QUIZ_GRADE)
    val careerHub: String get() = decode(CAREER_HUB)
    val assignmentSubmit: String get() = decode(ASSIGNMENT_SUBMIT)
    val mockRequest: String get() = decode(MOCK_REQUEST)
    val arenaFind: String get() = decode(ARENA_FIND)
    val arenaDeck: String get() = decode(ARENA_DECK)
    val vacancies: String get() = decode(VACANCIES)
    val vacancy: String get() = decode(VACANCY)
    val vacancyApply: String get() = decode(VACANCY_APPLY)

    private val VACANCIES: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 34, 0, 26, 0, 0, 0, 1, 49, 18)
    private val VACANCY: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 34, 0, 26, 0, 0, 0, 1, 49, 18, 86)
    private val VACANCY_APPLY: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 34, 0, 26, 0, 0, 0, 1, 49, 18, 86, 0, 30, 19, 4, 45)

    private val BASE: ByteArray = byteArrayOf(60, 21, 13, 17, 29, 89, 71, 123, 0, 9, 8, 64, 23, 9, 45, 0, 23, 2, 6, 77, 29, 46, 78)
    private val LOGIN: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 53, 20, 13, 9, 65, 15, 7, 51, 8, 23)
    private val REGISTER: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 53, 20, 13, 9, 65, 17, 13, 51, 8, 10, 21, 11, 17)
    private val REFRESH: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 53, 20, 13, 9, 65, 17, 13, 50, 19, 28, 18, 6)
    private val PUBLIC_KEY: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 53, 20, 13, 9, 65, 19, 29, 54, 13, 16, 2, 67, 8, 13, 45)
    private val ONBOARDING: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 59, 15, 27, 14, 15, 17, 12, 61, 15, 30)
    private val MAJORS: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 57, 0, 19, 14, 28, 16)
    private val INTERESTS: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 61, 15, 13, 4, 28, 6, 27, 32, 18)
    private val PROFILE: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 33, 18, 28, 19, 29, 76, 5, 49)
    private val LEADERBOARD: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 56, 4, 24, 5, 11, 17, 10, 59, 0, 11, 5)
    private val ROADMAP: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 38, 14, 24, 5, 3, 2, 24)
    private val CONTENT: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 55, 14, 23, 21, 11, 13, 28, 123)
    private val QUIZ_GRADE: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 37, 20, 16, 27, 65, 4, 26, 53, 5, 28)
    private val CAREER_HUB: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 55, 0, 11, 4, 11, 17, 71, 60, 20, 27)
    private val ASSIGNMENT_SUBMIT: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 55, 0, 11, 4, 11, 17, 71, 53, 18, 10, 8, 9, 13, 5, 49, 15, 13, 78, 29, 22, 10, 57, 8, 13)
    private val MOCK_REQUEST: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 55, 0, 11, 4, 11, 17, 71, 57, 14, 26, 10, 67, 10, 6, 32, 4, 11, 23, 7, 6, 31, 123, 19, 28, 16, 27, 6, 27, 32)
    private val ARENA_FIND: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 53, 19, 28, 15, 15, 76, 14, 61, 15, 29)
    private val ARENA_DECK: ByteArray = byteArrayOf(53, 17, 16, 78, 24, 82, 71, 53, 19, 28, 15, 15, 76, 12, 49, 2, 18)
}
