package uz.tayanch.app.data.mock

/**
 * "Python dasturlash" interest — roadmap + content (Uzbek). Path based on the
 * Scaler / roadmap.sh Python developer roadmap: basics → control flow & functions
 * → data structures (Level 1, fully authored), then OOP, modules/venv, files &
 * errors, APIs/JSON (Level 2, locked).
 */
object PythonContent {

    val roadmapJson = """
        {
          "levels": [
            {
              "level_id": "py-l1", "title": "Boshlang'ich Python", "rank_label": "Junior",
              "required_xp": 0, "is_unlocked": true,
              "topics": [
                {
                  "topic_id": "py-t-syntax", "title": "1. Sintaksis va ma'lumot turlari", "order": 1,
                  "contents": [
                    { "content_id": "py-md-syntax", "type": "MARKDOWN", "title": "O'zgaruvchilar va turlar", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "py-web-syntax", "type": "WEBLINK", "title": "Python rasmiy qo'llanma", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "py-vid-syntax", "type": "VIDEO", "title": "Python 5 daqiqada", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "py-fc-syntax", "type": "FLASHCARD", "title": "Turlar kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "py-quiz-syntax", "type": "QUIZ", "title": "Sintaksis testi", "reward_xp": 50, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "py-t-flow", "title": "2. Boshqaruv oqimi va funksiyalar", "order": 2,
                  "contents": [
                    { "content_id": "py-md-flow", "type": "MARKDOWN", "title": "if, sikllar, funksiyalar", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "py-web-flow", "type": "WEBLINK", "title": "Real Python: funksiyalar", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 7 },
                    { "content_id": "py-vid-flow", "type": "VIDEO", "title": "Funksiyalar tushuntirildi", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "py-fc-flow", "type": "FLASHCARD", "title": "Oqim kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "py-quiz-flow", "type": "QUIZ", "title": "Oqim testi", "reward_xp": 55, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "py-t-ds", "title": "3. Ma'lumotlar tuzilmalari", "order": 3,
                  "contents": [
                    { "content_id": "py-md-ds", "type": "MARKDOWN", "title": "list, dict, set, tuple", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "py-web-ds", "type": "WEBLINK", "title": "Python: ma'lumotlar tuzilmalari", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 7 },
                    { "content_id": "py-vid-ds", "type": "VIDEO", "title": "list vs dict", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "py-fc-ds", "type": "FLASHCARD", "title": "Tuzilma kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "py-quiz-ds", "type": "QUIZ", "title": "Tuzilmalar testi", "reward_xp": 60, "is_completed": false, "is_locked": false }
                  ]
                }
              ],
              "assignment": { "level_id": "py-l1", "title": "Junior bitiruv: CLI kalkulyator yozish", "state": "AVAILABLE" }
            },
            {
              "level_id": "py-l2", "title": "O'rta Python", "rank_label": "Middle",
              "required_xp": 1500, "is_unlocked": false,
              "topics": [
                {
                  "topic_id": "py-t-oop", "title": "1. OOP: sinflar va obyektlar", "order": 1,
                  "contents": [
                    { "content_id": "py-md-oop", "type": "MARKDOWN", "title": "Sinflar, meros, polimorfizm", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 7 },
                    { "content_id": "py-quiz-oop", "type": "QUIZ", "title": "OOP testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                },
                {
                  "topic_id": "py-t-env", "title": "2. Modullar, venv va pip", "order": 2,
                  "contents": [
                    { "content_id": "py-md-env", "type": "MARKDOWN", "title": "Paketlar va virtual muhit", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 6 },
                    { "content_id": "py-quiz-env", "type": "QUIZ", "title": "Muhit testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                }
              ],
              "assignment": { "level_id": "py-l2", "title": "Middle bitiruv: REST API (FastAPI)", "state": "LOCKED" }
            }
          ]
        }
    """.trimIndent()

    private val mdSyntax = """
        {
          "content_id": "py-md-syntax", "type": "MARKDOWN", "title": "O'zgaruvchilar va turlar",
          "reward_xp": 20, "estimated_minutes": 5,
          "markdown": "# O'zgaruvchilar va ma'lumot turlari\n\nPython'da o'zgaruvchini e'lon qilish uchun tur yozish shart emas — qiymat turni belgilaydi (dinamik turlash).\n\n## Asosiy turlar\n- **int** — butun son: 42\n- **float** — kasr son: 3.14\n- **str** — matn: \"salom\"\n- **bool** — mantiqiy: True / False\n\n```python\nname = \"Diyor\"      # str\nage = 23            # int\nis_student = True   # bool\nprint(type(age))    # <class 'int'>\n```\n\n## f-string bilan formatlash\n```python\nprint(f\"{name} {age} yoshda\")\n```\n\n> Maslahat: o'zgaruvchi nomlari kichik harf va pastki chiziq bilan (snake_case)."
        }
    """.trimIndent()

    private val mdFlow = """
        {
          "content_id": "py-md-flow", "type": "MARKDOWN", "title": "if, sikllar, funksiyalar",
          "reward_xp": 20, "estimated_minutes": 6,
          "markdown": "# Boshqaruv oqimi va funksiyalar\n\nDastur qarorlarni `if` orqali qabul qiladi va takrorlanishni sikllar bajaradi. Python bloklarni **otstep (indentation)** bilan ajratadi — qavs emas.\n\n## Shartlar\n```python\nif age >= 18:\n    print(\"Voyaga yetgan\")\nelse:\n    print(\"Yosh\")\n```\n\n## Sikllar\n```python\nfor i in range(3):\n    print(i)        # 0 1 2\n```\n\n## Funksiyalar\n```python\ndef salomlash(ism):\n    return f\"Salom, {ism}!\"\n\nprint(salomlash(\"Olim\"))\n```\n\n> Funksiya bitta vazifani bajarsin va aniq nom bilan atalsin."
        }
    """.trimIndent()

    private val mdDs = """
        {
          "content_id": "py-md-ds", "type": "MARKDOWN", "title": "list, dict, set, tuple",
          "reward_xp": 20, "estimated_minutes": 6,
          "markdown": "# Ma'lumotlar tuzilmalari\n\nTo'g'ri tuzilmani tanlash kodni tez va o'qiladigan qiladi.\n\n| Tuzilma | Tartibli | O'zgaruvchan | Takror |\n|---|---|---|---|\n| list | ha | ha | ha |\n| tuple | ha | yo'q | ha |\n| set | yo'q | ha | yo'q |\n| dict | ha | ha | kalit noyob |\n\n```python\nskills = [\"python\", \"git\"]   # list\npoint = (10, 20)             # tuple\nunique = {1, 2, 2}           # set -> {1, 2}\nuser = {\"name\": \"Aziz\"}     # dict\nuser[\"age\"] = 24\n```\n\n> Noyob elementlar kerakmi? `set`. Kalit-qiymat kerakmi? `dict`."
        }
    """.trimIndent()

    private fun web(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "WEBLINK", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    private fun vid(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "VIDEO", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    private val fcSyntax = """
        {
          "content_id": "py-fc-syntax", "type": "FLASHCARD", "title": "Turlar kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "p1", "front": "type(3.0) nima qaytaradi?", "back": "<class 'float'> — kasr son turi." },
            { "id": "p2", "front": "Dinamik turlash nima?", "back": "O'zgaruvchi turi ish vaqtida qiymatga qarab aniqlanadi; e'londa tur yozilmaydi." },
            { "id": "p3", "front": "Natijani toping:", "back": "'33' — + operatori matnlarni ulaydi, qo'shmaydi.", "code": "print('3' + '3')", "language": "python" },
            { "id": "p4", "front": "f-string nima uchun?", "back": "Matn ichiga o'zgaruvchi qiymatini qulay joylash: f\"{ism}\"." }
          ]
        }
    """.trimIndent()

    private val fcFlow = """
        {
          "content_id": "py-fc-flow", "type": "FLASHCARD", "title": "Oqim kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "p1", "front": "Python bloklarni qanday ajratadi?", "back": "Otstep (indentation) — odatda 4 probel. Qavs ishlatilmaydi." },
            { "id": "p2", "front": "range(1, 4) nimani beradi?", "back": "1, 2, 3 — oxirgi son (4) kirmaydi." },
            { "id": "p3", "front": "return bo'lmasa funksiya nima qaytaradi?", "back": "None." },
            { "id": "p4", "front": "while sikli qachon to'xtaydi?", "back": "Sharti False bo'lganda yoki break bajarilganda." }
          ]
        }
    """.trimIndent()

    private val fcDs = """
        {
          "content_id": "py-fc-ds", "type": "FLASHCARD", "title": "Tuzilma kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "p1", "front": "Qaysi tuzilma o'zgartirib bo'lmaydi?", "back": "tuple — yaratilgandan keyin o'zgarmaydi (immutable)." },
            { "id": "p2", "front": "set nimaga foydali?", "back": "Takrorlanmas elementlar va tez a'zolik tekshiruvi." },
            { "id": "p3", "front": "dict'dan qiymat olish:", "back": "user['name'] yoki user.get('name').", "code": "user = {'name': 'Aziz'}", "language": "python" },
            { "id": "p4", "front": "list va tuple farqi?", "back": "list o'zgaruvchan ([]), tuple o'zgarmas (())." }
          ]
        }
    """.trimIndent()

    private val quizSyntax = """
        {
          "content_id": "py-quiz-syntax", "type": "QUIZ", "title": "Sintaksis testi", "reward_xp": 50,
          "questions": [
            { "id": "py-q-syntax-1", "type": "SINGLE", "text": "type(42) nima qaytaradi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "<class 'str'>" }, { "id": "B", "text": "<class 'int'>" }, { "id": "C", "text": "<class 'float'>" }, { "id": "D", "text": "<class 'bool'>" } ] },
            { "id": "py-q-syntax-2", "type": "INPUT", "text": "'3' + '3' ifodasining natijasini yozing.", "duration_sec": 25 },
            { "id": "py-q-syntax-3", "type": "TRUE_FALSE", "text": "Python'da o'zgaruvchini e'lon qilishda turini yozish shart.", "duration_sec": 15,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] }
          ]
        }
    """.trimIndent()

    private val quizFlow = """
        {
          "content_id": "py-quiz-flow", "type": "QUIZ", "title": "Oqim testi", "reward_xp": 55,
          "questions": [
            { "id": "py-q-flow-1", "type": "SINGLE", "text": "range(3) sikli necha marta aylanadi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "2" }, { "id": "B", "text": "3" }, { "id": "C", "text": "4" }, { "id": "D", "text": "Cheksiz" } ] },
            { "id": "py-q-flow-2", "type": "SINGLE", "text": "return yozilmagan funksiya nimani qaytaradi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "0" }, { "id": "B", "text": "None" }, { "id": "C", "text": "'' " }, { "id": "D", "text": "Xatolik" } ] },
            { "id": "py-q-flow-3", "type": "INPUT", "text": "Python bloklarni ajratish uchun ishlatiladigan usulni bir so'z bilan yozing.", "duration_sec": 25 }
          ]
        }
    """.trimIndent()

    private val quizDs = """
        {
          "content_id": "py-quiz-ds", "type": "QUIZ", "title": "Tuzilmalar testi", "reward_xp": 60,
          "questions": [
            { "id": "py-q-ds-1", "type": "SINGLE", "text": "Qaysi tuzilma o'zgarmas (immutable)?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "list" }, { "id": "B", "text": "dict" }, { "id": "C", "text": "set" }, { "id": "D", "text": "tuple" } ] },
            { "id": "py-q-ds-2", "type": "MULTI", "text": "set tuzilmasining BARCHA to'g'ri xususiyatlarini tanlang.", "duration_sec": 30,
              "options": [ { "id": "A", "text": "Takrorlanmas elementlar" }, { "id": "B", "text": "Tartiblangan indeks" }, { "id": "C", "text": "Tez a'zolik tekshiruvi" }, { "id": "D", "text": "Kalit-qiymat juftligi" } ] },
            { "id": "py-q-ds-3", "type": "INPUT", "text": "{1, 2, 2, 3} to'plamida nechta element bo'ladi? (raqam)", "duration_sec": 20 }
          ]
        }
    """.trimIndent()

    val contentDetails: Map<String, String> = mapOf(
        "py-md-syntax" to mdSyntax,
        "py-web-syntax" to web("py-web-syntax", "Python rasmiy qo'llanma", 15, 6, "https://docs.python.org/3/tutorial/introduction.html"),
        "py-vid-syntax" to vid("py-vid-syntax", "O'zgaruvchilar va turlar", 15, 5, "https://www.youtube.com/watch?v=cQT33yu9pY8"),
        "py-fc-syntax" to fcSyntax,
        "py-quiz-syntax" to quizSyntax,
        "py-md-flow" to mdFlow,
        "py-web-flow" to web("py-web-flow", "Real Python: funksiyalar", 15, 7, "https://realpython.com/defining-your-own-python-function/"),
        "py-vid-flow" to vid("py-vid-flow", "Funksiyalar 7 daqiqada", 15, 6, "https://www.youtube.com/watch?v=nFX3RrRNlP4"),
        "py-fc-flow" to fcFlow,
        "py-quiz-flow" to quizFlow,
        "py-md-ds" to mdDs,
        "py-web-ds" to web("py-web-ds", "Python: ma'lumotlar tuzilmalari", 15, 7, "https://docs.python.org/3/tutorial/datastructures.html"),
        "py-vid-ds" to vid("py-vid-ds", "list va dict", 15, 5, "https://www.youtube.com/watch?v=4NpYAe-JXr0"),
        "py-fc-ds" to fcDs,
        "py-quiz-ds" to quizDs,
    )

    val answerKey: Map<String, AnswerSpec> = mapOf(
        "py-q-syntax-1" to AnswerSpec(setOf("B"), explanation = "42 — butun son, ya'ni int.", xp = 12),
        "py-q-syntax-2" to AnswerSpec(correctInputs = setOf("33", "'33'"), explanation = "+ operatori matnlarni ulaydi: '3'+'3' = '33'.", xp = 13),
        "py-q-syntax-3" to AnswerSpec(setOf("F"), explanation = "Python dinamik turlanadi — tur qiymatdan aniqlanadi.", xp = 10),
        "py-q-flow-1" to AnswerSpec(setOf("B"), explanation = "range(3) -> 0,1,2 — uch marta.", xp = 12),
        "py-q-flow-2" to AnswerSpec(setOf("B"), explanation = "return bo'lmasa funksiya None qaytaradi.", xp = 12),
        "py-q-flow-3" to AnswerSpec(correctInputs = setOf("otstep", "indentation", "indent", "probel", "bo'shliq"), explanation = "Otstep (indentation) bloklarni ajratadi.", xp = 13),
        "py-q-ds-1" to AnswerSpec(setOf("D"), explanation = "tuple yaratilgandan keyin o'zgarmaydi.", xp = 12),
        "py-q-ds-2" to AnswerSpec(setOf("A", "C"), explanation = "set takrorlanmas va tez a'zolik beradi; tartib/kalit-qiymat yo'q.", xp = 15),
        "py-q-ds-3" to AnswerSpec(correctInputs = setOf("3"), explanation = "Takrorlar tashlanadi: {1,2,3} — 3 ta element.", xp = 13),
    )

    /** SINGLE-choice questions reused by the global quiz + Arena (Pillar: interest-driven). */
    val samplePool: List<String> = listOf(
        """{ "id": "py-q-ds-1", "type": "SINGLE", "duration_sec": 15, "text": "Python'da qaysi tuzilma o'zgarmas?", "options": [ { "id": "A", "text": "list" }, { "id": "B", "text": "dict" }, { "id": "C", "text": "set" }, { "id": "D", "text": "tuple" } ] }""",
        """{ "id": "py-q-flow-1", "type": "SINGLE", "duration_sec": 15, "text": "range(3) necha marta aylanadi?", "options": [ { "id": "A", "text": "2" }, { "id": "B", "text": "3" }, { "id": "C", "text": "4" }, { "id": "D", "text": "Cheksiz" } ] }""",
        """{ "id": "py-q-syntax-1", "type": "SINGLE", "duration_sec": 15, "text": "type(42) nima?", "options": [ { "id": "A", "text": "str" }, { "id": "B", "text": "int" }, { "id": "C", "text": "float" }, { "id": "D", "text": "bool" } ] }""",
    )
}
