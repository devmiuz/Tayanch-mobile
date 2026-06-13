package uz.tayanch.app.data.security

import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Pillar 13 — HMAC request signing ("only our app, not Postman/curl").
 *
 * Every request to the real backend carries three headers the FastAPI
 * AppSignatureMiddleware verifies. The shared secret is NOT a Kotlin constant —
 * it is fetched from the NDK ([NativeSecrets], Pillar 8). The signed string and
 * the secret match the server's `expected_signature` exactly:
 *
 *   X-App-Timestamp : unix seconds (server replay window ±300s)
 *   X-App-Nonce     : 128-bit random hex, single-use within the window
 *   X-App-Signature : hex( HMAC-SHA256(secret, "METHOD\npath\nts\nnonce") )
 */
object AppSignature {

    const val HEADER_TS = "X-App-Timestamp"
    const val HEADER_NONCE = "X-App-Nonce"
    const val HEADER_SIG = "X-App-Signature"

    private val rng = SecureRandom()

    data class Headers(val timestamp: String, val nonce: String, val signature: String)

    fun sign(method: String, path: String): Headers {
        val ts = (System.currentTimeMillis() / 1000).toString()
        val nonce = randomHex(16)
        val message = "$method\n$path\n$ts\n$nonce"
        return Headers(ts, nonce, hmacHex(message))
    }

    private fun hmacHex(message: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(NativeSecrets.signatureKey.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(message.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    private fun randomHex(bytes: Int): String {
        val b = ByteArray(bytes).also(rng::nextBytes)
        return b.joinToString("") { "%02x".format(it) }
    }
}
