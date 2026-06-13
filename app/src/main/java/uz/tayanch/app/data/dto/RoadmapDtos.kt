package uz.tayanch.app.data.dto

import kotlinx.serialization.Serializable

/** The five content kinds a topic is built from, in unlock order. */
object ContentType {
    const val MARKDOWN = "MARKDOWN"   // AI-generated native article
    const val WEBLINK = "WEBLINK"     // external article in a hardened WebView
    const val VIDEO = "VIDEO"         // embedded video
    const val FLASHCARD = "FLASHCARD" // swipeable recall deck
    const val QUIZ = "QUIZ"           // graded, server-validated test
}

object AssignmentState {
    const val LOCKED = "LOCKED"
    const val AVAILABLE = "AVAILABLE"
    const val PENDING = "PENDING"
    const val ACCEPTED = "ACCEPTED"
    const val REJECTED = "REJECTED"
}

@Serializable
data class RoadmapResponse(
    val levels: List<LevelDto>,
)

/** A level (Junior, Middle, …) groups topics and ends with a "boss" assignment. */
@Serializable
data class LevelDto(
    val level_id: String,
    val title: String,
    val rank_label: String,
    val required_xp: Int,
    val is_unlocked: Boolean,
    val topics: List<TopicDto>,
    val assignment: AssignmentTaskDto,
)

@Serializable
data class TopicDto(
    val topic_id: String,
    val title: String,
    val order: Int,
    val contents: List<ContentNodeDto>,
)

/**
 * One node in a topic row. [is_locked] is advisory only — the server re-checks
 * prerequisites on open (Pillar: server-side state enforcement), so flipping it
 * on a decompiled client gets you a 403, not a free unlock.
 */
@Serializable
data class ContentNodeDto(
    val content_id: String,
    val type: String,
    val title: String,
    val reward_xp: Int,
    val is_completed: Boolean,
    val is_locked: Boolean,
    val estimated_minutes: Int = 0,
)

@Serializable
data class AssignmentTaskDto(
    val level_id: String,
    val title: String,
    val state: String,
)
