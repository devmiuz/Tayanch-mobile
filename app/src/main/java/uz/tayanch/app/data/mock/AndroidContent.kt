package uz.tayanch.app.data.mock

/**
 * "Android dasturlash" interest — roadmap + content (Uzbek). Path based on
 * roadmap.sh/android: Kotlin basics → Android Studio & app components → Jetpack
 * Compose UI & state (Level 1, fully authored), then architecture (MVVM/MVI),
 * coroutines, networking & storage, DI & testing (Level 2, locked).
 */
object AndroidContent {

    val roadmapJson = """
        {
          "levels": [
            {
              "level_id": "an-l1", "title": "Boshlang'ich Android", "rank_label": "Junior",
              "required_xp": 0, "is_unlocked": true,
              "topics": [
                {
                  "topic_id": "an-t-kotlin", "title": "1. Kotlin asoslari", "order": 1,
                  "contents": [
                    { "content_id": "an-md-kotlin", "type": "MARKDOWN", "title": "val, var, funksiyalar", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "an-web-kotlin", "type": "WEBLINK", "title": "Kotlin asoslari (rasmiy)", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 7 },
                    { "content_id": "an-vid-kotlin", "type": "VIDEO", "title": "Kotlin tez kirish", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "an-fc-kotlin", "type": "FLASHCARD", "title": "Kotlin kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "an-quiz-kotlin", "type": "QUIZ", "title": "Kotlin testi", "reward_xp": 50, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "an-t-components", "title": "2. Ilova komponentlari", "order": 2,
                  "contents": [
                    { "content_id": "an-md-components", "type": "MARKDOWN", "title": "Activity va hayot sikli", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "an-web-components", "type": "WEBLINK", "title": "Ilova asoslari (rasmiy)", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 7 },
                    { "content_id": "an-vid-components", "type": "VIDEO", "title": "Activity lifecycle", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 5 },
                    { "content_id": "an-fc-components", "type": "FLASHCARD", "title": "Komponent kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "an-quiz-components", "type": "QUIZ", "title": "Komponentlar testi", "reward_xp": 55, "is_completed": false, "is_locked": false }
                  ]
                },
                {
                  "topic_id": "an-t-compose", "title": "3. Jetpack Compose va holat", "order": 3,
                  "contents": [
                    { "content_id": "an-md-compose", "type": "MARKDOWN", "title": "Composable va state", "reward_xp": 20, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "an-web-compose", "type": "WEBLINK", "title": "Compose'da fikrlash (rasmiy)", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 8 },
                    { "content_id": "an-vid-compose", "type": "VIDEO", "title": "Compose asoslari", "reward_xp": 15, "is_completed": false, "is_locked": false, "estimated_minutes": 6 },
                    { "content_id": "an-fc-compose", "type": "FLASHCARD", "title": "Compose kartalari", "reward_xp": 25, "is_completed": false, "is_locked": false },
                    { "content_id": "an-quiz-compose", "type": "QUIZ", "title": "Compose testi", "reward_xp": 60, "is_completed": false, "is_locked": false }
                  ]
                }
              ],
              "assignment": { "level_id": "an-l1", "title": "Junior bitiruv: ToDo Compose ilovasi", "state": "AVAILABLE" }
            },
            {
              "level_id": "an-l2", "title": "O'rta Android", "rank_label": "Middle",
              "required_xp": 1500, "is_unlocked": false,
              "topics": [
                {
                  "topic_id": "an-t-arch", "title": "1. Arxitektura (MVVM/MVI)", "order": 1,
                  "contents": [
                    { "content_id": "an-md-arch", "type": "MARKDOWN", "title": "ViewModel va UDF", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 7 },
                    { "content_id": "an-quiz-arch", "type": "QUIZ", "title": "Arxitektura testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                },
                {
                  "topic_id": "an-t-async", "title": "2. Coroutines va tarmoq", "order": 2,
                  "contents": [
                    { "content_id": "an-md-async", "type": "MARKDOWN", "title": "suspend, Flow, Retrofit/Ktor", "reward_xp": 25, "is_completed": false, "is_locked": true, "estimated_minutes": 7 },
                    { "content_id": "an-quiz-async", "type": "QUIZ", "title": "Async testi", "reward_xp": 70, "is_completed": false, "is_locked": true }
                  ]
                }
              ],
              "assignment": { "level_id": "an-l2", "title": "Middle bitiruv: REST'dan ma'lumot ko'rsatuvchi ilova", "state": "LOCKED" }
            }
          ]
        }
    """.trimIndent()

    private val mdKotlin = """
        {
          "content_id": "an-md-kotlin", "type": "MARKDOWN", "title": "val, var, funksiyalar",
          "reward_xp": 20, "estimated_minutes": 5,
          "markdown": "# Kotlin asoslari\n\nKotlin — Android'ning asosiy tili: ixcham, xavfsiz (null-safety) va Java bilan to'liq mos.\n\n## o'zgaruvchilar\n```kotlin\nval name = \"Diyor\"   // o'zgarmas (read-only)\nvar age = 23          // o'zgaruvchan\n```\n\n## null-xavfsizlik\n```kotlin\nvar nick: String? = null   // ? -> null bo'lishi mumkin\nprintln(nick?.length)       // xavfsiz chaqiruv\n```\n\n## funksiya\n```kotlin\nfun salom(ism: String): String = \"Salom, ${'$'}ism\"\n```\n\n> `val` ni afzal ko'ring — o'zgarmaslik xatolarni kamaytiradi."
        }
    """.trimIndent()

    private val mdComponents = """
        {
          "content_id": "an-md-components", "type": "MARKDOWN", "title": "Activity va hayot sikli",
          "reward_xp": 20, "estimated_minutes": 6,
          "markdown": "# Ilova komponentlari va hayot sikli\n\n**Activity** — bitta ekran. Tizim uni yaratadi, to'xtatadi va yo'q qiladi; bu o'tishlar *lifecycle* deyiladi.\n\n## asosiy callback'lar\n- `onCreate` — UI o'rnatiladi\n- `onStart` / `onResume` — ko'rinadi va faol\n- `onPause` / `onStop` — fonga o'tadi\n- `onDestroy` — yo'q qilinadi\n\n```kotlin\nclass MainActivity : ComponentActivity() {\n    override fun onCreate(s: Bundle?) {\n        super.onCreate(s)\n        setContent { App() }\n    }\n}\n```\n\n> Resurslarni `onPause`/`onStop`da bo'shating — Tayanch'dagi diqqat taymeri ham shu hodisaga tayanadi."
        }
    """.trimIndent()

    private val mdCompose = """
        {
          "content_id": "an-md-compose", "type": "MARKDOWN", "title": "Composable va state",
          "reward_xp": 20, "estimated_minutes": 6,
          "markdown": "# Jetpack Compose va holat\n\nCompose — deklarativ UI: siz holatni tasvirlaysiz, framework ekranni chizadi. XML kerak emas.\n\n## composable funksiya\n```kotlin\n@Composable\nfun Counter() {\n    var count by remember { mutableStateOf(0) }\n    Button(onClick = { count++ }) {\n        Text(\"Bosilgan: ${'$'}count\")\n    }\n}\n```\n\n## holat (state)\n- `remember` — rekompozitsiyada qiymatni saqlaydi\n- `mutableStateOf` — o'zgarsa UI qayta chiziladi\n\n> Qoida: holatni yuqoriga ko'taring (state hoisting) — composable'lar imkon qadar holatsiz bo'lsin."
        }
    """.trimIndent()

    private fun web(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "WEBLINK", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    private fun vid(id: String, title: String, xp: Int, min: Int, url: String) =
        """{ "content_id": "$id", "type": "VIDEO", "title": "$title", "reward_xp": $xp, "estimated_minutes": $min, "url": "$url" }"""

    private val fcKotlin = """
        {
          "content_id": "an-fc-kotlin", "type": "FLASHCARD", "title": "Kotlin kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "a1", "front": "val va var farqi?", "back": "val — o'zgarmas (read-only), var — o'zgaruvchan." },
            { "id": "a2", "front": "String? nimani bildiradi?", "back": "Null bo'lishi mumkin bo'lgan tur (nullable)." },
            { "id": "a3", "front": "?. operatori nima qiladi?", "back": "Xavfsiz chaqiruv — obyekt null bo'lsa, natija null, NPE bermaydi." },
            { "id": "a4", "front": "Qaysi til Android uchun rasmiy tavsiya etiladi?", "back": "Kotlin." }
          ]
        }
    """.trimIndent()

    private val fcComponents = """
        {
          "content_id": "an-fc-components", "type": "FLASHCARD", "title": "Komponent kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "a1", "front": "Activity nima?", "back": "Foydalanuvchi bilan o'zaro ishlaydigan bitta ekran." },
            { "id": "a2", "front": "Ekran ko'ringanda qaysi callback chaqiriladi?", "back": "onStart, so'ng onResume." },
            { "id": "a3", "front": "Ilova fonga o'tganda?", "back": "onPause, so'ng onStop chaqiriladi." },
            { "id": "a4", "front": "onCreate'da nima qilinadi?", "back": "UI o'rnatiladi (setContent) va boshlang'ich holat tayyorlanadi." }
          ]
        }
    """.trimIndent()

    private val fcCompose = """
        {
          "content_id": "an-fc-compose", "type": "FLASHCARD", "title": "Compose kartalari", "reward_xp": 25,
          "flashcards": [
            { "id": "a1", "front": "Compose qanday UI paradigmasi?", "back": "Deklarativ — holatdan UI hosil bo'ladi." },
            { "id": "a2", "front": "remember nima uchun?", "back": "Rekompozitsiya orasida qiymatni eslab qoladi." },
            { "id": "a3", "front": "mutableStateOf o'zgarsa nima bo'ladi?", "back": "Uni o'qiydigan composable qayta chiziladi (recomposition)." },
            { "id": "a4", "front": "State hoisting nima?", "back": "Holatni composable'dan yuqoriga ko'tarib, uni holatsiz va qayta ishlatiladigan qilish." }
          ]
        }
    """.trimIndent()

    private val quizKotlin = """
        {
          "content_id": "an-quiz-kotlin", "type": "QUIZ", "title": "Kotlin testi", "reward_xp": 50,
          "questions": [
            { "id": "an-q-kotlin-1", "type": "SINGLE", "text": "Qaysi kalit so'z o'zgarmas qiymat e'lon qiladi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "var" }, { "id": "B", "text": "val" }, { "id": "C", "text": "let" }, { "id": "D", "text": "const fun" } ] },
            { "id": "an-q-kotlin-2", "type": "TRUE_FALSE", "text": "String? turidagi o'zgaruvchi null qiymat qabul qila oladi.", "duration_sec": 15,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] },
            { "id": "an-q-kotlin-3", "type": "INPUT", "text": "Null bo'lsa NPE bermasdan chaqirishga imkon beradigan operatorni yozing (masalan ?.).", "duration_sec": 20 }
          ]
        }
    """.trimIndent()

    private val quizComponents = """
        {
          "content_id": "an-quiz-components", "type": "QUIZ", "title": "Komponentlar testi", "reward_xp": 55,
          "questions": [
            { "id": "an-q-comp-1", "type": "SINGLE", "text": "UI odatda qaysi callback'da o'rnatiladi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "onCreate" }, { "id": "B", "text": "onDestroy" }, { "id": "C", "text": "onPause" }, { "id": "D", "text": "onStop" } ] },
            { "id": "an-q-comp-2", "type": "MULTI", "text": "Ilova fonga o'tganda chaqiriladigan callback'larni tanlang.", "duration_sec": 30,
              "options": [ { "id": "A", "text": "onPause" }, { "id": "B", "text": "onResume" }, { "id": "C", "text": "onStop" }, { "id": "D", "text": "onStart" } ] },
            { "id": "an-q-comp-3", "type": "INPUT", "text": "Bitta ekranni ifodalovchi Android komponentini nomlang.", "duration_sec": 20 }
          ]
        }
    """.trimIndent()

    private val quizCompose = """
        {
          "content_id": "an-quiz-compose", "type": "QUIZ", "title": "Compose testi", "reward_xp": 60,
          "questions": [
            { "id": "an-q-compose-1", "type": "SINGLE", "text": "Compose qaysi UI paradigmasiga tegishli?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "Imperativ (XML)" }, { "id": "B", "text": "Deklarativ" }, { "id": "C", "text": "Server-side" }, { "id": "D", "text": "Hech qaysi" } ] },
            { "id": "an-q-compose-2", "type": "SINGLE", "text": "Rekompozitsiyada qiymatni saqlash uchun nima ishlatiladi?", "duration_sec": 20,
              "options": [ { "id": "A", "text": "findViewById" }, { "id": "B", "text": "remember" }, { "id": "C", "text": "Bundle" }, { "id": "D", "text": "SharedPreferences" } ] },
            { "id": "an-q-compose-3", "type": "TRUE_FALSE", "text": "Jetpack Compose UI uchun XML layout fayllari shart.", "duration_sec": 15,
              "options": [ { "id": "T", "text": "To'g'ri" }, { "id": "F", "text": "Noto'g'ri" } ] }
          ]
        }
    """.trimIndent()

    val contentDetails: Map<String, String> = mapOf(
        "an-md-kotlin" to mdKotlin,
        "an-web-kotlin" to web("an-web-kotlin", "Kotlin asoslari (rasmiy)", 15, 7, "https://kotlinlang.org/docs/basic-syntax.html"),
        "an-vid-kotlin" to vid("an-vid-kotlin", "Kotlin 100 soniyada", 15, 6, "https://www.youtube.com/watch?v=xT8oP0wy-A0"),
        "an-fc-kotlin" to fcKotlin,
        "an-quiz-kotlin" to quizKotlin,
        "an-md-components" to mdComponents,
        "an-web-components" to web("an-web-components", "Ilova asoslari (rasmiy)", 15, 7, "https://developer.android.com/guide/components/fundamentals"),
        "an-vid-components" to vid("an-vid-components", "Activity lifecycle", 15, 5, "https://www.youtube.com/watch?v=UJN3AL4tiqw"),
        "an-fc-components" to fcComponents,
        "an-quiz-components" to quizComponents,
        "an-md-compose" to mdCompose,
        "an-web-compose" to web("an-web-compose", "Compose'da fikrlash (rasmiy)", 15, 8, "https://developer.android.com/develop/ui/compose/mental-model"),
        "an-vid-compose" to vid("an-vid-compose", "Compose asoslari", 15, 6, "https://www.youtube.com/watch?v=k3jvNqj4m08"),
        "an-fc-compose" to fcCompose,
        "an-quiz-compose" to quizCompose,
    )

    val answerKey: Map<String, AnswerSpec> = mapOf(
        "an-q-kotlin-1" to AnswerSpec(setOf("B"), explanation = "val — o'zgarmas (read-only) qiymat.", xp = 12),
        "an-q-kotlin-2" to AnswerSpec(setOf("T"), explanation = "? qo'shimchasi turni nullable qiladi.", xp = 10),
        "an-q-kotlin-3" to AnswerSpec(correctInputs = setOf("?.", "safe call", "xavfsiz chaqiruv"), explanation = "?. — xavfsiz chaqiruv operatori.", xp = 13),
        "an-q-comp-1" to AnswerSpec(setOf("A"), explanation = "UI onCreate'da setContent bilan o'rnatiladi.", xp = 12),
        "an-q-comp-2" to AnswerSpec(setOf("A", "C"), explanation = "Fonga o'tishda onPause, so'ng onStop chaqiriladi.", xp = 15),
        "an-q-comp-3" to AnswerSpec(correctInputs = setOf("activity", "aktivlik"), explanation = "Activity — bitta ekran.", xp = 13),
        "an-q-compose-1" to AnswerSpec(setOf("B"), explanation = "Compose deklarativ UI.", xp = 12),
        "an-q-compose-2" to AnswerSpec(setOf("B"), explanation = "remember qiymatni rekompozitsiyada saqlaydi.", xp = 12),
        "an-q-compose-3" to AnswerSpec(setOf("F"), explanation = "Compose'da XML shart emas — UI Kotlin'da yoziladi.", xp = 13),
    )

    val samplePool: List<String> = listOf(
        """{ "id": "an-q-kotlin-1", "type": "SINGLE", "duration_sec": 15, "text": "Qaysi kalit so'z o'zgarmas?", "options": [ { "id": "A", "text": "var" }, { "id": "B", "text": "val" }, { "id": "C", "text": "let" }, { "id": "D", "text": "const fun" } ] }""",
        """{ "id": "an-q-compose-1", "type": "SINGLE", "duration_sec": 15, "text": "Compose qaysi paradigma?", "options": [ { "id": "A", "text": "Imperativ" }, { "id": "B", "text": "Deklarativ" }, { "id": "C", "text": "Server-side" }, { "id": "D", "text": "Hech qaysi" } ] }""",
        """{ "id": "an-q-comp-1", "type": "SINGLE", "duration_sec": 15, "text": "UI qaysi callback'da o'rnatiladi?", "options": [ { "id": "A", "text": "onCreate" }, { "id": "B", "text": "onDestroy" }, { "id": "C", "text": "onPause" }, { "id": "D", "text": "onStop" } ] }""",
    )
}
