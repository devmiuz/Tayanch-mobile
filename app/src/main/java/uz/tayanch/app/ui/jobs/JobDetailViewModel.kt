package uz.tayanch.app.ui.jobs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.VacancyDto
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.ui.components.UiState

class JobDetailViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
) : ViewModel() {

    var state by mutableStateOf<UiState<VacancyDto>>(UiState.Loading); private set
    var applied by mutableStateOf(false); private set
    var applying by mutableStateOf(false); private set

    private var loadedId: String? = null

    fun load(id: String) {
        if (loadedId == id && state is UiState.Success) return
        loadedId = id
        state = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getVacancy(id) }.fold(
                onSuccess = { state = UiState.Success(it); applied = it.is_applied },
                onFailure = { state = UiState.Error(it.message ?: res.string(R.string.error_load_vacancy)) },
            )
        }
    }

    fun apply(id: String) {
        if (applied || applying) return
        applying = true
        viewModelScope.launch {
            runCatching { repo.applyVacancy(id) }
                .onSuccess { applied = true }
            applying = false
        }
    }
}
