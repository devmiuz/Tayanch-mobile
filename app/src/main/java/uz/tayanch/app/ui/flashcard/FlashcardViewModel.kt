package uz.tayanch.app.ui.flashcard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.FlashcardDto
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.SecureClock
import uz.tayanch.app.ui.components.UiState

class FlashcardViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
) : ViewModel() {

    var loadState by mutableStateOf<UiState<Unit>>(UiState.Loading)
        private set

    private var deck: List<FlashcardDto> = emptyList()
    var queue by mutableStateOf<List<Int>>(emptyList())
        private set
    var rewardXp by mutableIntStateOf(0)
        private set
    var tooFastStrikes by mutableIntStateOf(0)
        private set
    var frozen by mutableStateOf(false)
        private set

    val total: Int get() = deck.size
    val current: FlashcardDto? get() = queue.firstOrNull()?.let { deck.getOrNull(it) }
    val isComplete: Boolean get() = deck.isNotEmpty() && queue.isEmpty()

    private var shownAt = 0L

    fun load(contentId: String) {
        if (deck.isNotEmpty()) return
        loadState = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getContent(contentId) }.fold(
                onSuccess = { detail ->
                    deck = detail.flashcards
                    rewardXp = detail.reward_xp
                    queue = deck.indices.toList()
                    resetShownClock()
                    loadState = UiState.Success(Unit)
                },
                onFailure = { loadState = UiState.Error(it.message ?: res.string(R.string.error_load_deck)) },
            )
        }
    }

    fun resetShownClock() { shownAt = SecureClock.nowMonotonic() }

    /**
     * Pillar 22 (anti-farming): a "Know it" within 800ms of the card appearing is
     * biologically impossible reading speed. Three strikes freezes & resets the
     * deck so XP can't be farmed by rapid-fire swiping.
     */
    fun onResult(known: Boolean) {
        if (frozen || queue.isEmpty()) return
        val elapsed = SecureClock.nowMonotonic() - shownAt
        if (known && elapsed < 800) {
            tooFastStrikes++
            if (tooFastStrikes >= 3) { frozen = true; return }
        }
        queue = if (known) queue.drop(1) else queue.drop(1) + queue.first()
        resetShownClock()
    }

    fun acknowledgeFreezeAndReset() {
        frozen = false
        tooFastStrikes = 0
        queue = deck.indices.toList()
        resetShownClock()
    }
}
