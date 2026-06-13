// Pillar 8/18 — Binary-level secret storage (NDK + JNI + XOR).
//
// The app's static secrets live here, in native code, never as Java/Kotlin
// string constants. Each is additionally stored XOR-masked so the literal does
// not even appear in the .so's .rodata: a `strings libtayanch_secrets.so` finds
// noise, and the value only exists in process memory for the instant it is
// handed to the JVM. This is what powers the HMAC app-signature gate (the secret
// the backend shares) and the endpoint XOR key (Pillar 12).
#include <jni.h>
#include <string>

namespace {

// XOR-masked secret tables (mask = (0x5A + index) & 0xFF). See secrets generator.
// SIG  -> the HMAC app-signature key shared with the Ktor/FastAPI backend.
const unsigned char SIG[] = {0x29, 0x33, 0x3d, 0x2f, 0x3b, 0x3b, 0x4d, 0x12, 0x07, 0x00,
                             0x16, 0x00, 0x12, 0x4a, 0x0a, 0x08, 0x01, 0x0e, 0x08, 0x40,
                             0x07, 0x01, 0x04, 0x1e, 0x5f, 0x07, 0x1c, 0x10, 0x5b, 0x16,
                             0x08, 0x12, 0x57, 0x15, 0x18, 0x16};
// XOR  -> the endpoint-obfuscation key consumed by ApiRoutes.kt.
const unsigned char XOR[] = {0x0e, 0x3a, 0x25, 0x3c, 0x30, 0x3c, 0x08};

std::string unmask(const unsigned char *data, size_t len) {
    std::string out;
    out.reserve(len);
    for (size_t i = 0; i < len; ++i) {
        out.push_back(static_cast<char>(data[i] ^ ((0x5A + i) & 0xFF)));
    }
    return out;
}

} // namespace

extern "C" JNIEXPORT jstring JNICALL
Java_uz_tayanch_app_data_security_NativeSecrets_appSignatureKey(JNIEnv *env, jobject /*thiz*/) {
    std::string s = unmask(SIG, sizeof(SIG));
    jstring result = env->NewStringUTF(s.c_str());
    // Best-effort wipe of the transient buffer.
    volatile char *p = const_cast<volatile char *>(s.data());
    for (size_t i = 0; i < s.size(); ++i) p[i] = 0;
    return result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_uz_tayanch_app_data_security_NativeSecrets_endpointXorKey(JNIEnv *env, jobject /*thiz*/) {
    std::string s = unmask(XOR, sizeof(XOR));
    jstring result = env->NewStringUTF(s.c_str());
    volatile char *p = const_cast<volatile char *>(s.data());
    for (size_t i = 0; i < s.size(); ++i) p[i] = 0;
    return result;
}
