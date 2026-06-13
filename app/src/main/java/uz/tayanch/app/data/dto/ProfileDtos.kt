package uz.tayanch.app.data.dto

import kotlinx.serialization.Serializable

/**
 * Powers the Profile tab. The server is the sole authority for xp/rank/level
 * (Pillar 9 — Server-Side State Authority); the client only renders them.
 */
@Serializable
data class UserProfileDto(
    val id: String,
    val full_name: String,
    val current_level: String,
    val target_level: String,
    val current_xp: Int,
    val expected_salary: Int,
    val global_rank: Int,
    val ai_motivation: String,
    val velocity: CareerVelocityDto,
    val focus: FocusStatsDto,
    val badges: List<BadgeDto>,
)

/** "At your current pace you reach X in N months" — money/time motivation. */
@Serializable
data class CareerVelocityDto(
    val target_salary: Int,
    val weekly_xp: Int,
    val projection_text: String,
)

@Serializable
data class FocusStatsDto(
    val average_focus_time: String,
    val distraction_rate: String,
)

@Serializable
data class BadgeDto(
    val id: String,
    val title: String,
    val emoji: String,
    val earned: Boolean,
)

@Serializable
data class LeaderboardResponse(
    val scope: String,
    val leaderboard: List<LeaderboardEntryDto>,
)

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val name: String,
    val level: String,
    val xp: Int,
    val is_me: Boolean = false,
)
