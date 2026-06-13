package uz.tayanch.app.ui.preview

import uz.tayanch.app.data.dto.ArenaStatusDto
import uz.tayanch.app.data.dto.AssignmentState
import uz.tayanch.app.data.dto.AssignmentStatusDto
import uz.tayanch.app.data.dto.AssignmentTaskDto
import uz.tayanch.app.data.dto.BadgeDto
import uz.tayanch.app.data.dto.CareerHubDto
import uz.tayanch.app.data.dto.CareerVelocityDto
import uz.tayanch.app.data.dto.ContentDetailDto
import uz.tayanch.app.data.dto.ContentNodeDto
import uz.tayanch.app.data.dto.ContentType
import uz.tayanch.app.data.dto.FlashcardDto
import uz.tayanch.app.data.dto.FocusStatsDto
import uz.tayanch.app.data.dto.InterestDto
import uz.tayanch.app.data.dto.LeaderboardEntryDto
import uz.tayanch.app.data.dto.LeaderboardResponse
import uz.tayanch.app.data.dto.LevelDto
import uz.tayanch.app.data.dto.MajorDto
import uz.tayanch.app.data.dto.MockInterviewStatusDto
import uz.tayanch.app.data.dto.OpponentDto
import uz.tayanch.app.data.dto.QuestionType
import uz.tayanch.app.data.dto.QuizOptionDto
import uz.tayanch.app.data.dto.QuizQuestionDto
import uz.tayanch.app.data.dto.RoadmapResponse
import uz.tayanch.app.data.dto.TopicDto
import uz.tayanch.app.data.dto.UserProfileDto
import uz.tayanch.app.data.dto.VacancyDto
import uz.tayanch.app.data.dto.VacancyListResponse
import uz.tayanch.app.ui.auth.AuthUiState
import uz.tayanch.app.ui.onboarding.OnboardingUiState
import uz.tayanch.app.ui.profile.ProfileData

/**
 * Static fixtures used ONLY by the screen-level @Preview functions, so the
 * previews render a realistic, populated state without Koin or async loading.
 */
internal object PreviewSamples {

    private fun node(id: String, type: String, title: String, xp: Int, done: Boolean = false, locked: Boolean = false) =
        ContentNodeDto(content_id = id, type = type, title = title, reward_xp = xp, is_completed = done, is_locked = locked)

    val roadmap = RoadmapResponse(
        levels = listOf(
            LevelDto(
                level_id = "lvl-junior", title = "Junior AppSec", rank_label = "Junior",
                required_xp = 0, is_unlocked = true,
                topics = listOf(
                    TopicDto(
                        topic_id = "t1", title = "1. Autentifikatsiya kamchiliklari", order = 1,
                        contents = listOf(
                            node("md", ContentType.MARKDOWN, "Read", 20, done = true),
                            node("web", ContentType.WEBLINK, "Web", 15, done = true),
                            node("vid", ContentType.VIDEO, "Video", 15, done = true),
                            node("fc", ContentType.FLASHCARD, "Cards", 25, done = true),
                            node("qz", ContentType.QUIZ, "Quiz", 50),
                        ),
                    ),
                    TopicDto(
                        topic_id = "t2", title = "2. Inyeksiya (SQLi / XSS)", order = 2,
                        contents = listOf(
                            node("md2", ContentType.MARKDOWN, "Read", 20),
                            node("web2", ContentType.WEBLINK, "Web", 15, locked = true),
                            node("vid2", ContentType.VIDEO, "Video", 15, locked = true),
                            node("fc2", ContentType.FLASHCARD, "Cards", 25, locked = true),
                            node("qz2", ContentType.QUIZ, "Quiz", 60, locked = true),
                        ),
                    ),
                ),
                assignment = AssignmentTaskDto("lvl-junior", "Junior bitiruv: login API'ni mustahkamlash", AssignmentState.AVAILABLE),
            ),
            LevelDto(
                level_id = "lvl-middle", title = "Middle AppSec", rank_label = "Middle",
                required_xp = 1500, is_unlocked = false,
                topics = listOf(
                    TopicDto(
                        topic_id = "t3", title = "1. Amaliy kriptografiya", order = 1,
                        contents = listOf(node("md3", ContentType.MARKDOWN, "Read", 25, locked = true)),
                    ),
                ),
                assignment = AssignmentTaskDto("lvl-middle", "Middle bitiruv: Zero-Trust shlyuzi", AssignmentState.LOCKED),
            ),
        ),
    )

    val careerHub = CareerHubDto(
        arena = ArenaStatusDto(min_xp = 500, current_xp = 1180, unlocked = true),
        mock_interview = MockInterviewStatusDto(available = true, reached_level = "Strong Junior", pending_requests = 1),
        assignments = listOf(
            AssignmentStatusDto("lvl-junior", "Junior bitiruv", AssignmentState.PENDING, "github.com/jasur/junior-task"),
            AssignmentStatusDto("lvl-middle", "Middle bitiruv", AssignmentState.AVAILABLE),
        ),
    )

    private val profile = UserProfileDto(
        id = "u1", full_name = "Jasur Abdullaev", current_level = "Strong Junior", target_level = "Middle",
        current_xp = 1180, expected_salary = 1200, global_rank = 3,
        ai_motivation = "Siz Strong Junior darajasidasiz va 1200 USD maoshni maqsad qilgansiz. Inyeksiya mavzusini yakunlang — Middle qo'l ostingizda.",
        velocity = CareerVelocityDto(1200, 450, "Haftasiga 450 XP sur'atida Middle suhbat bosqichiga ~2.5 oyda yetasiz."),
        focus = FocusStatsDto("4m 12s", "Past (0.8 / maqola)"),
        badges = listOf(
            BadgeDto("b1", "Birinchi qadam", "🩸", true),
            BadgeDto("b2", "OWASP kashshofi", "🛡️", true),
            BadgeDto("b3", "Diqqat ustasi", "🎯", true),
            BadgeDto("b4", "Arena g'olibi", "⚔️", false),
        ),
    )

    private val leaderboard = LeaderboardResponse(
        scope = "Mutaxassisligim",
        leaderboard = listOf(
            LeaderboardEntryDto(1, "Alisher K.", "Senior", 4520),
            LeaderboardEntryDto(2, "Timur B.", "Middle", 1890),
            LeaderboardEntryDto(3, "Siz", "Strong Junior", 1180, is_me = true),
            LeaderboardEntryDto(4, "Dilnoza R.", "Junior", 980),
        ),
    )

    val profileData = ProfileData(profile, leaderboard)

    val markdownContent = ContentDetailDto(
        content_id = "md-auth", type = ContentType.MARKDOWN, title = "Buzilgan autentifikatsiya", reward_xp = 20,
        markdown = "# Buzilgan autentifikatsiya\n\nAutentifikatsiya foydalanuvchi **kimligini** tasdiqlaydi. Buzilganda tajovuzkor hisoblarni egallab olishi mumkin.\n\n## Keng tarqalgan sabablar\n- Credential stuffing\n- Session fixation\n\n```kotlin\nval hash = Argon2id.hash(password, salt)\n```",
        estimated_minutes = 4,
    )

    val flashcard = FlashcardDto(
        id = "c1",
        front = "JWT imzosi nimani kafolatlaydi?",
        back = "Yaxlitlik va haqiqiylik — payload o'zgartirilmagan va kalit egasi tomonidan berilgan.",
    )

    val quizQuestion = QuizQuestionDto(
        id = "q1", type = QuestionType.SINGLE,
        text = "2026-yilda OWASP qaysi parol xeshlash algoritmini tavsiya qiladi?",
        options = listOf(
            QuizOptionDto("A", "MD5"), QuizOptionDto("B", "SHA-256"),
            QuizOptionDto("C", "Argon2id"), QuizOptionDto("D", "Base64"),
        ),
        duration_sec = 25,
    )

    val opponent = OpponentDto("Timur B.", "Middle", 42)

    val authState = AuthUiState(
        isRegister = true, phone = "+998901234567", fullName = "Jasur Abdullaev",
        age = "21", password = "correct horse battery staple",
    )

    val onboardingState = OnboardingUiState(
        majorsLoading = false,
        majors = listOf(MajorDto("m1", "Kiberxavfsizlikni boshqarish"), MajorDto("m2", "Dasturiy injiniring")),
        selectedMajor = "Kiberxavfsizlikni boshqarish",
        interests = listOf(
            InterestDto("i1", "Penetration Testing"),
            InterestDto("i2", "Kriptografiya"),
            InterestDto("i3", "DevSecOps"),
        ),
        selectedInterestIds = setOf("i1"),
        interestsHasMore = true, aiGenerated = true, salary = "1200",
    )
    val onboardingMajors = onboardingState.majors

    private fun vac(id: String, title: String, company: String, salary: String, level: String, cat: String, tags: List<String>) =
        VacancyDto(
            id = id, title = title, company = company, salary = salary, location = "Toshkent",
            type = "Toʻliq stavka", category = cat, level = level, tags = tags,
            description = "Lorem ipsum vazifa tavsifi.", requirements = listOf("Kotlin", "Git"), posted_at = "2 kun oldin",
        )

    val vacancyList = VacancyListResponse(
        categories = listOf("Barchasi", "Mobil ishlab chiqish", "Kiberxavfsizlik", "Backend", "Frontend"),
        vacancies = listOf(
            vac("v1", "Junior Android dasturchi", "EPAM Systems", "8 000 000 UZS", "Junior", "Mobil ishlab chiqish", listOf("Kotlin", "Jetpack Compose")),
            vac("v2", "SOC tahlilchisi", "UZINFOCOM", "12 000 000 UZS", "Middle", "Kiberxavfsizlik", listOf("SIEM", "OWASP")),
            vac("v3", "Backend dasturchi (FastAPI)", "PayMe", "15 000 000 UZS", "Middle", "Backend", listOf("Python", "FastAPI")),
        ),
    )

    val vacancy = vacancyList.vacancies.first().copy(
        description = "Kotlin va Jetpack Compose asosida mobil ilovalar ishlab chiqish. Jamoaviy ishlash va Git bilan tanishlik talab etiladi.",
        requirements = listOf("Kotlin asoslari", "Jetpack Compose", "REST API", "Git"),
    )
}
