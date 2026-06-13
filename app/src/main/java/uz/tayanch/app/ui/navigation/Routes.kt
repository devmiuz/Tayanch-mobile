package uz.tayanch.app.ui.navigation

/** Central place for navigation routes and their argument builders. */
object Routes {
    const val AUTH = "auth"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"

    const val CONTENT = "content/{contentId}"
    const val FLASHCARD = "flashcard/{contentId}"
    const val QUIZ = "quiz/{contentId}"
    const val BATTLE = "battle"
    const val VACANCY = "vacancy/{vacancyId}"
    const val RESUME = "resume"
    const val INTERVIEW = "interview"
    const val OTP = "otp"

    const val ARG_CONTENT_ID = "contentId"
    const val ARG_VACANCY_ID = "vacancyId"

    fun content(id: String) = "content/$id"
    fun flashcard(id: String) = "flashcard/$id"
    fun quiz(contentId: String) = "quiz/$contentId"
    fun vacancy(id: String) = "vacancy/$id"
}
