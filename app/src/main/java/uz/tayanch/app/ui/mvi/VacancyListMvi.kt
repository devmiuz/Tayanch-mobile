package uz.tayanch.app.ui.mvi

import uz.tayanch.app.ui.theme.TayanchControl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.tayanch.app.ui.components.UiState

/**
 * Reference MVI (Model-View-Intent) implementation used by the thesis to explain
 * the architecture. Every user action becomes an [VacancyIntent]; the ViewModel
 * reduces it into a single, immutable [UiState] exposed as a [StateFlow]; the
 * Composable only observes that state and re-draws (UDF — one-way data flow).
 */
data class Vacancy(val id: String, val title: String, val company: String, val salary: String)

/** Intent — every action the user can trigger flows in through here. */
sealed interface VacancyIntent {
    data object Load : VacancyIntent
    data object Retry : VacancyIntent
}

class VacancyMviViewModel : ViewModel() {

    // Model — a single immutable UiState, exposed read-only as a StateFlow.
    private val _state = MutableStateFlow<UiState<List<Vacancy>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Vacancy>>> = _state.asStateFlow()

    init { onIntent(VacancyIntent.Load) }

    /** The single entry point: View sends Intents, the reducer produces new State. */
    fun onIntent(intent: VacancyIntent) {
        when (intent) {
            VacancyIntent.Load, VacancyIntent.Retry -> load()
        }
    }

    private fun load() {
        _state.update { UiState.Loading }
        viewModelScope.launch {
            runCatching {
                delay(400) // simulates the FastAPI request
                listOf(
                    Vacancy("1", "Junior Android Developer", "EPAM", "8 000 000 UZS"),
                    Vacancy("2", "SOC Analyst", "UZINFOCOM", "12 000 000 UZS"),
                    Vacancy("3", "Backend (FastAPI)", "PayMe", "15 000 000 UZS"),
                )
            }.fold(
                onSuccess = { list -> _state.update { UiState.Success(list) } },
                onFailure = { e -> _state.update { UiState.Error(e.message ?: "Xatolik") } },
            )
        }
    }
}

/**
 * View — observes the single StateFlow and renders Loading / Error / Success.
 * It never holds its own copies of isLoading/isError/data, so the three states
 * can never contradict each other.
 */
@Composable
fun VacancyListScreen(vm: VacancyMviViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }

        is UiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Button(shape = TayanchControl.Shape, onClick = { vm.onIntent(VacancyIntent.Retry) }) { Text("Qayta urinish") }
        }

        is UiState.Success -> LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            items(s.data, key = { it.id }) { vacancy ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
                        Text(vacancy.title, style = MaterialTheme.typography.titleMedium)
                        Text(vacancy.company, style = MaterialTheme.typography.bodyMedium)
                        Text(vacancy.salary, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
