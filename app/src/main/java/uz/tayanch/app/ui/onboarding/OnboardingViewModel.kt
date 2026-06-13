package uz.tayanch.app.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.data.dto.InterestDto
import uz.tayanch.app.data.dto.MajorDto
import uz.tayanch.app.data.dto.OnboardingRequest
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.InputSanitizer
import uz.tayanch.app.data.security.SecureSessionStore

data class OnboardingUiState(
    val majorsLoading: Boolean = true,
    val majors: List<MajorDto> = emptyList(),
    val majorQuery: String = "",
    val selectedMajor: String? = null,
    val interestsLoading: Boolean = false,
    val interests: List<InterestDto> = emptyList(),
    val selectedInterestIds: Set<String> = emptySet(),
    val interestsHasMore: Boolean = false,
    val aiGenerated: Boolean = false,
    val salary: String = "",
    val submitting: Boolean = false,
    val error: String? = null,
)

class OnboardingViewModel(
    private val repo: TayanchRepository,
    private val store: SecureSessionStore,
) : ViewModel() {

    private var interestsPage = 0

    var state by mutableStateOf(OnboardingUiState())
        private set

    init {
        loadMajors()
    }

    private fun loadMajors() {
        viewModelScope.launch {
            runCatching { repo.getMajors() }.fold(
                onSuccess = { state = state.copy(majorsLoading = false, majors = it.majors) },
                onFailure = { state = state.copy(majorsLoading = false, error = it.message) },
            )
        }
    }

    val filteredMajors: List<MajorDto>
        get() = state.majorQuery.let { q ->
            if (q.isBlank()) state.majors
            else state.majors.filter { it.name.contains(q, ignoreCase = true) }
        }

    /** Whether to show the "Add custom major" affordance (no close match exists). */
    val showAddMajor: Boolean
        get() = state.majorQuery.isNotBlank() &&
            state.majors.none { it.name.equals(state.majorQuery.trim(), ignoreCase = true) }

    fun onMajorQuery(v: String) { state = state.copy(majorQuery = v.take(60)) }

    fun selectMajor(name: String) {
        state = state.copy(selectedMajor = name, majorQuery = name)
        loadInterests(reset = true)
    }

    /**
     * Custom major. The backend would run Levenshtein/trigram dedup before
     * inserting (so "Software Eng" maps onto "Software Engineering"); here we just
     * sanitize the raw text (Pillar 1) and treat it as the selection.
     */
    fun addCustomMajor() {
        val clean = InputSanitizer.clean(state.majorQuery)
        if (clean.isNotBlank()) selectMajor(clean)
    }

    private fun loadInterests(reset: Boolean) {
        if (reset) interestsPage = 0
        val nextPage = interestsPage + 1
        state = state.copy(interestsLoading = true)
        viewModelScope.launch {
            runCatching { repo.getInterests(nextPage) }.fold(
                onSuccess = { resp ->
                    interestsPage = resp.page
                    val merged = if (reset) resp.interests else state.interests + resp.interests
                    state = state.copy(
                        interestsLoading = false,
                        interests = merged,
                        interestsHasMore = resp.has_more,
                        aiGenerated = state.aiGenerated || resp.ai_generated,
                    )
                },
                onFailure = { state = state.copy(interestsLoading = false, error = it.message) },
            )
        }
    }

    fun loadMoreInterests() {
        if (state.interestsHasMore && !state.interestsLoading) loadInterests(reset = false)
    }

    fun toggleInterest(id: String) {
        val current = state.selectedInterestIds
        state = state.copy(
            selectedInterestIds = if (id in current) current - id else current + id,
        )
    }

    fun onSalary(v: String) { state = state.copy(salary = v.filter(Char::isDigit).take(7)) }

    val canSubmit: Boolean
        get() = state.selectedMajor != null &&
            state.selectedInterestIds.isNotEmpty() &&
            (state.salary.toIntOrNull() ?: 0) > 0

    fun submit(onDone: () -> Unit) {
        if (!canSubmit || state.submitting) return
        state = state.copy(submitting = true, error = null)
        viewModelScope.launch {
            runCatching {
                repo.submitOnboarding(
                    OnboardingRequest(
                        major = state.selectedMajor!!,
                        interest_ids = state.selectedInterestIds.toList(),
                        expected_salary = state.salary.toInt(),
                    ),
                )
            }.fold(
                onSuccess = {
                    store.isOnboarded = true
                    state = state.copy(submitting = false)
                    onDone()
                },
                onFailure = { state = state.copy(submitting = false, error = it.message) },
            )
        }
    }
}
