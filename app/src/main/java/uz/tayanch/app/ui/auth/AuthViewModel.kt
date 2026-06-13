package uz.tayanch.app.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.LoginRequest
import uz.tayanch.app.data.dto.RegisterRequest
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.InputSanitizer
import uz.tayanch.app.data.security.SecureSessionStore

data class AuthUiState(
    val isRegister: Boolean = false,
    val phone: String = "+998",
    val fullName: String = "",
    val age: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)

class AuthViewModel(
    private val repo: TayanchRepository,
    private val store: SecureSessionStore,
    private val res: ResourceProvider,
) : ViewModel() {

    var state by mutableStateOf(AuthUiState())
        private set

    val hasEnrolledSession: Boolean get() = store.hasEnrolledSession

    fun setMode(register: Boolean) { state = state.copy(isRegister = register, error = null) }
    fun onPhone(v: String) { state = state.copy(phone = v.filter { it.isDigit() || it == '+' }.take(13)) }
    fun onName(v: String) { state = state.copy(fullName = v.take(60)) }
    fun onAge(v: String) { state = state.copy(age = v.filter(Char::isDigit).take(2)) }
    fun onPassword(v: String) { state = state.copy(password = v.take(64)) }

    val canSubmit: Boolean
        get() = with(state) {
            val phoneOk = phone.count(Char::isDigit) >= 12
            if (!isRegister) return phoneOk && password.isNotBlank()
            val ageOk = age.toIntOrNull()?.let { it in 14..70 } == true
            phoneOk && fullName.isNotBlank() && ageOk && estimatePassword(password).score >= 3
        }

    /** Pillar 7 note: in production the password is RSA-encrypted before this call. */
    fun submit(onAuthenticated: (onboarded: Boolean) -> Unit) {
        if (!canSubmit || state.loading) return
        state = state.copy(loading = true, error = null)
        viewModelScope.launch {
            val s = state
            val result = runCatching {
                if (s.isRegister) {
                    repo.register(
                        RegisterRequest(
                            phone_number = s.phone,
                            full_name = InputSanitizer.clean(s.fullName),
                            age = s.age.toInt(),
                            password = s.password,
                        ),
                    )
                } else {
                    repo.login(LoginRequest(phone_number = s.phone, password = s.password))
                }
            }
            result.fold(
                onSuccess = { resp ->
                    store.saveSession(resp.access_token, resp.refresh_token, resp.session_id, resp.onboarded)
                    state = state.copy(loading = false)
                    onAuthenticated(resp.onboarded)
                },
                onFailure = { e ->
                    state = state.copy(loading = false, error = e.message ?: res.string(R.string.error_auth_failed))
                },
            )
        }
    }

    /** Returning user unlocked by biometrics — session already exists locally. */
    fun completeBiometricUnlock(onAuthenticated: (onboarded: Boolean) -> Unit) {
        onAuthenticated(store.isOnboarded)
    }
}
