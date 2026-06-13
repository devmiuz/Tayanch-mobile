package uz.tayanch.app.data.mock

/**
 * Stand-in for the Ktor/FastAPI backend. Every value here is what the server
 * WOULD return; the Ktor client (NetworkModule) talks to a MockEngine that
 * serves these strings. User-facing content is Uzbek (the app's primary
 * language); technical acronyms (XSS, SQLi, JWT, Argon2id) are kept as-is.
 *
 * The [answerKey] lives "server-side" and is never serialized into any client
 * response, which is exactly how Zero-Trust grading (Pillar 3) is proven.
 */
object MockData {

    val userProfileJson = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "full_name": "Jasur Abdullaev",
          "current_level": "Strong Junior",
          "target_level": "Middle",
          "current_xp": 1180,
          "expected_salary": 1200,
          "global_rank": 3,
          "ai_motivation": "Siz Strong Junior darajasidasiz va 1200 USD maoshni maqsad qilgansiz. Bu hafta 2 pog'ona ko'tarildingiz — Inyeksiya mavzusini yakunlang va Middle qo'l ostingizda bo'ladi.",
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
            { "id": "b2", "title": "OWASP kashshofi", "emoji": "🛡️", "earned": true },
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

    val roadmapJson = """
        {
          "levels": [
            {
              "level_id": "lvl-junior",
              "title": "Junior AppSec",
              "rank_label": "Junior",
              "required_xp": 0,
              "is_unlocked": true,
              "topics": [
                {
                  "topic_id": "topic-auth",
                  "title": "1. Autentifikatsiya kamchiliklari",
                  "order": 1,
                  "contents": [
                    { "content_id": "md-auth", "type": "MARKDOWN", "title": "Buzilgan autentifikatsiya", "reward_xp": 20, "is_completed": true, "is_locked": false, "estimated_minutes": 4 },
                    { "content_id": "web-auth", "type": "WEBLINK", "title": "OWASP autentifikatsiya qo'llanmasi", "reward_xp": 15, "is_completed": true, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "vid-auth", "type": "VIDEO", "title": "JWT tushuntirildi", "reward_xp": 15, "is_completed": true, "is_locked": false, "estimated_minutes": 3 },
                    { "content_id": "fc-auth", "type": "FLASHCARD", "title": "Autentifikatsiya kartalari", "reward_xp": 25, "is_completed": true, "is_locked": false },
                    { "content_id": "quiz-auth", "type": "QUIZ", "title": "Autentifikatsiya testi", "reward_xp": 50, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "topic-injection",
                  "title": "2. Inyeksiya (SQLi / XSS)",
                  "order": 2,
                  "contents": [
                    { "content_id": "md-xss", "type": "MARKDOWN", "title": "XSS'ni tushunish", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "web-xss", "type": "WEBLINK", "title": "PortSwigger: XSS", "reward_xp": 15, "is_completed": false, "is_locked": true, "estimated_minutes": 6 },
                    { "content_id": "vid-xss", "type": "VIDEO", "title": "SQL Injection namoyishi", "reward_xp": 15, "is_completed": false, "is_locked": true, "estimated_minutes": 4 },
                    { "content_id": "fc-xss", "type": "FLASHCARD", "title": "Inyeksiya kartalari", "reward_xp": 25, "is_completed": false, "is_locked": true },
                    { "content_id": "quiz-xss", "type": "QUIZ", "title": "Inyeksiya sinov testi", "reward_xp": 60, "is_completed": false, "is_locked": true }
                  ]
                }
              ],
              "assignment": {
                "level_id": "lvl-junior",
                "title": "Junior bitiruv: login API'ni mustahkamlash",
                "state": "AVAILABLE"
              }
            },
            {
              "level_id": "lvl-middle",
              "title": "Middle AppSec",
              "rank_label": "Middle",
              "required_xp": 1500,
              "is_unlocked": false,
              "topics": [
                {
                  "topic_id": "topic-crypto",
                  "title": "1. Amaliy kriptografiya",
                  "order": 1,
                  "contents": [
                    { "content_id": "md-crypto", "type": "MARKDOWN", "title": "Argon2id va tuzlar", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 6 },
                    { "content_id": "web-crypto", "type": "WEBLINK", "title": "NIST parol bo'yicha tavsiyalar", "reward_xp": 20, "is_completed": false, "is_locked": true, "estimated_minutes": 7 },
                    { "content_id": "vid-crypto", "type": "VIDEO", "title": "TLS 1.3 qo'l berishi", "reward_xp": 20, "is_completed": false, "is_locked": true, "estimated_minutes": 5 },
                    { "content_id": "fc-crypto", "type": "FLASHCARD", "title": "Kriptografiya kartalari", "reward_xp": 30, "is_completed": false, "is_locked": true },
                    { "content_id": "quiz-crypto", "type": "QUIZ", "title": "Kriptografiya testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                }
              ],
              "assignment": {
                "level_id": "lvl-middle",
                "title": "Middle bitiruv: Zero-Trust shlyuzi",
                "state": "LOCKED"
              }
            }
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
        "v3" to vac("v3", "Backend dasturchi (FastAPI)", "PayMe", "15 000 000 UZS", "Masofaviy", "Masofaviy", "Backend", "Middle",
            "\"Python\", \"FastAPI\", \"PostgreSQL\"", "Yuqori yuklamali toʻlov tizimlari uchun API ishlab chiqish. Asinxron Python va PostgreSQL optimallashtirish.",
            "\"Python 3.11+\", \"FastAPI\", \"PostgreSQL\", \"Docker\""),
        "v4" to vac("v4", "Frontend dasturchi (React)", "Click", "10 000 000 UZS", "Toshkent", "Gibrid", "Frontend", "Junior",
            "\"React\", \"TypeScript\"", "Foydalanuvchi interfeyslarini React va TypeScript yordamida ishlab chiqish.",
            "\"React\", \"TypeScript\", \"Tailwind CSS\", \"REST API\""),
        "v5" to vac("v5", "UI/UX dizayner", "Uzum", "9 000 000 UZS", "Toshkent", "Gibrid", "Dizayn", "Junior",
            "\"Figma\", \"Material 3\"", "Mahsulot uchun zamonaviy, foydalanuvchiga qulay interfeyslar dizayni.",
            "\"Figma\", \"Material Design 3\", \"Prototiplash\", \"Portfolio\""),
        "v6" to vac("v6", "DevOps muhandisi", "Beeline", "18 000 000 UZS", "Masofaviy", "Masofaviy", "DevOps", "Senior",
            "\"Docker\", \"Kubernetes\", \"CI/CD\"", "Bulutli infratuzilmani avtomatlashtirish, CI/CD quvurlarini qurish va kuzatuv.",
            "\"Docker\", \"Kubernetes\", \"GitHub Actions\", \"AWS/GCP\""),
    )

    val vacancyCategories = listOf("Barchasi", "Mobil ishlab chiqish", "Kiberxavfsizlik", "Backend", "Frontend", "Dizayn", "DevOps")

    val vacancyListJson: String = run {
        val cats = vacancyCategories.joinToString(",") { "\"$it\"" }
        val items = vacancyDetails.values.joinToString(",")
        """{ "categories": [$cats], "vacancies": [$items] }"""
    }

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

    val interestsPage1Json = """
        {
          "page": 1,
          "has_more": true,
          "ai_generated": false,
          "interests": [
            { "id": "i1", "name": "Penetration Testing" },
            { "id": "i2", "name": "Ilova xavfsizligi" },
            { "id": "i3", "name": "Kriptografiya" },
            { "id": "i4", "name": "Bulut xavfsizligi" },
            { "id": "i5", "name": "Insidentlarga javob" }
          ]
        }
    """.trimIndent()

    val interestsPage2Json = """
        {
          "page": 2,
          "has_more": false,
          "ai_generated": true,
          "interests": [
            { "id": "i6", "name": "Tahdid modellashtirish" },
            { "id": "i7", "name": "Reverse Engineering" },
            { "id": "i8", "name": "DevSecOps" },
            { "id": "i9", "name": "Risklarni baholash" }
          ]
        }
    """.trimIndent()

    // ----- Content payloads, keyed by content_id -----

    private val mdAuth = """
        {
          "content_id": "md-auth",
          "type": "MARKDOWN",
          "title": "Buzilgan autentifikatsiya",
          "reward_xp": 20,
          "estimated_minutes": 4,
          "markdown": "# Buzilgan autentifikatsiya\n\nAutentifikatsiya foydalanuvchi **kimligini** tasdiqlaydi. U buzilganda tajovuzkor hisoblarni egallab olishi mumkin.\n\n## Keng tarqalgan sabablar\n- Rate limit yo'q endpointlarga credential stuffing hujumi\n- Kirgandan keyin yangilanmaydigan session ID lar\n- Parollarni tez xeshlar (MD5/SHA-1) bilan saqlash\n\n## Xavfsizroq xesh\n```kotlin\nval hash = Argon2id.hash(password, salt)\n// xotira-talab: GPU uni arzonga buza olmaydi\n```\n\n> Qoida: hech qachon o'z kriptografiyangizni yozmang va imtiyoz o'zgarganda session id ni yangilang."
        }
    """.trimIndent()

    private val webAuth = """
        {
          "content_id": "web-auth",
          "type": "WEBLINK",
          "title": "OWASP autentifikatsiya qo'llanmasi",
          "reward_xp": 15,
          "estimated_minutes": 5,
          "url": "https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html"
        }
    """.trimIndent()

    private val vidAuth = """
        {
          "content_id": "vid-auth",
          "type": "VIDEO",
          "title": "JWT tushuntirildi",
          "reward_xp": 15,
          "estimated_minutes": 3,
          "url": "https://www.youtube.com/watch?v=7Q17ubqLfaM"
        }
    """.trimIndent()

    private val fcAuth = """
        {
          "content_id": "fc-auth",
          "type": "FLASHCARD",
          "title": "Autentifikatsiya kartalari",
          "reward_xp": 25,
          "flashcards": [
            { "id": "c1", "front": "JWT imzosi nimani kafolatlaydi?", "back": "Yaxlitlik va haqiqiylik — payload o'zgartirilmagan va kalit egasi tomonidan berilgan." },
            { "id": "c2", "front": "Nega kirgandan keyin session id yangilanadi?", "back": "Session fixation hujumining oldini olish uchun — tajovuzkor qurbonning session id sini oldindan o'rnatib qo'yadi." },
            { "id": "c3", "front": "Zaiflikni toping:", "back": "MD5 — tez xesh, GPU'da buziladi. Argon2id ishlating.", "code": "val h = md5(password)", "language": "kotlin" },
            { "id": "c4", "front": "Access token va refresh token farqi?", "back": "Access = qisqa muddatli, har so'rovda. Refresh = uzoq muddatli, yangi access tokenga almashadi va ishlatilganda yangilanadi." }
          ]
        }
    """.trimIndent()

    private val quizAuth = """
        {
          "content_id": "quiz-auth",
          "type": "QUIZ",
          "title": "Autentifikatsiya testi",
          "reward_xp": 50,
          "questions": [
            {
              "id": "q-auth-1",
              "type": "SINGLE",
              "text": "2026-yilda OWASP qaysi parol xeshlash algoritmini tavsiya qiladi?",
              "duration_sec": 25,
              "options": [
                { "id": "A", "text": "MD5" },
                { "id": "B", "text": "SHA-256" },
                { "id": "C", "text": "Argon2id" },
                { "id": "D", "text": "Base64" }
              ]
            },
            {
              "id": "q-auth-2",
              "type": "MULTI",
              "text": "Xavfsiz refresh-token tizimining BARCHA xususiyatlarini tanlang.",
              "duration_sec": 35,
              "options": [
                { "id": "A", "text": "Har ishlatilganda yangilanadi" },
                { "id": "B", "text": "Oddiy SharedPreferences'da saqlanadi" },
                { "id": "C", "text": "Uzoq muddatli, ammo bekor qilinadi" },
                { "id": "D", "text": "Bitta qurilma/sessiyaga bog'langan" }
              ]
            },
            {
              "id": "q-auth-3",
              "type": "TRUE_FALSE",
              "text": "Qurilma soatini o'zgartirish server nazoratidagi test taymerini uzaytira oladi.",
              "duration_sec": 20,
              "options": [
                { "id": "T", "text": "To'g'ri" },
                { "id": "F", "text": "Noto'g'ri" }
              ]
            },
            {
              "id": "q-auth-4",
              "type": "INPUT",
              "text": "Access token muddati tugaganda server qaytaradigan HTTP holat kodini yozing.",
              "duration_sec": 25
            }
          ]
        }
    """.trimIndent()

    private val mdXss = """
        {
          "content_id": "md-xss",
          "type": "MARKDOWN",
          "title": "XSS'ni tushunish",
          "reward_xp": 20,
          "estimated_minutes": 5,
          "markdown": "# Cross-Site Scripting (XSS)\n\nXSS tajovuzkorga **o'z** JavaScript'ini **sizning** foydalanuvchingiz brauzerida ishga tushirish imkonini beradi.\n\n## Uch turi\n1. **Stored** — payload bazada saqlanadi, har bir ko'ruvchiga uzatiladi\n2. **Reflected** — payload so'rovdan qaytariladi\n3. **DOM** — sink brauzer DOM'ining o'zida\n\n## Nega Tayanch Markdown'ni nativ render qiladi\nBiz hech qachon HTML DOM'ni ishlatmaymiz, shuning uchun saqlangan `<script>` faol bo'lmagan matnga aylanadi:\n```html\n<script>fetch('/steal?c='+document.cookie)</script>\n```\nCompose Text orqali render qilinsa, yuqoridagi qator shunchaki belgilar — u ishga tusha olmaydi."
        }
    """.trimIndent()

    private val quizXss = """
        {
          "content_id": "quiz-xss",
          "type": "QUIZ",
          "title": "Inyeksiya sinov testi",
          "reward_xp": 60,
          "questions": [
            {
              "id": "q-xss-1",
              "type": "SINGLE",
              "text": "Qaysi qator so'rovni xavfsiz parametrlaydi?",
              "code": "// a) \"SELECT * FROM u WHERE id = ${'$'}id\"\n// b) prepare(\"SELECT * FROM u WHERE id = ?\").bind(id)",
              "language": "kotlin",
              "duration_sec": 30,
              "options": [
                { "id": "A", "text": "a qatori" },
                { "id": "B", "text": "b qatori" },
                { "id": "C", "text": "Ikkalasi ham xavfsiz" },
                { "id": "D", "text": "Hech biri xavfsiz emas" }
              ]
            },
            {
              "id": "q-xss-2",
              "type": "INPUT",
              "text": "< va > belgilarini render qilishdan oldin &lt; &gt; ga aylantiradigan himoya usulini nomlang (bir so'z).",
              "duration_sec": 30
            },
            {
              "id": "q-xss-3",
              "type": "TRUE_FALSE",
              "text": "Foydalanuvchi Markdown'ini nativ Compose Text orqali render qilish kiritilgan <script> tegini ishga tushira oladi.",
              "duration_sec": 20,
              "options": [
                { "id": "T", "text": "To'g'ri" },
                { "id": "F", "text": "Noto'g'ri" }
              ]
            }
          ]
        }
    """.trimIndent()

    private val quizGlobal = """
        {
          "content_id": "quiz-global",
          "type": "QUIZ",
          "title": "Umumiy takrorlash testi",
          "reward_xp": 40,
          "questions": [
            {
              "id": "q-auth-1",
              "type": "SINGLE",
              "text": "2026-yilda OWASP qaysi parol xeshlash algoritmini tavsiya qiladi?",
              "duration_sec": 20,
              "options": [
                { "id": "A", "text": "MD5" },
                { "id": "B", "text": "SHA-256" },
                { "id": "C", "text": "Argon2id" },
                { "id": "D", "text": "Base64" }
              ]
            },
            {
              "id": "q-xss-3",
              "type": "TRUE_FALSE",
              "text": "Foydalanuvchi Markdown'ini nativ Compose Text orqali render qilish kiritilgan <script> tegini ishga tushira oladi.",
              "duration_sec": 20,
              "options": [
                { "id": "T", "text": "To'g'ri" },
                { "id": "F", "text": "Noto'g'ri" }
              ]
            },
            {
              "id": "q-auth-4",
              "type": "INPUT",
              "text": "Access token muddati tugaganda server qaytaradigan HTTP holat kodini yozing.",
              "duration_sec": 25
            }
          ]
        }
    """.trimIndent()

    // Reused by the 1v1 Arena. Fast single-choice rounds.
    val battleDeckJson = """
        {
          "content_id": "battle-deck",
          "type": "QUIZ",
          "title": "Arena jangi",
          "reward_xp": 0,
          "questions": [
            {
              "id": "q-bat-1", "type": "SINGLE", "duration_sec": 15,
              "text": "Oynadagi FLAG_SECURE nimaning oldini oladi?",
              "options": [
                { "id": "A", "text": "Skrinshot va ekran yozuvi" },
                { "id": "B", "text": "SQL inyeksiya" },
                { "id": "C", "text": "Sekin internet" },
                { "id": "D", "text": "Xotira oqishi" }
              ]
            },
            {
              "id": "q-bat-2", "type": "SINGLE", "duration_sec": 15,
              "text": "Postman'ga o'xshash API suiiste'molini eng yaxshi qaysi bloklaydi?",
              "options": [
                { "id": "A", "text": "User-Agent" },
                { "id": "B", "text": "HMAC so'rov imzosi" },
                { "id": "C", "text": "Referer" },
                { "id": "D", "text": "Accept-Language" }
              ]
            },
            {
              "id": "q-bat-3", "type": "SINGLE", "duration_sec": 15,
              "text": "IDOR'ga qarshi eng yaxshi himoya?",
              "options": [
                { "id": "A", "text": "Taxmin qilinadigan butun ID lar" },
                { "id": "B", "text": "Shaxsni URL'dan emas, JWT'dan olish" },
                { "id": "C", "text": "Endpointni yashirish" },
                { "id": "D", "text": "Faqat mijoz tomonida tekshirish" }
              ]
            },
            {
              "id": "q-bat-4", "type": "SINGLE", "duration_sec": 15,
              "text": "WebSocket replay hujumlarini nima to'xtatadi?",
              "options": [
                { "id": "A", "text": "Kattaroq freymlar" },
                { "id": "B", "text": "Nonce + timestamp tekshiruvi" },
                { "id": "C", "text": "Polling" },
                { "id": "D", "text": "TLS'ni o'chirish" }
              ]
            },
            {
              "id": "q-bat-5", "type": "SINGLE", "duration_sec": 15,
              "text": "Argon2id afzal, chunki u...",
              "options": [
                { "id": "A", "text": "GPU'da tez" },
                { "id": "B", "text": "Xotira-talab" },
                { "id": "C", "text": "Teskari aylantiriladi" },
                { "id": "D", "text": "Qisqaroq natija" }
              ]
            }
          ]
        }
    """.trimIndent()

    val contentDetails: Map<String, String> = mapOf(
        "md-auth" to mdAuth,
        "web-auth" to webAuth,
        "vid-auth" to vidAuth,
        "fc-auth" to fcAuth,
        "quiz-auth" to quizAuth,
        "md-xss" to mdXss,
        "quiz-xss" to quizXss,
        "quiz-global" to quizGlobal,
        "battle-deck" to battleDeckJson,
    )

    /**
     * SERVER-SIDE answer key — never part of any client DTO. The MockEngine
     * consults it to grade a submission and returns only the verdict (Pillar 3).
     * INPUT answers accept Uzbek and English synonyms.
     */
    data class AnswerSpec(
        val correctOptionIds: Set<String> = emptySet(),
        val correctInputs: Set<String> = emptySet(),
        val explanation: String,
        val xp: Int,
    )

    val answerKey: Map<String, AnswerSpec> = mapOf(
        "q-auth-1" to AnswerSpec(setOf("C"), explanation = "Argon2id xotira-talab va hozirgi OWASP/NIST tavsiyasi.", xp = 12),
        "q-auth-2" to AnswerSpec(setOf("A", "C", "D"), explanation = "Refresh token yangilanishi, bekor qilinishi va qurilmaga bog'lanishi kerak — hech qachon oddiy prefs'da emas.", xp = 15),
        "q-auth-3" to AnswerSpec(setOf("F"), explanation = "Server elapsedRealtime/UTC tekshiruvidan foydalanadi; qurilma soati taymerni uzaytira olmaydi.", xp = 10),
        "q-auth-4" to AnswerSpec(correctInputs = setOf("401", "401 unauthorized", "unauthorized"), explanation = "401 Unauthorized — access token muddati tugagani yoki yaroqsizligini bildiradi.", xp = 13),
        "q-xss-1" to AnswerSpec(setOf("B"), explanation = "Prepared statement parametrlarni bog'laydi, shuning uchun kiritma — ma'lumot, hech qachon bajariladigan SQL emas.", xp = 15),
        "q-xss-2" to AnswerSpec(
            correctInputs = setOf("ekranlash", "kodlash", "html kodlash", "chiqish kodlash", "sanitizatsiya", "encoding", "output encoding", "html encoding", "escaping", "html escaping"),
            explanation = "Chiqishni kodlash/ekranlash < > belgilarini entity'larga aylantirib, ularni zararsiz qiladi.", xp = 15,
        ),
        "q-xss-3" to AnswerSpec(setOf("F"), explanation = "Nativ Compose Text'da DOM yo'q, shuning uchun kiritilgan skript faol bo'lmagan matn.", xp = 12),
        "q-bat-1" to AnswerSpec(setOf("A"), explanation = "FLAG_SECURE skrinshot va ekran yozuvini bloklaydi.", xp = 0),
        "q-bat-2" to AnswerSpec(setOf("B"), explanation = "HMAC imzosini ilova ichidagi sirsiz qalbakilashtirib bo'lmaydi.", xp = 0),
        "q-bat-3" to AnswerSpec(setOf("B"), explanation = "Shaxsni JWT'dan oling; URL'dagi ID'ga hech qachon ishonmang.", xp = 0),
        "q-bat-4" to AnswerSpec(setOf("B"), explanation = "Nonce + yangi timestamp takrorlangan freymlarni aniqlanadigan qiladi.", xp = 0),
        "q-bat-5" to AnswerSpec(setOf("B"), explanation = "Xotira-talablik arzon GPU brute-force hujumini yengadi.", xp = 0),
    )
}
