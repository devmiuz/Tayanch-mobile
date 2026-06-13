package uz.tayanch.app.data.security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.Locale

/**
 * Pillar 21 (client preview) — breached-credential screening via k-anonymity.
 *
 * Mirrors the backend's authoritative check so the user gets instant feedback:
 * only the first 5 hex chars of SHA-1(password) leave the device (over HTTPS,
 * Pillar 11), so Have I Been Pwned never learns the password or its full hash.
 * Best-effort: any network failure returns 0 (the server still re-checks).
 */
object BreachCheck {

    private const val RANGE_API = "https://api.pwnedpasswords.com/range/"

    suspend fun breachCount(password: String): Int = withContext(Dispatchers.IO) {
        if (password.isEmpty()) return@withContext 0
        val sha1 = MessageDigest.getInstance("SHA-1")
            .digest(password.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02X".format(it) }
        val prefix = sha1.substring(0, 5)
        val suffix = sha1.substring(5)

        runCatching {
            val conn = (URL(RANGE_API + prefix).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 2500
                readTimeout = 2500
                setRequestProperty("User-Agent", "Tayanch-Android")
                setRequestProperty("Add-Padding", "true")
            }
            conn.inputStream.bufferedReader().use { reader ->
                reader.lineSequence().forEach { line ->
                    val parts = line.split(":")
                    if (parts.size == 2 && parts[0].trim().uppercase(Locale.ROOT) == suffix) {
                        return@runCatching parts[1].trim().toIntOrNull() ?: 0
                    }
                }
            }
            0
        }.getOrDefault(0)
    }

    suspend fun isBreached(password: String): Boolean = breachCount(password) > 0
}
