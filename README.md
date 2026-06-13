# Tayanch — Secure, Gamified Career Accelerator (Android)

**Tayanch** ("a support / pillar" in Uzbek) is a career-training Android app for young
CS graduates: it turns a university major into a market-aligned learning roadmap, tests
knowledge in a cheat-resistant quiz engine, lets peers compete 1-to-1, and tracks
career velocity — all built to showcase a **defense-in-depth security architecture**
for a Cyber Security Management diploma.

This is the Android client. It runs **fully standalone against mock data**: the Ktor
HTTP client is wired to a `MockEngine` that serves JSON exactly as a real backend would.
Swapping in a real server later is a one-line engine change (see *Going live* below).

---

## Tech stack

| Layer | Choice |
|------|--------|
| UI | Jetpack Compose (Material 3), single-activity, Navigation-Compose |
| State | MVVM + `StateFlow`/Compose state, unidirectional data flow |
| Networking | **Ktor client** (`MockEngine` now → `OkHttp` later) + `kotlinx.serialization` |
| Async | Kotlin Coroutines |
| Security | Android Keystore (AES-GCM), BiometricPrompt, FLAG_SECURE, R8 |
| Build | AGP 8.13, Kotlin 2.2.20, Gradle 8.14.3, compileSdk 36, minSdk 26 |

No Hilt/Room/Retrofit — a tiny manual `ServiceLocator` keeps the dependency graph
small and the security primitives explicit.

## Build & run

```bash
./gradlew assembleDebug          # build the APK
./gradlew installDebug           # install on a connected device/emulator
# or open the folder in Android Studio and press Run
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.
Release builds (`assembleRelease`) exercise R8 shrinking + obfuscation.

**First run:** you land on the auth gateway. Register (the password strength meter must
reach *Good*) or log in with any `+998` phone (≥12 digits) and any password — the mock
backend accepts it and returns tokens.

## App flow

```
Launch ─▶ [JWT?] ─no─▶ F1 Auth ─▶ F2 Onboarding ─▶ Main Hub
            └─yes, onboarded──────────────────────▶ Main Hub

Main Hub (bottom bar): Home · Career · Profile        + Global-Quiz FAB
   Home ─▶ Article/Video (Focus Trainer) · Flashcards · Quiz   (immersive, no bar)
   Career ─▶ 1v1 Battle                                        (immersive, no bar)
```

- **Home** – the learning roadmap: levels → topics → a row of 5 nodes
  (Article · Web · Video · Flashcards · Quiz), each level ending in a "boss" capstone
  PDF you share to your PC.
- **Career** – XP-gated 1v1 Arena, mock-interview requests, and capstone submission
  with status tracking.
- **Profile** – AI motivation, career-velocity projection, focus stats, badges, and a
  filterable leaderboard.

## Project structure

```
data/
  dto/         kotlinx.serialization DTOs (the API contract)
  mock/        MockData — every JSON response + the server-side answer key
  network/     ApiRoutes (XOR-obfuscated) · NetworkModule (Ktor + MockEngine + grader)
  repository/  TayanchRepository — the one gateway to the network
  security/    KeystoreCrypto · SecureSessionStore · SecureClock · InputSanitizer
ui/
  auth · onboarding · home · career · profile · content · flashcard · quiz · battle
  components/  shared UiState/StateContent/Markdown renderer/CodeBlock/SecurityNote
  navigation/  Routes · MainScaffold · TayanchNavGraph
  security/    SecureScreenEffect (FLAG_SECURE + anti-tapjacking)
```

## Security pillars → where they live in the code

Each screen shows a small 🛡 note describing the pillar it exercises.

| # | Pillar | Implementation |
|---|--------|----------------|
| 1 | Secure token storage | `KeystoreCrypto` (AES-GCM) + `SecureSessionStore` |
| 2 | Screenshot/record block (FLAG_SECURE) | `SecureScreenEffect` (Auth, Quiz, Battle, Video) |
| 3 | Zero-Trust grading (answers never sent to client) | `NetworkModule.gradeJson` + `MockData.answerKey` |
| 4 | Anti time-tampering | `SecureClock.elapsedRealtime`, quiz/battle timers |
| 5 | Offline resilience | `OfflineAnswerQueue` (WorkManager-style retry) |
| 6 | Stored-XSS-inert content | native `MarkdownText` (no WebView/DOM) |
| 7 | R8 hardening | `app/build.gradle.kts` (release) + `proguard-rules.pro` |
| 8 | Hardware-backed crypto (TEE) | `KeystoreCrypto` key generated in AndroidKeyStore |
| 9 | Server-side state authority | XP total owned by `NetworkModule`, not the client |
| 10 | IDOR-safe identity | `/users/me`, identity from token (see `ProfileViewModel`) |
| 11 | Transport security | `res/xml/network_security_config.xml` (no cleartext) |
| 12 | Endpoint obfuscation | `ApiRoutes` — XOR byte arrays, decoded at call site |
| 19 | WebView sandboxing | `HardenedWebView` (allowlist, no file access, Safe Browsing) |
| 20 | Anti-tapjacking | `filterTouchesWhenObscured` in `SecureScreenEffect` |
| 21 | Modern password entropy | `PasswordStrength` (zxcvbn-style meter) |
| 22 | Proof-of-Presence / anti-farming | `ContentScreen` focus engine · `FlashcardViewModel` velocity check |
| 14 | Link defanging (anti-SSRF) | `CareerScreen.defang` |

Biometric unlock for returning users uses the real `BiometricPrompt`
(`BiometricAuthenticator`).

## Going live (swap the mock for a real backend)

The whole stack above the network is production-shaped. To point at a real server:

1. In `app/build.gradle.kts`, replace `ktor-client-mock` with `ktor-client-okhttp`.
2. In `NetworkModule`, change `HttpClient(MockEngine) { … engine { addHandler … } }`
   to `HttpClient(OkHttp)` and delete the `engine { }` block.
3. Add the `Authorization` / `X-App-Signature` headers in `defaultRequest`.

No DTO, repository, view-model, or UI code changes.

> Mock-data tuning: focus/quiz/battle timers are scaled down for demoing
> (`DEMO_MS_PER_MINUTE` in `ContentScreen`, `duration_sec` in `MockData`).
