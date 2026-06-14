package uz.tayanch.app.data.mock

/**
 * "Kiberxavfsizlik" interest — roadmap + content (Uzbek). Expanded from the
 * original two shallow topics to three fully-authored Level-1 topics
 * (authentication, injection, access control / IDOR), then cryptography &
 * transport at Level 2 (locked). Its [samplePool] (the classic FLAG_SECURE /
 * HMAC / IDOR / replay / Argon2 questions) also feeds the Arena + global quiz.
 */
object CyberContent {

    val roadmapJson = """
        {
          "levels": [
            {
              "level_id": "cy-l1", "title": "Junior AppSec", "rank_label": "Junior",
              "required_xp": 0, "is_unlocked": true,
              "topics": [
                {
                  "topic_id": "cy-t-auth", "title": "1. Autentifikatsiya kamchiliklari", "order": 1,
                  "contents": [
                    { "content_id": "md-auth", "type": "MARKDOWN", "title": "Buzilgan autentifikatsiya", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 4 },
                    { "content_id": "web-auth", "type": "WEBLINK", "title": "OWASP autentifikatsiya qo'llanmasi", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "vid-auth", "type": "VIDEO", "title": "JWT tushuntirildi", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 3 },
                    { "content_id": "fc-auth", "type": "FLASHCARD", "title": "Autentifikatsiya kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "quiz-auth", "type": "QUIZ", "title": "Autentifikatsiya testi", "reward_xp": 50, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "cy-t-inj", "title": "2. Inyeksiya (SQLi / XSS)", "order": 2,
                  "contents": [
                    { "content_id": "md-xss", "type": "MARKDOWN", "title": "XSS'ni tushunish", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "web-xss", "type": "WEBLINK", "title": "PortSwigger: XSS", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "vid-xss", "type": "VIDEO", "title": "SQL Injection namoyishi", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 4 },
                    { "content_id": "fc-xss", "type": "FLASHCARD", "title": "Inyeksiya kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "quiz-xss", "type": "QUIZ", "title": "Inyeksiya sinov testi", "reward_xp": 60, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "cy-t-idor", "title": "3. Kirish nazorati va IDOR", "order": 3,
                  "contents": [
                    { "content_id": "md-idor", "type": "MARKDOWN", "title": "Buzilgan kirish nazorati", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "web-idor", "type": "WEBLINK", "title": "OWASP: kirish nazorati", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "vid-idor", "type": "VIDEO", "title": "IDOR tushuntirildi", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "fc-idor", "type": "FLASHCARD", "title": "IDOR kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "quiz-idor", "type": "QUIZ", "title": "Kirish nazorati testi", "reward_xp": 60, "is_completed": false, "is_locked": false }
                  ]
                }
              ],
              "assignment": { "level_id": "cy-l1", "title": "Junior bitiruv: login API'ni mustahkamlash", "state": "AVAILABLE" }
            },
            {
              "level_id": "cy-l2", "title": "Middle AppSec", "rank_label": "Middle",
              "required_xp": 1500, "is_unlocked": false,
              "topics": [
                {
                  "topic_id": "cy-t-crypto", "title": "1. Amaliy kriptografiya", "order": 1,
                  "contents": [
                    { "content_id": "md-crypto", "type": "MARKDOWN", "title": "Argon2id va tuzlar", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 6 },
                    { "content_id": "quiz-crypto", "type": "QUIZ", "title": "Kriptografiya testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                },
                {
                  "topic_id": "cy-t-transport", "title": "2. Xavfsiz transport", "order": 2,
                  "contents": [
                    { "content_id": "md-transport", "type": "MARKDOWN", "title": "TLS 1.3 va sertifikat pinning", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 6 },
                    { "content_id": "quiz-transport", "type": "QUIZ", "title": "Transport testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                }
              ],
              "assignment": { "level_id": "cy-l2", "title": "Middle bitiruv: Zero-Trust shlyuzi", "state": "LOCKED" }
            }
          ]
        }
    """.trimIndent()

    private val mdAuth = """
        {
          "content_id": "md-auth", "type": "MARKDOWN", "title": "Buzilgan autentifikatsiya",
          "reward_xp": 20, "estimated_minutes": 4,
          "markdown": "# Buzilgan autentifikatsiya\n\nAutentifikatsiya foydalanuvchi **kimligini** tasdiqlaydi. U buzilganda tajovuzkor hisoblarni egallab olishi mumkin.\n\n## Keng tarqalgan sabablar\n- Rate limit yo'q endpointlarga credential stuffing hujumi\n- Kirgandan keyin yangilanmaydigan session ID lar\n- Parollarni tez xeshlar (MD5/SHA-1) bilan saqlash\n\n## Xavfsizroq xesh\n```kotlin\nval hash = Argon2id.hash(password, salt)\n// xotira-talab: GPU uni arzonga buza olmaydi\n```\n\n> Qoida: hech qachon o'z kriptografiyangizni yozmang va imtiyoz o'zgarganda session id ni yangilang."
        }
    """.trimIndent()

    private val fcAuth = """
        {
          "content_id": "fc-auth", "type": "FLASHCARD", "title": "Autentifikatsiya kartalari", "reward_xp": 25,
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
          "content_id": "quiz-auth", "type": "QUIZ", "title": "Autentifikatsiya testi", "reward_xp": 50,
          "questions": [
            { "id": "q-auth-1", "type": "SINGLE", "text": "2026-yilda OWASP qaysi parol xeshlash algoritmini tavsiya qiladi?", "duration_sec": 25,
              "options": [ { "id": "A", "text": "MD5" }, { "id": "B", "text": "SHA-256" }, { "id": "C", "text": "Argon2id" }, { "id": "D", "text": "Base64" } ] },
            { "id": "q-auth-2", "type": "MULTI", "text": "Xavfsiz refresh-token tizimining BARCHA xususiyatlarini tanlang.", "duration_sec": 35,
              "options": [ { "id": "A", "text": "Har ishlatilganda yangilanadi" }, { "id": "B", "text": "Oddiy SharedPreferences'da saqlanadi" }, { "id": "C", "text": "Uzoq muddatli, ammo bekor qilinadi" }, { "id": "D", "text": "Bitta qurilma/sessiyaga bog'langan" } ] },
            { "id": "q-auth-3", "type": "TRUE_FALSE", "text": "Qurilma soatini o'zgartirish server nazoratidagi test taymerini uzaytira oladi.", "duration_sec": 20,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] },
            { "id": "q-auth-4", "type": "INPUT", "text": "Access token muddati tugaganda server qaytaradigan HTTP holat kodini yozing.", "duration_sec": 25 }
          ]
        }
    """.trimIndent()

    private val mdXss = """
        {
          "content_id": "md-xss", "type": "MARKDOWN", "title": "XSS'ni tushunish",
          "reward_xp": 20, "estimated_minutes": 5,
          "markdown": "# Cross-Site Scripting (XSS)\n\nXSS tajovuzkorga **o'z** JavaScript'ini **sizning** foydalanuvchingiz brauzerida ishga tushirish imkonini beradi.\n\n## Uch turi\n1. **Stored** — payload bazada saqlanadi, har bir ko'ruvchiga uzatiladi\n2. **Reflected** — payload so'rovdan qaytariladi\n3. **DOM** — sink brauzer DOM'ining o'zida\n\n## Nega Tayanch Markdown'ni nativ render qiladi\nBiz hech qachon HTML DOM'ni ishlatmaymiz, shuning uchun saqlangan `<script>` faol bo'lmagan matnga aylanadi:\n```html\n<script>fetch('/steal?c='+document.cookie)</script>\n```\nCompose Text orqali render qilinsa, yuqoridagi qator shunchaki belgilar — u ishga tusha olmaydi."
        }
    """.trimIndent()

    private val fcXss = """
        {
          "content_id": "fc-xss", "type": "FLASHCARD", "title": "Inyeksiya kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "x1", "front": "SQLi'ning eng yaxshi himoyasi?", "back": "Parametrlangan so'rovlar (prepared statements) — kiritma hech qachon SQL sifatida bajarilmaydi." },
            { "id": "x2", "front": "Stored va Reflected XSS farqi?", "back": "Stored — payload bazada saqlanadi; Reflected — so'rovdan darhol qaytariladi." },
            { "id": "x3", "front": "Bu kod nega xavfli?", "back": "Foydalanuvchi kiritmasi to'g'ridan-to'g'ri so'rovga ulanmoqda — SQL injeksiyasi. Bog'lash (?) ishlating.", "code": "\"SELECT * FROM u WHERE id=\" + id", "language": "kotlin" },
            { "id": "x4", "front": "Chiqishni kodlash (output encoding) nima qiladi?", "back": "< > belgilarini &lt; &gt; ga aylantirib, brauzer ularni teg emas, matn deb biladi." }
          ]
        }
    """.trimIndent()

    private val quizXss = """
        {
          "content_id": "quiz-xss", "type": "QUIZ", "title": "Inyeksiya sinov testi", "reward_xp": 60,
          "questions": [
            { "id": "q-xss-1", "type": "SINGLE", "text": "Qaysi qator so'rovni xavfsiz parametrlaydi?",
              "code": "// a) \"SELECT * FROM u WHERE id = \" + id\n// b) prepare(\"SELECT * FROM u WHERE id = ?\").bind(id)",
              "language": "kotlin", "duration_sec": 30,
              "options": [ { "id": "A", "text": "a qatori" }, { "id": "B", "text": "b qatori" }, { "id": "C", "text": "Ikkalasi ham xavfsiz" }, { "id": "D", "text": "Hech biri xavfsiz emas" } ] },
            { "id": "q-xss-2", "type": "INPUT", "text": "< va > belgilarini render qilishdan oldin &lt; &gt; ga aylantiradigan himoya usulini nomlang (bir so'z).", "duration_sec": 30 },
            { "id": "q-xss-3", "type": "TRUE_FALSE", "text": "Foydalanuvchi Markdown'ini nativ Compose Text orqali render qilish kiritilgan <script> tegini ishga tushira oladi.", "duration_sec": 20,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] }
          ]
        }
    """.trimIndent()

    private val mdIdor = """
        {
          "content_id": "md-idor", "type": "MARKDOWN", "title": "Buzilgan kirish nazorati",
          "reward_xp": 20, "estimated_minutes": 5,
          "markdown": "# Buzilgan kirish nazorati va IDOR\n\nKirish nazorati foydalanuvchi **nima qila olishini** belgilaydi. Buzilganda foydalanuvchi o'zganing ma'lumotiga yetadi.\n\n## IDOR (Insecure Direct Object Reference)\nServer kimligini tekshirmasdan URL'dagi id'ga ishonsa:\n```http\nGET /api/users/123   ->  124 ga o'zgartiring va boshqaning profili ochiladi\n```\n\n## To'g'ri yondashuv\n- Shaxsni **JWT/sessiyadan** oling, URL'dan emas\n- `GET /users/me` — server tokendan id ni aniqlaydi\n- Har so'rovda **avtorizatsiyani serverda** tekshiring\n\n> Tayanch shu sababli faqat `/users/me` ishlatadi va taxmin qilinadigan id'larga ishonmaydi (Pillar 4 — IDOR himoyasi)."
        }
    """.trimIndent()

    private val fcIdor = """
        {
          "content_id": "fc-idor", "type": "FLASHCARD", "title": "IDOR kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "i1", "front": "IDOR nima?", "back": "Server kimlikni tekshirmay URL'dagi obyekt id'siga ishonganda yuzaga keladigan kirish nazorati kamchiligi." },
            { "id": "i2", "front": "IDOR'ning eng yaxshi himoyasi?", "back": "Shaxsni JWT/sessiyadan olish va serverda avtorizatsiyani tekshirish; /users/me kabi." },
            { "id": "i3", "front": "Nega taxmin qilinadigan id'lar xavfli?", "back": "Tajovuzkor id'ni oshirib (123->124) boshqaning resursiga yetishi mumkin." },
            { "id": "i4", "front": "Faqat mijoz tomonida tekshiruv yetarlimi?", "back": "Yo'q — barcha avtorizatsiya serverda majburiy bajarilishi kerak." }
          ]
        }
    """.trimIndent()

    private val quizIdor = """
        {
          "content_id": "quiz-idor", "type": "QUIZ", "title": "Kirish nazorati testi", "reward_xp": 60,
          "questions": [
            { "id": "q-idor-1", "type": "SINGLE", "text": "IDOR'ga qarshi eng yaxshi himoya qaysi?", "duration_sec": 25,
              "options": [ { "id": "A", "text": "Endpointni yashirish" }, { "id": "B", "text": "Shaxsni JWT'dan olish, URL'dan emas" }, { "id": "C", "text": "Taxmin qilinadigan id'lar" }, { "id": "D", "text": "Faqat mijozda tekshirish" } ] },
            { "id": "q-idor-2", "type": "TRUE_FALSE", "text": "Avtorizatsiyani faqat mijoz (client) tomonida tekshirish yetarli.", "duration_sec": 20,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] },
            { "id": "q-idor-3", "type": "INPUT", "text": "Joriy foydalanuvchini tokendan aniqlaydigan xavfsiz endpoint yo'lini yozing (masalan /users/me).", "duration_sec": 25 }
          ]
        }
    """.trimIndent()

    private fun web(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "WEBLINK", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    private fun vid(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "VIDEO", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    val contentDetails: Map<String, String> = mapOf(
        "md-auth" to mdAuth,
        "web-auth" to web("web-auth", "OWASP autentifikatsiya qo'llanmasi", 15, 5, "https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html"),
        "vid-auth" to vid("vid-auth", "JWT tushuntirildi", 15, 3, "https://www.youtube.com/watch?v=7Q17ubqLfaM"),
        "fc-auth" to fcAuth,
        "quiz-auth" to quizAuth,
        "md-xss" to mdXss,
        "web-xss" to web("web-xss", "PortSwigger: XSS", 15, 6, "https://portswigger.net/web-security/cross-site-scripting"),
        "vid-xss" to vid("vid-xss", "SQL Injection namoyishi", 15, 4, "https://www.youtube.com/watch?v=ciNHn38EyRc"),
        "fc-xss" to fcXss,
        "quiz-xss" to quizXss,
        "md-idor" to mdIdor,
        "web-idor" to web("web-idor", "OWASP: kirish nazorati", 15, 6, "https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html"),
        "vid-idor" to vid("vid-idor", "IDOR tushuntirildi", 15, 5, "https://www.youtube.com/watch?v=SfXgvtFHzEE"),
        "fc-idor" to fcIdor,
        "quiz-idor" to quizIdor,
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
        "q-idor-1" to AnswerSpec(setOf("B"), explanation = "Shaxsni JWT'dan oling; URL'dagi id'ga hech qachon ishonmang.", xp = 13),
        "q-idor-2" to AnswerSpec(setOf("F"), explanation = "Avtorizatsiya serverda majburiy tekshirilishi kerak; faqat mijoz tekshiruvi yetarli emas.", xp = 10),
        "q-idor-3" to AnswerSpec(correctInputs = setOf("/users/me", "users/me", "/user/me"), explanation = "/users/me — shaxs tokendan aniqlanadi, URL'dagi id'dan emas.", xp = 13),
        // Classic security questions — feed the Arena + global quiz.
        "q-bat-1" to AnswerSpec(setOf("A"), explanation = "FLAG_SECURE skrinshot va ekran yozuvini bloklaydi.", xp = 0),
        "q-bat-2" to AnswerSpec(setOf("B"), explanation = "HMAC imzosini ilova ichidagi sirsiz qalbakilashtirib bo'lmaydi.", xp = 0),
        "q-bat-3" to AnswerSpec(setOf("B"), explanation = "Shaxsni JWT'dan oling; URL'dagi ID'ga hech qachon ishonmang.", xp = 0),
        "q-bat-4" to AnswerSpec(setOf("B"), explanation = "Nonce + yangi timestamp takrorlangan freymlarni aniqlanadigan qiladi.", xp = 0),
        "q-bat-5" to AnswerSpec(setOf("B"), explanation = "Xotira-talablik arzon GPU brute-force hujumini yengadi.", xp = 0),
    )

    val samplePool: List<String> = listOf(
        """{ "id": "q-bat-1", "type": "SINGLE", "duration_sec": 15, "text": "Oynadagi FLAG_SECURE nimaning oldini oladi?", "options": [ { "id": "A", "text": "Skrinshot va ekran yozuvi" }, { "id": "B", "text": "SQL inyeksiya" }, { "id": "C", "text": "Sekin internet" }, { "id": "D", "text": "Xotira oqishi" } ] }""",
        """{ "id": "q-bat-2", "type": "SINGLE", "duration_sec": 15, "text": "Postman'ga o'xshash API suiiste'molini eng yaxshi qaysi bloklaydi?", "options": [ { "id": "A", "text": "User-Agent" }, { "id": "B", "text": "HMAC so'rov imzosi" }, { "id": "C", "text": "Referer" }, { "id": "D", "text": "Accept-Language" } ] }""",
        """{ "id": "q-bat-3", "type": "SINGLE", "duration_sec": 15, "text": "IDOR'ga qarshi eng yaxshi himoya?", "options": [ { "id": "A", "text": "Taxmin qilinadigan ID lar" }, { "id": "B", "text": "Shaxsni URL'dan emas, JWT'dan olish" }, { "id": "C", "text": "Endpointni yashirish" }, { "id": "D", "text": "Faqat mijoz tomonida tekshirish" } ] }""",
        """{ "id": "q-bat-4", "type": "SINGLE", "duration_sec": 15, "text": "WebSocket replay hujumlarini nima to'xtatadi?", "options": [ { "id": "A", "text": "Kattaroq freymlar" }, { "id": "B", "text": "Nonce + timestamp tekshiruvi" }, { "id": "C", "text": "Polling" }, { "id": "D", "text": "TLS'ni o'chirish" } ] }""",
        """{ "id": "q-bat-5", "type": "SINGLE", "duration_sec": 15, "text": "Argon2id afzal, chunki u...", "options": [ { "id": "A", "text": "GPU'da tez" }, { "id": "B", "text": "Xotira-talab" }, { "id": "C", "text": "Teskari aylantiriladi" }, { "id": "D", "text": "Qisqaroq natija" } ] }""",
    )
}
