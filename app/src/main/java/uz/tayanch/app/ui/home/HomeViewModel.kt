package uz.tayanch.app.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.CompletionStore
import uz.tayanch.app.data.Interests
import uz.tayanch.app.data.dto.RoadmapResponse
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.SecureSessionStore
import uz.tayanch.app.ui.components.UiState

class HomeViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
    store: SecureSessionStore,
    private val completion: CompletionStore,
) : ViewModel() {

    /** The interests the user picked at onboarding (≥1; defaults to all four). */
    val interests: List<Interests.Interest> = Interests.resolve(store.interestIds())

    var activeInterestId by mutableStateOf(interests.first().id)
        private set

    var state by mutableStateOf<UiState<RoadmapResponse>>(UiState.Loading)
        private set

    /** Raw roadmap for the active interest, before the completion overlay. */
    private var rawRoadmap: RoadmapResponse? = null

    init {
        load()
        // Re-overlay whenever the user finishes a content node, so the green check
        // appears immediately on return — for whichever interest is showing.
        viewModelScope.launch {
            completion.completed.collect { ids ->
                rawRoadmap?.let { state = UiState.Success(applyCompletion(it, ids)) }
            }
        }
    }

    /** Home shows one roadmap at a time; the switcher chips pick which interest. */
    fun selectInterest(id: String) {
        if (id != activeInterestId) {
            activeInterestId = id
            load()
        }
    }

    fun load() {
        state = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getRoadmap(activeInterestId) }.fold(
                onSuccess = {
                    rawRoadmap = it
                    state = UiState.Success(applyCompletion(it, completion.completed.value))
                },
                onFailure = { state = UiState.Error(it.message ?: res.string(R.string.error_load_roadmap)) },
            )
        }
    }

    /** Mark a node completed if its id is in the [CompletionStore] (across interests). */
    private fun applyCompletion(roadmap: RoadmapResponse, done: Set<String>): RoadmapResponse {
        if (done.isEmpty()) return roadmap
        return roadmap.copy(
            levels = roadmap.levels.map { level ->
                level.copy(
                    topics = level.topics.map { topic ->
                        topic.copy(
                            contents = topic.contents.map { node ->
                                if (node.content_id in done && !node.is_completed) {
                                    node.copy(is_completed = true)
                                } else {
                                    node
                                }
                            },
                        )
                    },
                )
            },
        )
    }
}
