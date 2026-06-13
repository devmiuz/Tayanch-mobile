package uz.tayanch.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VacancyDto(
    val id: String,
    val title: String,
    val company: String,
    val salary: String,
    val location: String,
    val type: String,            // "Toʻliq stavka", "Masofaviy", ...
    val category: String,        // job sphere / category
    val level: String,           // Junior / Middle / Senior
    val tags: List<String> = emptyList(),
    val description: String = "",
    val requirements: List<String> = emptyList(),
    val posted_at: String = "",
    val is_applied: Boolean = false,
)

@Serializable
data class VacancyListResponse(
    val categories: List<String>,
    val vacancies: List<VacancyDto>,
)
