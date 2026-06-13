package uz.tayanch.app.data.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Pillar 8/17 — Hardware-Backed Cryptography (TEE / StrongBox).
 *
 * The AES key is generated inside the Android Keystore and never leaves it: the
 * raw key bytes are not accessible to the app's process, so a rooted device that
 * dumps the heap finds only ciphertext. On devices with a dedicated secure
 * element we additionally request StrongBox (a separate tamper-resistant chip);
 * if the hardware is absent the key is still TEE-backed. We encrypt with
 * AES-256-GCM (authenticated encryption) and prefix the 12-byte IV to the
 * ciphertext.
 */
object KeystoreCrypto {

    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "tayanch_session_key"
    private const val TRANSFORM = "AES/GCM/NoPadding"
    private const val IV_LEN = 12
    private const val TAG_BITS = 128

    private fun secretKey(): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (ks.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        // Try StrongBox first (API 28+); fall back to the TEE if unavailable.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            runCatching { return generate(strongBox = true) }
                .onFailure { if (it !is StrongBoxUnavailableException) throw it }
        }
        return generate(strongBox = false)
    }

    private fun generate(strongBox: Boolean): SecretKey {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .apply {
                if (strongBox && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setIsStrongBoxBacked(true)
                }
            }
            .build()
        generator.init(spec)
        return generator.generateKey()
    }

    fun encrypt(plain: String): String {
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = cipher.iv
        val body = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + body, Base64.NO_WRAP)
    }

    fun decrypt(encoded: String): String {
        val all = Base64.decode(encoded, Base64.NO_WRAP)
        val iv = all.copyOfRange(0, IV_LEN)
        val body = all.copyOfRange(IV_LEN, all.size)
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_BITS, iv))
        return String(cipher.doFinal(body), Charsets.UTF_8)
    }
}
