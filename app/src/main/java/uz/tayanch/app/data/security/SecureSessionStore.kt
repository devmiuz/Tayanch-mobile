package uz.tayanch.app.data.security

import android.content.Context
import androidx.core.content.edit
import uz.tayanch.app.data.Interests

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

    /** The learning interests picked at onboarding; drive the roadmap, quiz & Arena. */
    fun saveInterests(ids: List<String>) {
        prefs.edit { putString(KEY_INTERESTS, KeystoreCrypto.encrypt(ids.joinToString(","))) }
    }

    /** Stored interests, or all four as a sensible default (returning/seed users). */
    fun interestIds(): List<String> {
        val stored = prefs.getString(KEY_INTERESTS, null)
            ?.let(KeystoreCrypto::decrypt)
            ?.split(",")
            ?.filter { it.isNotBlank() }
            .orEmpty()
        return stored.ifEmpty { Interests.defaultIds }
    }

    /** PII captured at sign-up — stored encrypted, shown on the Profile screen. */
    fun saveFullName(name: String) {
        if (name.isNotBlank()) prefs.edit { putString(KEY_FULL_NAME, KeystoreCrypto.encrypt(name)) }
    }

    fun fullName(): String? = prefs.getString(KEY_FULL_NAME, null)?.let(KeystoreCrypto::decrypt)

    fun saveExpectedSalary(salary: Int) {
        if (salary > 0) prefs.edit { putString(KEY_SALARY, KeystoreCrypto.encrypt(salary.toString())) }
    }

    fun expectedSalary(): Int? = prefs.getString(KEY_SALARY, null)?.let(KeystoreCrypto::decrypt)?.toIntOrNull()

    fun clear() = prefs.edit { clear() }

    private companion object {
        const val KEY_ACCESS = "access"
        const val KEY_REFRESH = "refresh"
        const val KEY_SESSION = "session"
        const val KEY_ONBOARDED = "onboarded"
        const val KEY_INTERESTS = "interests"
        const val KEY_FULL_NAME = "full_name"
        const val KEY_SALARY = "expected_salary"
    }
}
