package uz.tayanch.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Tracks which content nodes the user has finished, by `content_id`. The roadmap
 * JSON only carries the *seed* completion state; once a user actually completes
 * an article / video / flashcard deck / quiz we record its id here and HomeScreen
 * overlays it onto whichever interest's roadmap is showing — so the green check
 * appears for every interest, not just the pre-seeded one.
 *
 * Process-scoped (single Koin instance); ids are namespaced per interest
 * (`py-md-syntax`, `gd-md-comp`, `md-auth`, …) so there are no cross-interest
 * collisions.
 */
class CompletionStore {

    private val _completed = MutableStateFlow<Set<String>>(emptySet())
    val completed: StateFlow<Set<String>> = _completed.asStateFlow()

    fun markCompleted(contentId: String) {
        if (contentId.isNotBlank()) _completed.update { it + contentId }
    }
}
