package uz.tayanch.app.data.mock

/**
 * "Grafik dizayn" interest — roadmap + content (Uzbek). Path based on the
 * Coursera graphic-design roadmap: fundamentals (composition & visual hierarchy,
 * color theory, typography — Level 1, fully authored), then design principles,
 * branding, and tools/portfolio (Level 2, locked).
 */
object DesignContent {

    val roadmapJson = """
        {
          "levels": [
            {
              "level_id": "gd-l1", "title": "Dizayn asoslari", "rank_label": "Junior",
              "required_xp": 0, "is_unlocked": true,
              "topics": [
                {
                  "topic_id": "gd-t-comp", "title": "1. Kompozitsiya va vizual ierarxiya", "order": 1,
                  "contents": [
                    { "content_id": "gd-md-comp", "type": "MARKDOWN", "title": "Ko'zni boshqarish", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "gd-web-comp", "type": "WEBLINK", "title": "Vizual ierarxiya (IxDF)", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 7 },
                    { "content_id": "gd-vid-comp", "type": "VIDEO", "title": "Kompozitsiya asoslari", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "gd-fc-comp", "type": "FLASHCARD", "title": "Kompozitsiya kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "gd-quiz-comp", "type": "QUIZ", "title": "Kompozitsiya testi", "reward_xp": 50, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "gd-t-color", "title": "2. Rang nazariyasi", "order": 2,
                  "contents": [
                    { "content_id": "gd-md-color", "type": "MARKDOWN", "title": "Rang g'ildiragi va uyg'unlik", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "gd-web-color", "type": "WEBLINK", "title": "Rang nazariyasi (Figma)", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 7 },
                    { "content_id": "gd-vid-color", "type": "VIDEO", "title": "Rang nazariyasi 10 daqiqada", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "gd-fc-color", "type": "FLASHCARD", "title": "Rang kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "gd-quiz-color", "type": "QUIZ", "title": "Rang testi", "reward_xp": 55, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "gd-t-type", "title": "3. Tipografika", "order": 3,
                  "contents": [
                    { "content_id": "gd-md-type", "type": "MARKDOWN", "title": "Shrift va o'qilishi", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "gd-web-type", "type": "WEBLINK", "title": "Tipografika bilimi (Google Fonts)", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 7 },
                    { "content_id": "gd-vid-type", "type": "VIDEO", "title": "Tipografika asoslari", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "gd-fc-type", "type": "FLASHCARD", "title": "Tipografika kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "gd-quiz-type", "type": "QUIZ", "title": "Tipografika testi", "reward_xp": 60, "is_completed": false, "is_locked": false }
                  ]
                }
              ],
              "assignment": { "level_id": "gd-l1", "title": "Junior bitiruv: ijtimoiy tarmoq posteri", "state": "AVAILABLE" }
            },
            {
              "level_id": "gd-l2", "title": "Amaliy dizayn", "rank_label": "Middle",
              "required_xp": 1500, "is_unlocked": false,
              "topics": [
                {
                  "topic_id": "gd-t-principles", "title": "1. Dizayn tamoyillari", "order": 1,
                  "contents": [
                    { "content_id": "gd-md-principles", "type": "MARKDOWN", "title": "Muvozanat, tekislash, takror", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 7 },
                    { "content_id": "gd-quiz-principles", "type": "QUIZ", "title": "Tamoyillar testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                },
                {
                  "topic_id": "gd-t-tools", "title": "2. Asboblar va brending", "order": 2,
                  "contents": [
                    { "content_id": "gd-md-tools", "type": "MARKDOWN", "title": "Figma, brending, portfolio", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 6 },
                    { "content_id": "gd-quiz-tools", "type": "QUIZ", "title": "Asboblar testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                }
              ],
              "assignment": { "level_id": "gd-l2", "title": "Middle bitiruv: brend uchun logo va palitra", "state": "LOCKED" }
            }
          ]
        }
    """.trimIndent()

    private val mdComp = """
        {
          "content_id": "gd-md-comp", "type": "MARKDOWN", "title": "Ko'zni boshqarish",
          "reward_xp": 20, "estimated_minutes": 5,
          "markdown": "# Kompozitsiya va vizual ierarxiya\n\nVizual ierarxiya — bu tomoshabin ko'zini kerakli tartibda yo'naltirish san'ati. U **o'lcham**, **rang**, **kontrast** va **joylashuv** orqali yaratiladi.\n\n## asosiy vositalar\n- **O'lcham** — kattaroq element birinchi e'tiborni tortadi\n- **Kontrast** — farq diqqatni jalb qiladi\n- **Bo'sh joy (whitespace)** — nafas oldiradi va muhimni ajratadi\n- **Tekislash** — tartib va ishonch hissi beradi\n\n## amaliy qoida\nHar bir dizaynda bitta aniq **fokus nuqtasi** bo'lsin. Agar hamma narsa baqirsa — hech narsa eshitilmaydi.\n\n> Yangi boshlovchining xatosi: avval effektlarga sho'ng'ish. Avval ierarxiya va joylashuvni o'rganing."
        }
    """.trimIndent()

    private val mdColor = """
        {
          "content_id": "gd-md-color", "type": "MARKDOWN", "title": "Rang g'ildiragi va uyg'unlik",
          "reward_xp": 20, "estimated_minutes": 6,
          "markdown": "# Rang nazariyasi\n\nRang kayfiyat uyg'otadi va e'tiborni boshqaradi. Asos — **rang g'ildiragi**.\n\n## uyg'unlik turlari\n- **Komplementar** — g'ildirakda qarama-qarshi ranglar (kuchli kontrast)\n- **Analog** — yonma-yon ranglar (yumshoq, hotirjam)\n- **Triada** — teng uchburchak (jonli, muvozanatli)\n\n## 60-30-10 qoidasi\n- 60% — asosiy rang\n- 30% — ikkilamchi rang\n- 10% — urg'u (accent) rang\n\n```text\nAsosiy: #1B5E4F  Ikkilamchi: #E8F0EE  Urg'u: #F6A800\n```\n\n> Kontrastni tekshiring: matn va fon o'rtasida yetarli farq bo'lmasa, o'qilmaydi (qulaylik)."
        }
    """.trimIndent()

    private val mdType = """
        {
          "content_id": "gd-md-type", "type": "MARKDOWN", "title": "Shrift va o'qilishi",
          "reward_xp": 20, "estimated_minutes": 6,
          "markdown": "# Tipografika\n\nTipografika — matnni o'qiladigan va chiroyli qilish. Yaxshi tipografika ko'rinmaydi — u shunchaki ishlaydi.\n\n## asosiy tushunchalar\n- **Serif** — oyoqchali (an'anaviy, rasmiy)\n- **Sans-serif** — oyoqchasiz (zamonaviy, toza)\n- **Ierarxiya** — sarlavha, kichik sarlavha, asosiy matn o'lchamlari aniq farq qilsin\n- **Qator oralig'i (leading)** — qulay o'qish uchun ~1.4–1.6\n\n## juftlash qoidasi\nKo'pi bilan **2 ta shrift** ishlating: bittasi sarlavha, bittasi matn uchun. Kontrast bo'lsin, lekin to'qnashmasin.\n\n> Ko'p shrift = tartibsizlik. Kam — ko'pdir."
        }
    """.trimIndent()

    private fun web(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "WEBLINK", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    private fun vid(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "VIDEO", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    private val fcComp = """
        {
          "content_id": "gd-fc-comp", "type": "FLASHCARD", "title": "Kompozitsiya kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "g1", "front": "Vizual ierarxiya nima?", "back": "Tomoshabin ko'zini o'lcham, rang va joylashuv bilan kerakli tartibda yo'naltirish." },
            { "id": "g2", "front": "Whitespace nima beradi?", "back": "Nafas oladigan joy; muhim elementlarni ajratadi va o'qishni osonlashtiradi." },
            { "id": "g3", "front": "Har dizaynda nechta fokus nuqtasi bo'lsin?", "back": "Bitta aniq fokus nuqtasi." },
            { "id": "g4", "front": "Tekislash nima beradi?", "back": "Tartib, ishonch va professional ko'rinish." }
          ]
        }
    """.trimIndent()

    private val fcColor = """
        {
          "content_id": "gd-fc-color", "type": "FLASHCARD", "title": "Rang kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "g1", "front": "Komplementar ranglar nima?", "back": "Rang g'ildiragida qarama-qarshi turgan ranglar — kuchli kontrast." },
            { "id": "g2", "front": "60-30-10 qoidasi nima?", "back": "60% asosiy, 30% ikkilamchi, 10% urg'u rang." },
            { "id": "g3", "front": "Analog ranglar qanday his uyg'otadi?", "back": "Yumshoq, hotirjam — ular yonma-yon turadi." },
            { "id": "g4", "front": "Nega kontrast muhim?", "back": "Matn va fon farqi yetarli bo'lmasa, matn o'qilmaydi (qulaylik)." }
          ]
        }
    """.trimIndent()

    private val fcType = """
        {
          "content_id": "gd-fc-type", "type": "FLASHCARD", "title": "Tipografika kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "g1", "front": "Serif va sans-serif farqi?", "back": "Serif — oyoqchali (an'anaviy); sans-serif — oyoqchasiz (zamonaviy)." },
            { "id": "g2", "front": "Bitta dizaynda nechta shrift tavsiya etiladi?", "back": "Ko'pi bilan 2 ta." },
            { "id": "g3", "front": "Leading nima?", "back": "Qatorlar orasidagi oraliq; qulay o'qish uchun ~1.4–1.6." },
            { "id": "g4", "front": "Yaxshi tipografika qanday?", "back": "Ko'rinmaydi — diqqatni tortmasdan o'qishni osonlashtiradi." }
          ]
        }
    """.trimIndent()

    private val quizComp = """
        {
          "content_id": "gd-quiz-comp", "type": "QUIZ", "title": "Kompozitsiya testi", "reward_xp": 50,
          "questions": [
            { "id": "gd-q-comp-1", "type": "SINGLE", "text": "Tomoshabin ko'zini yo'naltirish nima deyiladi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "Rang g'ildiragi" }, { "id": "B", "text": "Vizual ierarxiya" }, { "id": "C", "text": "Tipografika" }, { "id": "D", "text": "Rasterlash" } ] },
            { "id": "gd-q-comp-2", "type": "TRUE_FALSE", "text": "Bo'sh joy (whitespace) — bu dizayndagi bekor maydon, undan qochish kerak.", "duration_sec": 15,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] },
            { "id": "gd-q-comp-3", "type": "INPUT", "text": "Dizaynda nechta asosiy fokus nuqtasi bo'lishi tavsiya etiladi? (raqam)", "duration_sec": 20 }
          ]
        }
    """.trimIndent()

    private val quizColor = """
        {
          "content_id": "gd-quiz-color", "type": "QUIZ", "title": "Rang testi", "reward_xp": 55,
          "questions": [
            { "id": "gd-q-color-1", "type": "SINGLE", "text": "Rang g'ildiragida qarama-qarshi ranglar qanday ataladi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "Analog" }, { "id": "B", "text": "Komplementar" }, { "id": "C", "text": "Monoxrom" }, { "id": "D", "text": "Triada" } ] },
            { "id": "gd-q-color-2", "type": "INPUT", "text": "Mashhur rang nisbati qoidasini yozing (masalan 60-30-10).", "duration_sec": 25 },
            { "id": "gd-q-color-3", "type": "TRUE_FALSE", "text": "Matn va fon o'rtasidagi past kontrast o'qishni qiyinlashtiradi.", "duration_sec": 15,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] }
          ]
        }
    """.trimIndent()

    private val quizType = """
        {
          "content_id": "gd-quiz-type", "type": "QUIZ", "title": "Tipografika testi", "reward_xp": 60,
          "questions": [
            { "id": "gd-q-type-1", "type": "SINGLE", "text": "Oyoqchasiz shrift turi qanday ataladi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "Serif" }, { "id": "B", "text": "Sans-serif" }, { "id": "C", "text": "Monospace" }, { "id": "D", "text": "Script" } ] },
            { "id": "gd-q-type-2", "type": "SINGLE", "text": "Bitta dizaynda nechta shrift tavsiya etiladi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "Ko'pi bilan 2" }, { "id": "B", "text": "Kamida 5" }, { "id": "C", "text": "Har bo'lim uchun yangi" }, { "id": "D", "text": "Cheklov yo'q" } ] },
            { "id": "gd-q-type-3", "type": "INPUT", "text": "Oyoqchali (an'anaviy) shrift oilasini bir so'z bilan nomlang.", "duration_sec": 20 }
          ]
        }
    """.trimIndent()

    val contentDetails: Map<String, String> = mapOf(
        "gd-md-comp" to mdComp,
        "gd-web-comp" to web("gd-web-comp", "Vizual ierarxiya (IxDF)", 15, 7, "https://www.interaction-design.org/literature/topics/visual-hierarchy"),
        "gd-vid-comp" to vid("gd-vid-comp", "Vizual ierarxiya", 15, 6, "https://www.youtube.com/watch?v=DBHBmeNhYrY"),
        "gd-fc-comp" to fcComp,
        "gd-quiz-comp" to quizComp,
        "gd-md-color" to mdColor,
        "gd-web-color" to web("gd-web-color", "Rang nazariyasi (Figma)", 15, 7, "https://www.figma.com/resource-library/color-theory/"),
        "gd-vid-color" to vid("gd-vid-color", "Rang nazariyasi 5 daqiqada", 15, 6, "https://www.youtube.com/watch?v=A1vcOIlzS1Q"),
        "gd-fc-color" to fcColor,
        "gd-quiz-color" to quizColor,
        "gd-md-type" to mdType,
        "gd-web-type" to web("gd-web-type", "Tipografika bilimi (Google Fonts)", 15, 7, "https://fonts.google.com/knowledge"),
        "gd-vid-type" to vid("gd-vid-type", "Tipografika asoslari", 15, 6, "https://www.youtube.com/watch?v=d94QRzDyfFM"),
        "gd-fc-type" to fcType,
        "gd-quiz-type" to quizType,
    )

    val answerKey: Map<String, AnswerSpec> = mapOf(
        "gd-q-comp-1" to AnswerSpec(setOf("B"), explanation = "Vizual ierarxiya ko'zni yo'naltiradi.", xp = 12),
        "gd-q-comp-2" to AnswerSpec(setOf("F"), explanation = "Whitespace — qimmatli vosita; u muhimni ajratadi va o'qishni osonlashtiradi.", xp = 10),
        "gd-q-comp-3" to AnswerSpec(correctInputs = setOf("1", "bitta", "bir"), explanation = "Bitta aniq fokus nuqtasi tavsiya etiladi.", xp = 13),
        "gd-q-color-1" to AnswerSpec(setOf("B"), explanation = "Qarama-qarshi ranglar — komplementar.", xp = 12),
        "gd-q-color-2" to AnswerSpec(correctInputs = setOf("60-30-10", "60 30 10", "603010"), explanation = "60-30-10 — asosiy, ikkilamchi, urg'u rang nisbati.", xp = 15),
        "gd-q-color-3" to AnswerSpec(setOf("T"), explanation = "Past kontrast qulaylikni buzadi.", xp = 10),
        "gd-q-type-1" to AnswerSpec(setOf("B"), explanation = "Oyoqchasiz shrift — sans-serif.", xp = 12),
        "gd-q-type-2" to AnswerSpec(setOf("A"), explanation = "Ko'pi bilan 2 ta shrift — toza va tartibli.", xp = 12),
        "gd-q-type-3" to AnswerSpec(correctInputs = setOf("serif"), explanation = "Serif — oyoqchali an'anaviy shrift.", xp = 13),
    )

    val samplePool: List<String> = listOf(
        """{ "id": "gd-q-color-1", "type": "SINGLE", "duration_sec": 15, "text": "Qarama-qarshi ranglar qanday ataladi?", "options": [ { "id": "A", "text": "Analog" }, { "id": "B", "text": "Komplementar" }, { "id": "C", "text": "Monoxrom" }, { "id": "D", "text": "Triada" } ] }""",
        """{ "id": "gd-q-type-1", "type": "SINGLE", "duration_sec": 15, "text": "Oyoqchasiz shrift turi?", "options": [ { "id": "A", "text": "Serif" }, { "id": "B", "text": "Sans-serif" }, { "id": "C", "text": "Monospace" }, { "id": "D", "text": "Script" } ] }""",
        """{ "id": "gd-q-comp-1", "type": "SINGLE", "duration_sec": 15, "text": "Ko'zni yo'naltirish nima deyiladi?", "options": [ { "id": "A", "text": "Rang g'ildiragi" }, { "id": "B", "text": "Vizual ierarxiya" }, { "id": "C", "text": "Tipografika" }, { "id": "D", "text": "Rasterlash" } ] }""",
    )
}
