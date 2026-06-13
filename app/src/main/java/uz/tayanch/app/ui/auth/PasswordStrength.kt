package uz.tayanch.app.ui.auth

import androidx.compose.ui.graphics.Color

/**
 * Pillar 21 — modern password entropy (a compact, zxcvbn-flavoured estimator).
 * Rather than the discouraged "1 upper + 1 symbol" rules, it rewards length and
 * variety and punishes common patterns, returning a 0..4 score that gates the
 * Register button. (The server adds the breached-credential k-Anonymity check.)
 */
data class PasswordStrength(val score: Int, val label: String, val color: Color)

private val veryWeak = PasswordStrength(0, "Too weak", Color(0xFFE53E3E))
private val weak = PasswordStrength(1, "Weak", Color(0xFFED8936))
private val fair = PasswordStrength(2, "Fair", Color(0xFFECC94B))
private val good = PasswordStrength(3, "Good", Color(0xFF48BB78))
private val strong = PasswordStrength(4, "Strong", Color(0xFF1B5E4F))

private val commonPasswords = setOf(
    "password", "123456", "qwerty", "111111", "12345678",
    "iloveyou", "admin", "welcome", "passw0rd", "abc123",
)

fun estimatePassword(pw: String): PasswordStrength {
    if (pw.isEmpty()) return veryWeak
    if (pw.lowercase() in commonPasswords) return veryWeak

    var score = 0
    if (pw.length >= 8) score++
    if (pw.length >= 12) score++
    if (pw.length >= 16) score++ // NIST encourages length / passphrases

    val classes = listOf(
        pw.any { it.isDigit() },
        pw.any { it.isLowerCase() },
        pw.any { it.isUpperCase() },
        pw.any { !it.isLetterOrDigit() },
    ).count { it }
    if (classes >= 3) score++

    // Penalise trivial patterns.
    if (Regex("(.)\\1\\1").containsMatchIn(pw)) score-- // aaa, 111
    if (pw.lowercase().contains("qwerty") || pw.contains("1234")) score--

    return when (score.coerceIn(0, 4)) {
        0 -> veryWeak
        1 -> weak
        2 -> fair
        3 -> good
        else -> strong
    }
}
