package uz.tayanch.app.ui.components

/** Minimal three-state container shared by every screen's view-model. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
