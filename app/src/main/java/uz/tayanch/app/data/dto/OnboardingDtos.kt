package uz.tayanch.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MajorDto(
    val id: String,
    val name: String,
)

@Serializable
data class MajorsResponse(
    val majors: List<MajorDto>,
)

@Serializable
data class InterestDto(
    val id: String,
    val name: String,
)

/**
 * Interests are fetched in pages (Pillar: lazy loading). When the backend finds
 * fewer than 5 interests for a major it asks an LLM to generate professional
 * spheres and persists them — modelled here by [ai_generated].
 */
@Serializable
data class InterestsResponse(
    val interests: List<InterestDto>,
    val page: Int,
    val has_more: Boolean,
    val ai_generated: Boolean = false,
)

@Serializable
data class OnboardingRequest(
    val major: String,
    val interest_ids: List<String>,
    val expected_salary: Int,
)
