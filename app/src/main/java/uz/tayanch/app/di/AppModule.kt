package uz.tayanch.app.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.SecureSessionStore
import uz.tayanch.app.ui.auth.AuthViewModel
import uz.tayanch.app.ui.battle.BattleViewModel
import uz.tayanch.app.ui.career.CareerViewModel
import uz.tayanch.app.ui.content.ContentViewModel
import uz.tayanch.app.ui.flashcard.FlashcardViewModel
import uz.tayanch.app.ui.home.HomeViewModel
import uz.tayanch.app.ui.jobs.JobDetailViewModel
import uz.tayanch.app.ui.jobs.JobsViewModel
import uz.tayanch.app.ui.onboarding.OnboardingViewModel
import uz.tayanch.app.ui.profile.ProfileViewModel
import uz.tayanch.app.ui.quiz.QuizViewModel

/**
 * The single Koin module for the app. Replaces the former hand-rolled
 * ServiceLocator: shared singletons + every screen's ViewModel.
 */
val appModule = module {
    // Shared singletons.
    single { TayanchRepository() }
    single { SecureSessionStore(androidContext()) }
    single { ResourceProvider(androidContext()) }

    // ViewModels (resolved per ViewModelStoreOwner via koinViewModel()).
    viewModelOf(::AuthViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CareerViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ContentViewModel)
    viewModelOf(::FlashcardViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::BattleViewModel)
    viewModelOf(::JobsViewModel)
    viewModelOf(::JobDetailViewModel)
}
