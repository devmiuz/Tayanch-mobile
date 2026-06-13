package uz.tayanch.app.data.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Pillar 8/17 — a biometric-bound Keystore key.
 *
 * Unlike [KeystoreCrypto] (used freely by the app), this AES key is created with
 * `setUserAuthenticationRequired(true)`, so the TEE refuses to run a cipher with
 * it until a *fresh* biometric auth has unlocked it. We hand the un-initialised-
 * for-use Cipher to BiometricPrompt inside a CryptoObject; only after the OS
 * verifies the fingerprint/face does the returned Cipher become usable. That
 * makes the unlock cryptographically bound to the hardware, not a screen the app
 * could simply skip.
 *
 * If the user adds/removes a fingerprint the key is permanently invalidated
 * (KeyPermanentlyInvalidatedException) — we drop it and fall back to credentials.
 */
object BiometricKey {

    private const val KEYSTORE = "AndroidKeyStore"
    private const val ALIAS = "tayanch_biometric_key"
    private const val TRANSFORM = "AES/GCM/NoPadding"
    private val CANARY = "tayanch-biometric-canary".toByteArray()

    private fun keystore(): KeyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }

    private fun getOrCreateKey(): SecretKey {
        val ks = keystore()
        (ks.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        val builder = KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
        }
        gen.init(builder.build())
        return gen.generateKey()
    }

    private fun drop() = runCatching { keystore().deleteEntry(ALIAS) }

    /** A Cipher to wrap in BiometricPrompt.CryptoObject. null ⇒ no crypto-bound path. */
    fun unlockCipher(): Cipher? = runCatching {
        Cipher.getInstance(TRANSFORM).apply { init(Cipher.ENCRYPT_MODE, getOrCreateKey()) }
    }.getOrElse {
        if (it is KeyPermanentlyInvalidatedException) drop()
        null
    }

    /**
     * Proves the cipher really was unlocked by the OS: a TEE op that only succeeds
     * post-auth. Throws if the key wasn't authorised — the caller treats that as a
     * failed unlock.
     */
    fun assertUnlocked(cipher: Cipher) {
        cipher.doFinal(CANARY)
    }
}
