package uz.tayanch.app.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.LeaderboardResponse
import uz.tayanch.app.data.dto.UserProfileDto
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.SecureSessionStore
import uz.tayanch.app.ui.components.UiState

data class ProfileData(
    val profile: UserProfileDto,
    val leaderboard: LeaderboardResponse,
)

class ProfileViewModel(
    private val repo: TayanchRepository,
    private val store: SecureSessionStore,
    private val res: ResourceProvider,
) : ViewModel() {

    var state by mutableStateOf<UiState<ProfileData>>(UiState.Loading)
        private set

    var scope by mutableStateOf("major")
        private set

    init { load() }

    fun load() {
        state = UiState.Loading
        viewModelScope.launch {
            runCatching {
                // /users/me — identity comes from the JWT, never a URL id (Pillar 10, IDOR).
                val profile = repo.getProfile()
                val board = repo.getLeaderboard(scope)
                ProfileData(profile, board)
            }.fold(
                onSuccess = { state = UiState.Success(it) },
                onFailure = { state = UiState.Error(it.message ?: res.string(R.string.error_load_profile)) },
            )
        }
    }

    fun selectScope(newScope: String) {
        if (newScope == scope) return
        scope = newScope
        val current = state
        if (current !is UiState.Success) return
        viewModelScope.launch {
            runCatching { repo.getLeaderboard(newScope) }
                .onSuccess { state = UiState.Success(current.data.copy(leaderboard = it)) }
        }
    }

    fun logout(onDone: () -> Unit) {
        store.clear()
        onDone()
    }
}
