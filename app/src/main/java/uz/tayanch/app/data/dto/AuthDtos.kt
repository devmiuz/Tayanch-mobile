package uz.tayanch.app.data.dto

import kotlinx.serialization.Serializable

/**
 * Auth payloads. The password is RSA-OAEP sealed with the server's public key
 * before transit (Pillar 7) on top of TLS 1.3 (Pillar 11/20): the `enc` flag
 * tells the backend the field is ciphertext ("rsa-oaep-sha256") vs. plaintext
 * (null, used by the offline MockEngine path).
 */
@Serializable
data class RegisterRequest(
    val phone_number: String,
    val full_name: String,
    val age: Int,
    val password: String,
    val enc: String? = null,
)

@Serializable
data class LoginRequest(
    val phone_number: String,
    val password: String,
    val enc: String? = null,
)

/** Sent to /auth/refresh to rotate the token pair (Pillar 16). */
@Serializable
data class RefreshRequest(
    val refresh_token: String,
)

/** Dual-token response (Pillar 16): short-lived access + rotating refresh. */
@Serializable
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val session_id: String,
    val onboarded: Boolean,
)

/** The RSA public key the client seals the password with (Pillar 7). */
@Serializable
data class PublicKeyResponse(
    val public_key: String,
    val key_id: String,
    val alg: String = "rsa-oaep-sha256",
)
