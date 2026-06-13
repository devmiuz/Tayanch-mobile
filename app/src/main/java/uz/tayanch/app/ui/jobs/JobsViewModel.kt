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
import uz.tayanch.app.data.dto.VacancyListResponse
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.ui.components.UiState

class JobsViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
) : ViewModel() {

    var state by mutableStateOf<UiState<VacancyListResponse>>(UiState.Loading); private set
    var query by mutableStateOf(""); private set
    var category by mutableStateOf("Barchasi"); private set

    init { load() }

    fun load() {
        state = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getVacancies() }.fold(
                onSuccess = { state = UiState.Success(it) },
                onFailure = { state = UiState.Error(it.message ?: res.string(R.string.error_load_jobs)) },
            )
        }
    }

    fun onQuery(q: String) { query = q.take(40) }
    fun selectCategory(c: String) { category = c }

    /** Client-side search + category filter over the loaded list. */
    fun filter(all: List<VacancyDto>): List<VacancyDto> = all.filter { v ->
        (category == "Barchasi" || v.category == category) &&
            (query.isBlank() ||
                v.title.contains(query, true) ||
                v.company.contains(query, true) ||
                v.tags.any { it.contains(query, true) })
    }
}
