package uz.tayanch.app.data.security

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

/**
 * Pillar 7 — Application-layer encryption in transit.
 *
 * Seals the password with the server's RSA public key (fetched once from
 * /auth/public-key and cached by key_id) before it is sent. The parameters mirror
 * the FastAPI backend's `crypto_transit.decrypt_password` *exactly*:
 *
 *   RSA / OAEP, main digest SHA-256, MGF1 SHA-256, no label  →  Base64(ciphertext)
 *
 * The plaintext is taken as a [CharArray] and the intermediate UTF-8 bytes are
 * zeroed right after encryption (Pillar 3 — PII memory minimization).
 */
object CryptoTransit {

    const val ALG = "rsa-oaep-sha256"

    private val oaep = OAEPParameterSpec(
        "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT,
    )

    @Volatile private var cachedKeyId: String? = null
    @Volatile private var cachedKey: PublicKey? = null

    private fun publicKey(pem: String, keyId: String): PublicKey {
        cachedKey?.let { if (cachedKeyId == keyId) return it }
        val body = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        val der = Base64.decode(body, Base64.DEFAULT)
        val key = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(der))
        cachedKey = key
        cachedKeyId = keyId
        return key
    }

    /** Returns Base64(RSA-OAEP-SHA256(password)). The plaintext bytes are wiped. */
    fun seal(password: CharArray, pem: String, keyId: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey(pem, keyId), oaep)
        val plain = toUtf8(password)
        try {
            return Base64.encodeToString(cipher.doFinal(plain), Base64.NO_WRAP)
        } finally {
            plain.fill(0)
        }
    }

    private fun toUtf8(chars: CharArray): ByteArray {
        val bb: ByteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars))
        val out = ByteArray(bb.remaining())
        bb.get(out)
        if (bb.hasArray()) bb.array().fill(0)
        return out
    }
}
