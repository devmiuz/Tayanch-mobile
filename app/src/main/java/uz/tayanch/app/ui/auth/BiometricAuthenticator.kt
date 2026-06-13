package uz.tayanch.app.ui.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import uz.tayanch.app.R
import uz.tayanch.app.data.security.BiometricKey

/**
 * Real Android BiometricPrompt wrapper (fingerprint / face). A returning user
 * whose encrypted session already exists can re-enter the app behind the device
 * biometric gate instead of re-typing credentials.
 *
 * Pillar 8/17 — when a Class-3 (STRONG) biometric is present we bind the prompt
 * to a [BiometricKey] CryptoObject, so success is proven by a TEE crypto op that
 * only runs after the OS verifies the user. On devices with only a WEAK biometric
 * we fall back to a plain (UI-level) prompt.
 */
object BiometricAuthenticator {

    fun isAvailable(context: Context): Boolean =
        BiometricManager.from(context).canAuthenticate(BIOMETRIC_WEAK) ==
            BiometricManager.BIOMETRIC_SUCCESS

    private fun hasStrong(context: Context): Boolean =
        BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS

    fun prompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        // Prefer the crypto-bound path; null cipher ⇒ fall back to a WEAK prompt.
        val cipher = if (hasStrong(activity)) BiometricKey.unlockCipher() else null

        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val unlocked = result.cryptoObject?.cipher
                    if (cipher != null && unlocked != null) {
                        // Prove the key was really released by the OS (Pillar 8/17).
                        runCatching { BiometricKey.assertUnlocked(unlocked) }
                            .fold(
                                onSuccess = { onSuccess() },
                                onFailure = { onError(activity.getString(R.string.error_auth_failed)) },
                            )
                    } else {
                        onSuccess()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString())
                }
            },
        )

        val allowed = if (cipher != null) BIOMETRIC_STRONG else BIOMETRIC_WEAK
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_title))
            .setSubtitle(activity.getString(R.string.biometric_subtitle))
            .setNegativeButtonText(activity.getString(R.string.biometric_negative))
            .setAllowedAuthenticators(allowed)
            .build()

        if (cipher != null) {
            prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
        } else {
            prompt.authenticate(info)
        }
    }
}
