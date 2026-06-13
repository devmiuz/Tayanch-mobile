package uz.tayanch.app.data.dto

import kotlinx.serialization.Serializable

/** Everything the Career tab needs in one call. */
@Serializable
data class CareerHubDto(
    val arena: ArenaStatusDto,
    val mock_interview: MockInterviewStatusDto,
    val assignments: List<AssignmentStatusDto>,
)

/** 1v1 is gated behind a fixed XP floor so the matchmaking pool stays serious. */
@Serializable
data class ArenaStatusDto(
    val min_xp: Int,
    val current_xp: Int,
    val unlocked: Boolean,
)

@Serializable
data class MockInterviewStatusDto(
    val available: Boolean,
    val reached_level: String,
    val pending_requests: Int,
)

@Serializable
data class AssignmentStatusDto(
    val level_id: String,
    val level_label: String,
    val state: String,
    val github_url: String? = null,
)

@Serializable
data class AssignmentSubmitRequest(
    val level_id: String,
    val github_url: String,
)

@Serializable
data class SimpleStatusResponse(
    val status: String,
    val message: String,
)

/** Opponent surfaced by the matchmaker for a 1v1 battle. */
@Serializable
data class OpponentDto(
    val name: String,
    val level: String,
    val rank: Int,
)
