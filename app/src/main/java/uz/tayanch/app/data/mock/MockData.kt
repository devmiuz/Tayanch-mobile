package uz.tayanch.app.data.mock

import uz.tayanch.app.data.Interests

/**
 * Stand-in for the FastAPI backend. Every value here is what the server WOULD
 * return; the Ktor client (NetworkModule) talks to a MockEngine that serves
 * these strings. User-facing content is Uzbek.
 *
 * Learning content is split **per interest** ([CyberContent], [PythonContent],
 * [DesignContent], [AndroidContent]). This object aggregates them and serves the
 * roadmap / global quiz / Arena based on the user's selected interest ids — so
 * "shared screens take the interest-id list and show matching content".
 *
 * The [answerKey] lives "server-side" and is never serialized into any client
 * response (Pillar 3 — Zero-Trust grading).
 */
object MockData {

    // ----- Per-interest aggregation -----

    private val roadmaps: Map<String, String> = mapOf(
        "int-cyber" to CyberContent.roadmapJson,
        "int-python" to PythonContent.roadmapJson,
        "int-design" to DesignContent.roadmapJson,
        "int-android" to AndroidContent.roadmapJson,
    )

    private val samplePools: Map<String, List<String>> = mapOf(
        "int-cyber" to CyberContent.samplePool,
        "int-python" to PythonContent.samplePool,
        "int-design" to DesignContent.samplePool,
        "int-android" to AndroidContent.samplePool,
    )

    /** Content payloads (article/weblink/video/flashcard/quiz) across all interests. */
    val contentDetails: Map<String, String> =
        CyberContent.contentDetails +
            PythonContent.contentDetails +
            DesignContent.contentDetails +
            AndroidContent.contentDetails

    /** Merged server-side grading key across all interests. */
    val answerKey: Map<String, AnswerSpec> =
        CyberContent.answerKey +
            PythonContent.answerKey +
            DesignContent.answerKey +
            AndroidContent.answerKey

    /** Roadmap for a single interest (Home shows one at a time via the switcher). */
    fun roadmapJson(interestId: String): String = roadmaps[interestId] ?: CyberContent.roadmapJson

    private fun pool(interestIds: List<String>): List<String> {
        val ids = interestIds.ifEmpty { Interests.defaultIds }
        return ids.flatMap { samplePools[it].orEmpty() }
    }

    /** Global quiz assembled from the selected interests' question pools. */
    fun globalQuizJson(interestIds: List<String>): String {
        val qs = pool(interestIds).shuffled().take(6).joinToString(",")
        return """{ "content_id": "quiz-global", "type": "QUIZ", "title": "Umumiy takrorlash testi", "reward_xp": 40, "questions": [ $qs ] }"""
    }

    /** Arena deck assembled from the selected interests' question pools. */
    fun arenaDeckJson(interestIds: List<String>): String {
        val qs = pool(interestIds).shuffled().take(5).joinToString(",")
        return """{ "content_id": "battle-deck", "type": "QUIZ", "title": "Arena jangi", "reward_xp": 0, "questions": [ $qs ] }"""
    }

    // ----- Onboarding catalog -----

    val majorsJson = """
        {
          "majors": [
            { "id": "m1", "name": "Kiberxavfsizlikni boshqarish" },
            { "id": "m2", "name": "Dasturiy injiniring" },
            { "id": "m3", "name": "Kompyuter ilmlari" },
            { "id": "m4", "name": "Axborot tizimlari" },
            { "id": "m5", "name": "Ma'lumotlar ilmi" }
          ]
        }
    """.trimIndent()

    /** The interests catalog = the four learning fields (single page). */
    val interestsCatalogJson: String = run {
        val items = Interests.all.joinToString(",") {
            """{ "id": "${it.id}", "name": "${it.emoji} ${it.name}" }"""
        }
        """{ "page": 1, "has_more": false, "ai_generated": false, "interests": [ $items ] }"""
    }

    // ----- Profile / leaderboards / career hub (shared) -----

    val userProfileJson = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "full_name": "Jasur Abdullaev",
          "current_level": "Strong Junior",
          "target_level": "Middle",
          "current_xp": 1180,
          "expected_salary": 1200,
          "global_rank": 3,
          "ai_motivation": "Siz Strong Junior darajasidasiz va maqsadingiz sari barqaror ilgarilayapsiz. Bu hafta 2 pog'ona ko'tarildingiz — tanlagan yo'nalishingizdagi keyingi mavzuni yakunlang va Middle qo'l ostingizda bo'ladi.",
          "velocity": {
            "target_salary": 1200,
            "weekly_xp": 450,
            "projection_text": "Haftasiga 450 XP sur'atida Middle suhbat bosqichiga ~2.5 oyda yetasiz."
          },
          "focus": {
            "average_focus_time": "4m 12s",
            "distraction_rate": "Past (0.8 / maqola)"
          },
          "badges": [
            { "id": "b1", "title": "Birinchi qadam", "emoji": "🩸", "earned": true },
            { "id": "b2", "title": "Kashshof", "emoji": "🛡️", "earned": true },
            { "id": "b3", "title": "Diqqat ustasi", "emoji": "🎯", "earned": true },
            { "id": "b4", "title": "Arena g'olibi", "emoji": "⚔️", "earned": false },
            { "id": "b5", "title": "Senior tafakkur", "emoji": "🧠", "earned": false },
            { "id": "b6", "title": "Mukammal test", "emoji": "💯", "earned": false }
          ]
        }
    """.trimIndent()

    val leaderboardJson = """
        {
          "scope": "Mutaxassisligim",
          "leaderboard": [
            { "rank": 1, "name": "Alisher K.", "level": "Senior", "xp": 4520 },
            { "rank": 2, "name": "Timur B.", "level": "Middle", "xp": 1890 },
            { "rank": 3, "name": "Siz", "level": "Strong Junior", "xp": 1180, "is_me": true },
            { "rank": 4, "name": "Dilnoza R.", "level": "Junior", "xp": 980 },
            { "rank": 5, "name": "Sardor M.", "level": "Junior", "xp": 760 },
            { "rank": 6, "name": "Kamola S.", "level": "Junior", "xp": 540 }
          ]
        }
    """.trimIndent()

    val leaderboardGlobalJson = """
        {
          "scope": "Global",
          "leaderboard": [
            { "rank": 1, "name": "Bekzod (Backend)", "level": "Senior", "xp": 8120 },
            { "rank": 2, "name": "Alisher K.", "level": "Senior", "xp": 4520 },
            { "rank": 3, "name": "Nigora (UI/UX)", "level": "Middle", "xp": 2300 },
            { "rank": 4, "name": "Timur B.", "level": "Middle", "xp": 1890 },
            { "rank": 5, "name": "Siz", "level": "Strong Junior", "xp": 1180, "is_me": true },
            { "rank": 6, "name": "Dilnoza R.", "level": "Junior", "xp": 980 }
          ]
        }
    """.trimIndent()

    val careerHubJson = """
        {
          "arena": { "min_xp": 500, "current_xp": 1180, "unlocked": true },
          "mock_interview": { "available": true, "reached_level": "Strong Junior", "pending_requests": 1 },
          "assignments": [
            { "level_id": "lvl-junior", "level_label": "Junior bitiruv", "state": "PENDING", "github_url": "github.com/jasur/junior-task" },
            { "level_id": "lvl-middle", "level_label": "Middle bitiruv", "state": "AVAILABLE" }
          ]
        }
    """.trimIndent()

    // ----- Vacancies (job board) -----

    private fun vac(
        id: String, title: String, company: String, salary: String, location: String,
        type: String, category: String, level: String, tags: String, desc: String, reqs: String,
    ) = """
        {
          "id": "$id", "title": "$title", "company": "$company", "salary": "$salary",
          "location": "$location", "type": "$type", "category": "$category", "level": "$level",
          "tags": [$tags], "posted_at": "2 kun oldin", "is_applied": false,
          "description": "$desc",
          "requirements": [$reqs]
        }
    """.trimIndent()

    val vacancyDetails: Map<String, String> = mapOf(
        "v1" to vac("v1", "Junior Android dasturchi", "EPAM Systems", "8 000 000 UZS", "Toshkent", "Toʻliq stavka", "Mobil ishlab chiqish", "Junior",
            "\"Kotlin\", \"Jetpack Compose\"", "Kotlin va Jetpack Compose asosida mobil ilovalar ishlab chiqish. Jamoaviy ishlash va Git bilan tanishlik talab etiladi.",
            "\"Kotlin asoslari\", \"Jetpack Compose\", \"REST API\", \"Git\""),
        "v2" to vac("v2", "SOC tahlilchisi", "UZINFOCOM", "12 000 000 UZS", "Toshkent", "Toʻliq stavka", "Kiberxavfsizlik", "Middle",
            "\"SIEM\", \"OWASP\"", "Xavfsizlik hodisalarini monitoring qilish va insidentlarga javob berish. SIEM tizimlari bilan ishlash tajribasi.",
            "\"SIEM (Splunk/ELK)\", \"OWASP Top 10\", \"Tarmoq protokollari\", \"Ingliz tili B2\""),
        "v3" to vac("v3", "Backend dasturchi (Python/FastAPI)", "PayMe", "15 000 000 UZS", "Masofaviy", "Masofaviy", "Backend", "Middle",
            "\"Python\", \"FastAPI\", \"PostgreSQL\"", "Yuqori yuklamali toʻlov tizimlari uchun API ishlab chiqish. Asinxron Python va PostgreSQL optimallashtirish.",
            "\"Python 3.11+\", \"FastAPI\", \"PostgreSQL\", \"Docker\""),
        "v4" to vac("v4", "Junior Python dasturchi", "Click", "9 000 000 UZS", "Toshkent", "Gibrid", "Backend", "Junior",
            "\"Python\", \"Django\"", "Python va Django yordamida veb-ilovalar uchun backend ishlab chiqish.",
            "\"Python asoslari\", \"OOP\", \"SQL\", \"Git\""),
        "v5" to vac("v5", "UI/UX dizayner", "Uzum", "9 000 000 UZS", "Toshkent", "Gibrid", "Dizayn", "Junior",
            "\"Figma\", \"Material 3\"", "Mahsulot uchun zamonaviy, foydalanuvchiga qulay interfeyslar dizayni.",
            "\"Figma\", \"Material Design 3\", \"Tipografika\", \"Portfolio\""),
        "v6" to vac("v6", "Grafik dizayner", "Korzinka", "7 000 000 UZS", "Toshkent", "Toʻliq stavka", "Dizayn", "Junior",
            "\"Photoshop\", \"Illustrator\"", "Brending, ijtimoiy tarmoq va reklama materiallari uchun grafik dizayn.",
            "\"Rang nazariyasi\", \"Tipografika\", \"Adobe Illustrator\", \"Portfolio\""),
    )

    val vacancyCategories = listOf("Barchasi", "Mobil ishlab chiqish", "Kiberxavfsizlik", "Backend", "Dizayn")

    val vacancyListJson: String = run {
        val cats = vacancyCategories.joinToString(",") { "\"$it\"" }
        val items = vacancyDetails.values.joinToString(",")
        """{ "categories": [$cats], "vacancies": [$items] }"""
    }
}
