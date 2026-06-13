package uz.tayanch.app.di

import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.network.NetworkModule
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.SecureSessionStore
import uz.tayanch.app.data.security.SessionManager
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

val appModule = module {
    single { SecureSessionStore(androidContext()) }
    single { SessionManager(get()) }
    single<HttpClient> { NetworkModule.build(get(), get()) }
    single { TayanchRepository(get()) }
    single { ResourceProvider(androidContext()) }

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
