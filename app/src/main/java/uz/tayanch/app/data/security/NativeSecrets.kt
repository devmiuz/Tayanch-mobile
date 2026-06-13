package uz.tayanch.app.data.security

/**
 * Pillar 8/18 — Binary-level secret storage (NDK + JNI).
 *
 * The HMAC app-signature key and the endpoint XOR key are held in
 * `libtayanch_secrets.so` (see `src/main/cpp/secrets.cpp`), XOR-masked, and only
 * cross into the JVM through these JNI calls. Decompiling the APK's `classes.dex`
 * reveals neither value — they are not Kotlin string constants. We cache the
 * unmasked values once; the native side wipes its transient buffers immediately.
 */
object NativeSecrets {

    init {
        System.loadLibrary("tayanch_secrets")
    }

    /** Shared HMAC-SHA256 secret for the X-App-Signature gate (matches the backend). */
    external fun appSignatureKey(): String

    /** XOR key used by [ApiRoutes] to reconstruct endpoint paths in memory. */
    external fun endpointXorKey(): String

    val signatureKey: String by lazy { appSignatureKey() }
    val xorKey: String by lazy { endpointXorKey() }
}
