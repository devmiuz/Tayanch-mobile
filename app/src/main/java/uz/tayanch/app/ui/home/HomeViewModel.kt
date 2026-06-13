package uz.tayanch.app.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.RoadmapResponse
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.ui.components.UiState

class HomeViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
) : ViewModel() {

    var state by mutableStateOf<UiState<RoadmapResponse>>(UiState.Loading)
        private set

    init { load() }

    fun load() {
        state = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getRoadmap() }.fold(
                onSuccess = { state = UiState.Success(it) },
                onFailure = { state = UiState.Error(it.message ?: res.string(R.string.error_load_roadmap)) },
            )
        }
    }
}
