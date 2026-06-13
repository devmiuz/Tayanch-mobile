package uz.tayanch.app.data.dto

import kotlinx.serialization.Serializable

/**
 * A single, flexible content payload deserialized straight from the JSONB-style
 * mock. Only the field matching [type] is populated. Crucially, quiz options
 * carry NO "is_correct" flag — the client never sees the answer key
 * (Pillar 3, Zero-Trust grading).
 */
@Serializable
data class ContentDetailDto(
    val content_id: String,
    val type: String,
    val title: String,
    val reward_xp: Int,
    val markdown: String? = null,
    val url: String? = null,
    val estimated_minutes: Int = 0,
    val flashcards: List<FlashcardDto> = emptyList(),
    val questions: List<QuizQuestionDto> = emptyList(),
)

@Serializable
data class FlashcardDto(
    val id: String,
    val front: String,
    val back: String,
    val code: String? = null,
    val language: String? = null,
)

object QuestionType {
    const val SINGLE = "SINGLE"
    const val MULTI = "MULTI"
    const val INPUT = "INPUT"
    const val TRUE_FALSE = "TRUE_FALSE"
}

@Serializable
data class QuizQuestionDto(
    val id: String,
    val type: String,
    val text: String,
    val code: String? = null,
    val language: String? = null,
    val options: List<QuizOptionDto> = emptyList(),
    val duration_sec: Int = 30,
)

@Serializable
data class QuizOptionDto(
    val id: String,
    val text: String,
)

/**
 * What the client submits. [elapsed_ms] is measured with SystemClock
 * .elapsedRealtime() so changing the phone clock can't buy extra time
 * (Pillar 4). The server independently re-checks its own timestamp.
 */
@Serializable
data class QuizSubmitRequest(
    val question_id: String,
    val selected_option_ids: List<String> = emptyList(),
    val input_answer: String? = null,
    val elapsed_ms: Long,
)

/** Server grades and returns the verdict — the only place correctness lives. */
@Serializable
data class QuizGradeResponse(
    val question_id: String,
    val is_correct: Boolean,
    val correct_option_ids: List<String> = emptyList(),
    val explanation: String,
    val xp_earned: Int,
    val new_total_xp: Int,
)
