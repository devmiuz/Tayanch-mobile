package uz.tayanch.app.ui.battle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.tayanch.app.R
import uz.tayanch.app.core.ResourceProvider
import uz.tayanch.app.data.dto.OpponentDto
import uz.tayanch.app.data.dto.QuizQuestionDto
import uz.tayanch.app.data.dto.QuizSubmitRequest
import uz.tayanch.app.data.repository.TayanchRepository
import uz.tayanch.app.data.security.SecureClock
import uz.tayanch.app.ui.components.UiState
import kotlin.random.Random

/**
 * 1v1 Arena. The flow mirrors the real WebSocket design (JOIN → MATCH_FOUND →
 * ROUND_START → SUBMIT_ANSWER → ROUND_RESULT → MATCH_END). My answers are graded
 * server-side (the same Zero-Trust grader as the solo quiz); the opponent is
 * simulated here. Ties break on aggregate time spent on correct answers — so
 * faster recall wins (Pillar 4 timing via the monotonic clock).
 */
class BattleViewModel(
    private val repo: TayanchRepository,
    private val res: ResourceProvider,
) : ViewModel() {

    var loadState by mutableStateOf<UiState<Unit>>(UiState.Loading); private set
    var opponent by mutableStateOf<OpponentDto?>(null); private set

    private var questions: List<QuizQuestionDto> = emptyList()

    var index by mutableIntStateOf(0); private set
    var inResult by mutableStateOf(false); private set
    var selected by mutableStateOf<String?>(null); private set
    var grading by mutableStateOf(false); private set

    var myScore by mutableIntStateOf(0); private set
    var oppScore by mutableIntStateOf(0); private set
    private var myCorrectTimeMs = 0L
    private var oppCorrectTimeMs = 0L

    var lastMyCorrect by mutableStateOf<Boolean?>(null); private set
    var lastOppCorrect by mutableStateOf(false); private set

    var isComplete by mutableStateOf(false); private set
    var winner by mutableStateOf(""); private set
    var resultDetail by mutableStateOf(""); private set

    private var roundStartMs = 0L

    val current: QuizQuestionDto? get() = questions.getOrNull(index)
    val total: Int get() = questions.size
    val isLast: Boolean get() = index >= questions.lastIndex

    fun load() {
        loadState = UiState.Loading
        viewModelScope.launch {
            runCatching {
                val opp = repo.findOpponent()
                val deck = repo.getBattleDeck()
                opp to deck.questions
            }.fold(
                onSuccess = { (opp, qs) ->
                    opponent = opp
                    questions = qs
                    roundStartMs = SecureClock.nowMonotonic()
                    loadState = UiState.Success(Unit)
                },
                onFailure = { loadState = UiState.Error(it.message ?: res.string(R.string.error_matchmaking)) },
            )
        }
    }

    fun choose(optionId: String) {
        if (!inResult && !grading) selected = optionId
    }

    fun submit(auto: Boolean = false) {
        val q = current ?: return
        if (inResult || grading) return
        if (!auto && selected == null) return
        grading = true
        val elapsed = SecureClock.nowMonotonic() - roundStartMs
        viewModelScope.launch {
            val grade = runCatching {
                repo.gradeQuestion(
                    QuizSubmitRequest(
                        question_id = q.id,
                        selected_option_ids = listOfNotNull(selected),
                        input_answer = null,
                        elapsed_ms = elapsed,
                    ),
                )
            }.getOrNull()

            val myCorrect = grade?.is_correct == true
            if (myCorrect) { myScore++; myCorrectTimeMs += elapsed }

            // Simulated opponent: ~62% accurate, answers in 2–8s.
            val oppCorrect = Random.nextInt(100) < 62
            val oppTime = Random.nextLong(2000, 8000)
            if (oppCorrect) { oppScore++; oppCorrectTimeMs += oppTime }

            lastMyCorrect = myCorrect
            lastOppCorrect = oppCorrect
            inResult = true
            grading = false
        }
    }

    fun next() {
        if (isLast) finish() else {
            index++
            selected = null
            inResult = false
            roundStartMs = SecureClock.nowMonotonic()
        }
    }

    private fun finish() {
        winner = when {
            myScore > oppScore -> "YOU"
            oppScore > myScore -> "OPPONENT"
            myCorrectTimeMs < oppCorrectTimeMs -> "YOU"
            oppCorrectTimeMs < myCorrectTimeMs -> "OPPONENT"
            else -> "DRAW"
        }
        resultDetail = if (myScore == oppScore && winner != "DRAW") {
            val diff = kotlin.math.abs(myCorrectTimeMs - oppCorrectTimeMs) / 1000.0
            res.string(R.string.battle_tie_speed, "%.1f".format(diff))
        } else {
            res.string(R.string.battle_final_score, myScore, oppScore)
        }
        isComplete = true
    }
}
