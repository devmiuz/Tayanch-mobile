package uz.tayanch.app.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.QuestionType
import uz.tayanch.app.data.dto.QuizGradeResponse
import uz.tayanch.app.data.dto.QuizQuestionDto
import uz.tayanch.app.data.dto.QuizSubmitRequest
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.InputSanitizer
import uz.tayanch.app.data.security.SecureClock
import uz.tayanch.app.ui.components.UiState

/** The per-question UI phase. */
sealed interface QuizPhase {
    data object Answering : QuizPhase
    data class Reviewing(val grade: QuizGradeResponse?, val savedOffline: Boolean) : QuizPhase
}

class QuizViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
) : ViewModel() {

    var loadState by mutableStateOf<UiState<Unit>>(UiState.Loading)
        private set

    private var questions: List<QuizQuestionDto> = emptyList()

    var quizTitle by mutableStateOf(""); private set
    var index by mutableIntStateOf(0); private set
    var phase by mutableStateOf<QuizPhase>(QuizPhase.Answering); private set
    var selectedOptions by mutableStateOf<Set<String>>(emptySet()); private set
    var inputAnswer by mutableStateOf(""); private set
    var grading by mutableStateOf(false); private set

    var correctCount by mutableIntStateOf(0); private set
    var totalXpEarned by mutableIntStateOf(0); private set
    var isComplete by mutableStateOf(false); private set
    var pendingSyncCount by mutableIntStateOf(0); private set

    private var questionStartMs = 0L

    val current: QuizQuestionDto? get() = questions.getOrNull(index)
    val total: Int get() = questions.size
    val isLast: Boolean get() = index >= questions.lastIndex

    fun load(contentId: String) {
        if (questions.isNotEmpty()) return
        loadState = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getContent(contentId) }.fold(
                onSuccess = { detail ->
                    questions = detail.questions
                    quizTitle = detail.title
                    if (questions.isEmpty()) {
                        loadState = UiState.Error(res.string(R.string.quiz_no_questions))
                    } else {
                        startQuestion()
                        loadState = UiState.Success(Unit)
                    }
                },
                onFailure = { loadState = UiState.Error(it.message ?: res.string(R.string.error_load_quiz)) },
            )
        }
    }

    private fun startQuestion() {
        phase = QuizPhase.Answering
        selectedOptions = emptySet()
        inputAnswer = ""
        questionStartMs = SecureClock.nowMonotonic()
    }

    fun toggleOption(optionId: String) {
        val q = current ?: return
        if (phase !is QuizPhase.Answering) return
        selectedOptions = when (q.type) {
            QuestionType.MULTI ->
                if (optionId in selectedOptions) selectedOptions - optionId else selectedOptions + optionId
            else -> setOf(optionId)
        }
    }

    fun setInput(value: String) {
        if (phase is QuizPhase.Answering) inputAnswer = value.take(80)
    }

    val canSubmit: Boolean
        get() {
            val q = current ?: return false
            return when (q.type) {
                QuestionType.INPUT -> inputAnswer.isNotBlank()
                else -> selectedOptions.isNotEmpty()
            }
        }

    /** Submit just the choice. Server grades it; timing uses the monotonic clock. */
    fun submit(auto: Boolean = false) {
        val q = current ?: return
        if (phase !is QuizPhase.Answering || grading) return
        if (!auto && !canSubmit) return
        grading = true
        val elapsed = SecureClock.nowMonotonic() - questionStartMs
        val submission = QuizSubmitRequest(
            question_id = q.id,
            selected_option_ids = selectedOptions.toList(),
            input_answer = InputSanitizer.clean(inputAnswer).ifBlank { null },
            elapsed_ms = elapsed,
        )
        viewModelScope.launch {
            runCatching { repo.gradeQuestion(submission) }.fold(
                onSuccess = { grade ->
                    if (grade.is_correct) {
                        correctCount++
                        totalXpEarned += grade.xp_earned
                    }
                    phase = QuizPhase.Reviewing(grade, savedOffline = false)
                },
                onFailure = {
                    // Pillar 5 — offline resilience: cache and let WorkManager retry
                    // later. The user keeps their place and never loses progress.
                    OfflineAnswerQueue.enqueue(submission)
                    pendingSyncCount = OfflineAnswerQueue.size
                    phase = QuizPhase.Reviewing(grade = null, savedOffline = true)
                },
            )
            grading = false
        }
    }

    fun next() {
        if (isLast) {
            isComplete = true
        } else {
            index++
            startQuestion()
        }
    }
}
