package uz.tayanch.app.ui.content

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.CompletionStore
import uz.tayanch.app.data.dto.ContentDetailDto
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.ui.components.UiState

class ContentViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
    private val completion: CompletionStore,
) : ViewModel() {

    var state by mutableStateOf<UiState<ContentDetailDto>>(UiState.Loading)
        private set

    private var loadedId: String? = null

    /** Called when the user actually completes the content (not on forfeit-leave). */
    fun markCompleted() { loadedId?.let(completion::markCompleted) }

    fun load(contentId: String) {
        if (loadedId == contentId && state is UiState.Success) return
        loadedId = contentId
        state = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getContent(contentId) }.fold(
                onSuccess = { state = UiState.Success(it) },
                onFailure = { state = UiState.Error(it.message ?: res.string(R.string.error_load_content)) },
            )
        }
    }
}
