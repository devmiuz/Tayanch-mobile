package uz.tayanch.app.ui.career

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.AssignmentSubmitRequest
import uz.tayanch.app.data.dto.CareerHubDto
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.ui.components.UiState

class CareerViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
) : ViewModel() {

    var state by mutableStateOf<UiState<CareerHubDto>>(UiState.Loading)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    var busy by mutableStateOf(false)
        private set

    init { load() }

    fun load() {
        state = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getCareerHub() }.fold(
                onSuccess = { state = UiState.Success(it) },
                onFailure = { state = UiState.Error(it.message ?: res.string(R.string.error_load_career)) },
            )
        }
    }

    fun clearMessage() { message = null }

    fun requestMock() {
        if (busy) return
        busy = true
        viewModelScope.launch {
            runCatching { repo.requestMockInterview() }.fold(
                onSuccess = { message = it.message },
                onFailure = { message = it.message ?: res.string(R.string.error_request_failed) },
            )
            busy = false
        }
    }

    /** Validates the link locally (Pillar 1) before the server stores it defanged. */
    fun submitAssignment(levelId: String, rawUrl: String) {
        if (busy) return
        val url = rawUrl.trim()
        val looksValid = url.startsWith("http", ignoreCase = true) || url.contains("github.com", ignoreCase = true)
        if (!looksValid) {
            message = res.string(R.string.error_invalid_github)
            return
        }
        busy = true
        viewModelScope.launch {
            runCatching { repo.submitAssignment(AssignmentSubmitRequest(levelId, url)) }.fold(
                onSuccess = {
                    message = it.message
                    load() // refresh so the status flips to PENDING
                },
                onFailure = { message = it.message ?: res.string(R.string.error_submission_failed) },
            )
            busy = false
        }
    }
}
