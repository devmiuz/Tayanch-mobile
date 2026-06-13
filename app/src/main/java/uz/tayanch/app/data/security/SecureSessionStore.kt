package uz.tayanch.app.data.security

import android.content.Context
import androidx.core.content.edit

/**
 * Pillar 1 — Secure token storage. Access/refresh/session tokens are encrypted
 * with a Keystore-held key ([KeystoreCrypto]) before they touch disk, so the
 * on-disk SharedPreferences file holds only ciphertext.
 */
class SecureSessionStore(context: Context) {

    private val prefs = context.getSharedPreferences("tayanch_secure_session", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.contains(KEY_ACCESS)
        private set(_) {}

    var isOnboarded: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDED, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDED, value) }

    /** True once any session has ever been created — gates the biometric unlock. */
    val hasEnrolledSession: Boolean
        get() = prefs.contains(KEY_ACCESS)

    fun saveSession(access: String, refresh: String, sessionId: String, onboarded: Boolean) {
        prefs.edit {
            putString(KEY_ACCESS, KeystoreCrypto.encrypt(access))
            putString(KEY_REFRESH, KeystoreCrypto.encrypt(refresh))
            putString(KEY_SESSION, KeystoreCrypto.encrypt(sessionId))
            putBoolean(KEY_ONBOARDED, onboarded)
        }
    }

    /**
     * Pillar 16 — refresh rotation: after /auth/refresh mints a new pair, replace
     * the access + refresh tokens in place (the session id is unchanged).
     */
    fun updateTokens(access: String, refresh: String) {
        prefs.edit {
            putString(KEY_ACCESS, KeystoreCrypto.encrypt(access))
            putString(KEY_REFRESH, KeystoreCrypto.encrypt(refresh))
        }
    }

    fun accessToken(): String? = prefs.getString(KEY_ACCESS, null)?.let(KeystoreCrypto::decrypt)
    fun refreshToken(): String? = prefs.getString(KEY_REFRESH, null)?.let(KeystoreCrypto::decrypt)
    fun sessionId(): String? = prefs.getString(KEY_SESSION, null)?.let(KeystoreCrypto::decrypt)

    fun clear() = prefs.edit { clear() }

    private companion object {
        const val KEY_ACCESS = "access"
        const val KEY_REFRESH = "refresh"
        const val KEY_SESSION = "session"
        const val KEY_ONBOARDED = "onboarded"
    }
}
