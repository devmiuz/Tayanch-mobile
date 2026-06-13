package uz.tayanch.app.data.security

/**
 * Pillar 1 — defense-in-depth input handling. This is the *client* half: cap
 * length to stop payload bloat and strip the obvious XSS/script vectors before a
 * value (a custom major, a quiz INPUT answer, a report note) leaves the device.
 * The server still treats every field as hostile and re-validates.
 */
object InputSanitizer {

    private const val MAX_LEN = 120
    private val scriptTag = Regex("(?i)</?\\s*script[^>]*>")
    private val angle = Regex("[<>]")

    fun clean(raw: String): String =
        raw.replace(scriptTag, "")
            .replace(angle, "")
            .trim()
            .take(MAX_LEN)

    /** A quick "does this look like an injection attempt?" check for the UI. */
    fun looksMalicious(raw: String): Boolean {
        val lower = raw.lowercase()
        return "<script" in lower ||
            "' or " in lower ||
            "; drop " in lower ||
            "1=1" in lower ||
            "--" in raw
    }
}
