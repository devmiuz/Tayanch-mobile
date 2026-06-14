package uz.tayanch.app.data.mock

/**
 * SERVER-SIDE answer key entry — never part of any client DTO. The MockEngine
 * consults it to grade a submission and returns only the verdict (Pillar 3 —
 * Zero-Trust grading). INPUT answers accept Uzbek and English synonyms.
 *
 * Top-level (shared) so every per-interest content file can contribute entries
 * to the merged [MockData.answerKey].
 */
data class AnswerSpec(
    val correctOptionIds: Set<String> = emptySet(),
    val correctInputs: Set<String> = emptySet(),
    val explanation: String,
    val xp: Int,
)
