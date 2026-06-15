package uz.tayanch.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uz.tayanch.app.ui.auth.AuthScreen
import uz.tayanch.app.ui.auth.OtpScreen
import uz.tayanch.app.ui.battle.BattleScreen
import uz.tayanch.app.ui.content.ContentScreen
import uz.tayanch.app.ui.flashcard.FlashcardScreen
import uz.tayanch.app.ui.interview.InterviewScreen
import uz.tayanch.app.ui.jobs.JobDetailScreen
import uz.tayanch.app.ui.onboarding.OnboardingScreen
import uz.tayanch.app.ui.quiz.QuizScreen
import uz.tayanch.app.ui.resume.ResumeScreen

/**
 * The whole app's navigation. Three phases:
 *  - Gateways (auth, onboarding) — no bottom bar, and once you pass them the
 *    back stack is cleared so Back can't leak you into the login screen.
 *  - Main hub — the bottom-bar Scaffold ([MainScaffold]).
 *  - Immersive tasks (content, flashcard, quiz, battle) — full-screen, launched
 *    on top of the hub and popped back when finished.
 */
private const val NAV_ANIM_MS = 300

@Composable
fun TayanchNavGraph(startDestination: String) {
    val nav = rememberNavController()

    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = nav,
        startDestination = startDestination,
        // Outer host transitions: forward navigations slide in from the right and
        // the previous screen slides out left; on Back/pop the directions reverse,
        // so the immersive screens (ContentScreen, etc.) open and close smoothly.
        enterTransition = { slideIntoContainer(SlideDirection.Left, tween(NAV_ANIM_MS)) + fadeIn(tween(NAV_ANIM_MS)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Left, tween(NAV_ANIM_MS)) + fadeOut(tween(NAV_ANIM_MS)) },
        popEnterTransition = { slideIntoContainer(SlideDirection.Right, tween(NAV_ANIM_MS)) + fadeIn(tween(NAV_ANIM_MS)) },
        popExitTransition = { slideOutOfContainer(SlideDirection.Right, tween(NAV_ANIM_MS)) + fadeOut(tween(NAV_ANIM_MS)) },
    ) {

        composable(Routes.AUTH) {
            AuthScreen(
                onAuthenticated = { onboarded ->
                    val target = if (onboarded) Routes.MAIN else Routes.ONBOARDING
                    nav.navigate(target) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onForgotPassword = { nav.navigate(Routes.OTP) },
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onDone = {
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.MAIN) {
            MainScaffold(
                onOpenContent = { id -> nav.navigate(Routes.content(id)) },
                onOpenFlashcard = { id -> nav.navigate(Routes.flashcard(id)) },
                onOpenQuiz = { id -> nav.navigate(Routes.quiz(id)) },
                onOpenBattle = { nav.navigate(Routes.BATTLE) },
                onOpenVacancy = { id -> nav.navigate(Routes.vacancy(id)) },
                onOpenInterview = { nav.navigate(Routes.INTERVIEW) },
                onOpenResume = { nav.navigate(Routes.RESUME) },
                onLogout = {
                    nav.navigate(Routes.AUTH) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.CONTENT,
            arguments = listOf(navArgument(Routes.ARG_CONTENT_ID) { type = NavType.StringType }),
        ) { entry ->
            val id = entry.arguments?.getString(Routes.ARG_CONTENT_ID).orEmpty()
            ContentScreen(contentId = id, onFinish = { nav.popBackStack() })
        }

        composable(
            route = Routes.FLASHCARD,
            arguments = listOf(navArgument(Routes.ARG_CONTENT_ID) { type = NavType.StringType }),
        ) { entry ->
            val id = entry.arguments?.getString(Routes.ARG_CONTENT_ID).orEmpty()
            FlashcardScreen(contentId = id, onFinish = { nav.popBackStack() })
        }

        composable(
            route = Routes.QUIZ,
            arguments = listOf(navArgument(Routes.ARG_CONTENT_ID) { type = NavType.StringType }),
        ) { entry ->
            val id = entry.arguments?.getString(Routes.ARG_CONTENT_ID).orEmpty()
            QuizScreen(contentId = id, onFinish = { nav.popBackStack() })
        }

        composable(Routes.BATTLE) {
            BattleScreen(onFinish = { nav.popBackStack() })
        }

        composable(
            route = Routes.VACANCY,
            arguments = listOf(navArgument(Routes.ARG_VACANCY_ID) { type = NavType.StringType }),
        ) { entry ->
            val id = entry.arguments?.getString(Routes.ARG_VACANCY_ID).orEmpty()
            JobDetailScreen(vacancyId = id, onBack = { nav.popBackStack() })
        }

        composable(Routes.RESUME) {
            ResumeScreen(onBack = { nav.popBackStack() })
        }

        composable(Routes.INTERVIEW) {
            InterviewScreen(onEnd = { nav.popBackStack() })
        }

        composable(Routes.OTP) {
            OtpScreen(onBack = { nav.popBackStack() }, onDone = { nav.popBackStack() })
        }
    }
}
