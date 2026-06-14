# Tayanch — Secure, Gamified Career Accelerator (Android)

**Tayanch** ("a support / pillar" in Uzbek) is a career-training Android app for young
CS graduates: it turns a university major into a market-aligned learning roadmap, tests
knowledge in a cheat-resistant quiz engine, lets peers compete 1-to-1, and tracks
career velocity — all built to showcase a **defense-in-depth security architecture**
for a Cyber Security Management diploma.

This is the Android client. It runs **fully standalone against mock data**: the Ktor
HTTP client is wired to a `MockEngine` that serves JSON exactly as a real backend would.
Swapping in a real server later is a one-line engine change (see *Going live* below).

> [!WARNING]
> **This build ships with placeholder/dev secrets and runs on mock data by default.**
> The certificate pins, the NDK HMAC/XOR keys, the backend host, the Agora keys, and the
> release signing key are all **fake/demo values**. Do **not** publish or run against real
> users until you replace every item in the
> **"⚠️ Placeholder secrets — replace before production"** section near the end of this file.

---

## Tech stack

| Layer | Choice |
|------|--------|
| UI | Jetpack Compose (Material 3), single-activity, Navigation-Compose |
| State | MVVM + `StateFlow`/Compose state, unidirectional data flow |
| Networking | **Ktor client** (`MockEngine` now → `OkHttp` later) + `kotlinx.serialization` |
| Async | Kotlin Coroutines |
| Security | Keystore AES-GCM + StrongBox, biometric-bound keys, RSA-OAEP transit, NDK secrets, cert pinning, HMAC app-signature, FLAG_SECURE, R8 |
| Build | AGP 9.2.1 (built-in Kotlin), Kotlin 2.2.20, Gradle wrapper 9.4.1, JDK 17, compileSdk/targetSdk 37 (Android 17), minSdk 26 |

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

## Learning content — interests

At onboarding the candidate multi-selects from **four learning interests**, each with its
own 2-level roadmap and fully-authored Level-1 content (article + weblink + video +
flashcards + quiz, in Uzbek):

| Interest | Level-1 topics |
|----------|----------------|
| 🛡️ Kiberxavfsizlik | Authentication · Injection (SQLi/XSS) · Access control & IDOR |
| 🐍 Python dasturlash | Syntax & types · Control flow & functions · Data structures |
| 🎨 Grafik dizayn | Composition & hierarchy · Color theory · Typography |
| 🤖 Android dasturlash | Kotlin basics · App components · Compose & state |

The selected interest ids are stored ([SecureSessionStore]) and **drive the shared
screens**: Home shows one roadmap at a time (switcher chips when several are picked), and
the global quiz + Arena deck are assembled from the chosen interests' question pools.
Content lives per interest in `data/mock/{Cyber,Python,Design,Android}Content.kt`; paths
follow [roadmap.sh/android](https://roadmap.sh/android), the
[Scaler Python roadmap](https://www.scaler.com/blog/python-developer-roadmap-2026-6-month-step-by-step-guide/),
and the [Coursera graphic-design roadmap](https://www.coursera.org/resources/graphic-design-learning-roadmap).

## Project structure

```
data/
  Interests.kt single source of the 4 learning interests (id · name · emoji)
  dto/         kotlinx.serialization DTOs (the API contract)
  mock/        MockData (aggregator) · {Cyber,Python,Design,Android}Content · AnswerSpec
  network/     ApiRoutes (XOR-obfuscated) · NetworkModule (Ktor + MockEngine + grader)
  repository/  TayanchRepository — the one gateway to the network
  security/    KeystoreCrypto(+StrongBox) · BiometricKey · SecureSessionStore · SecureClock
               InputSanitizer · CryptoTransit(RSA) · AppSignature(HMAC) · NativeSecrets(NDK)
               BreachCheck(HIBP) · SessionManager · RootDetector(RootBeer, release-only)
cpp/           secrets.cpp + CMakeLists.txt — NDK secret store (libtayanch_secrets.so)
ui/
  auth · onboarding · home · career · profile · content · flashcard · quiz · battle
  components/  shared UiState/StateContent/Markdown renderer/CodeBlock/SecurityNote
  navigation/  Routes · MainScaffold · TayanchNavGraph
  security/    SecureScreenEffect (FLAG_SECURE + anti-tapjacking) · RootBlockedScreen
```

## Security pillars (all applied, in one place)

Every security mechanism the app ships, grouped by layer. Each screen also shows a small
🛡 note pointing to the pillar it exercises. The **Backend** column marks the pillars wired
**end-to-end** to the FastAPI server (`Tayanch/backend`); `—` means the defense is fully
client-side. Together these realise the diploma's ~25 security principles.

| # | Layer | Pillar | Android implementation | Backend counterpart |
|---|-------|--------|------------------------|---------------------|
| 1 | Storage & keys | Secure token storage | `SecureSessionStore` + `KeystoreCrypto` (AES-256-GCM) | — |
| 2 | Storage & keys | Hardware-backed crypto (TEE / StrongBox) | `KeystoreCrypto` (StrongBox, TEE fallback) | — |
| 3 | Storage & keys | Biometric-bound unlock | `BiometricKey` `CryptoObject` (Class-3) + `BiometricAuthenticator` | — |
| 4 | Storage & keys | Binary secret storage (NDK) | `cpp/secrets.cpp` + `NativeSecrets` (JNI) — secrets in `.so`, not the DEX | shares `app_hmac_secret` |
| 5 | Storage & keys | PII memory minimization | `CharArray` wipe in `AuthViewModel`/`CryptoTransit` + `AuthUiState` reset | — |
| 6 | Transport & network | Transport security (HTTPS-only) | `res/xml/network_security_config.xml` (no cleartext) | TLS + HSTS headers |
| 7 | Transport & network | Certificate pinning | OkHttp `CertificatePinner` + `<pin-set>` | TLS leaf / intermediate |
| 8 | Transport & network | App-layer encryption in transit (RSA-OAEP) | `CryptoTransit` seals password; `enc` flag | `GET /auth/public-key` + `crypto_transit.decrypt_password` |
| 9 | Transport & network | HMAC app-signature ("not Postman") | `AppSignature` + OkHttp interceptor (`X-App-Timestamp/Nonce/Signature`) | `AppSignatureMiddleware` |
| 10 | Transport & network | Endpoint obfuscation | `ApiRoutes` — XOR byte arrays, key from the NDK | — |
| 11 | Auth & session | Dual-token + refresh rotation | ktor `Auth` `bearer { refreshTokens }` + `SecureSessionStore.updateTokens` | `POST /auth/refresh` (jti rotation) |
| 12 | Auth & session | Single-session lock | `SessionManager` evicts → `MainActivity` bounces to gateway | `active_sid` → 401 |
| 13 | Auth & session | IDOR-safe identity | `/users/me`, identity from token (`ProfileViewModel`) | token-derived identity |
| 14 | Auth & session | Password entropy meter | `PasswordStrength` (zxcvbn-style) | `password_strength` (authoritative) |
| 15 | Auth & session | Breached-credential gate (HIBP k-anonymity) | `BreachCheck` (client preview) | `breach.breach_count` (authoritative) |
| 16 | Integrity & anti-cheat | Zero-Trust grading | client submits the choice only | `NetworkModule.gradeJson` / backend grader + answer key |
| 17 | Integrity & anti-cheat | Server-side state authority | XP total owned by the server, not the client | `serverTotalXp` / backend |
| 18 | Integrity & anti-cheat | Anti time-tampering | `SecureClock.elapsedRealtime` (quiz/battle timers) | server UTC re-check |
| 19 | Integrity & anti-cheat | Lifecycle anti-cheat | `QuizViewModel`/`BattleViewModel.onBackgrounded()` void on `ON_PAUSE` | — |
| 20 | Integrity & anti-cheat | Offline resilience | `OfflineAnswerQueue` (WorkManager-style retry) | — |
| 21 | Integrity & anti-cheat | Proof-of-Presence / anti-farming | `ContentScreen` focus engine · `FlashcardViewModel` velocity check | — |
| 22 | App & UI hardening | Screenshot / record block (FLAG_SECURE) | `SecureScreenEffect` (Auth, Quiz, Battle, Video, Interview) | — |
| 23 | App & UI hardening | Anti-tapjacking | `filterTouchesWhenObscured` in `SecureScreenEffect` | — |
| 24 | App & UI hardening | WebView sandboxing | `HardenedWebView` (allowlist, no file access, Safe Browsing) | — |
| 25 | App & UI hardening | Stored-XSS-inert content | native `MarkdownText` (no WebView/DOM) | — |
| 26 | App & UI hardening | Input sanitization | `InputSanitizer` (length cap + strip script/SQL vectors) | `sanitizer.sanitize_text` |
| 27 | App & UI hardening | Link defanging (anti-SSRF) | `CareerScreen.defang` | portal-side preview block |
| 28 | App & UI hardening | R8 compiler hardening | `app/build.gradle.kts` (release) + `proguard-rules.pro` | — |
| 29 | App & UI hardening | Root / tamper detection (**release builds only**) | `RootDetector` (RootBeer) → `MainActivity` shows a terminal `RootBlockedScreen` | — |

> **Pillar 29 note:** root detection runs **only in release builds** — `RootDetector`
> returns `false` when `BuildConfig.DEBUG` is true, so emulators and rooted test devices
> don't trip it during development. Local root checks are a deterrent, not a guarantee
> (Magisk Hide/Zygisk can defeat them); pair with server-verified Play Integrity for a
> hard gate.

## Going live (swap the mock for the real backend)

The real OkHttp engine is already built (`NetworkModule.realClient`) with pinning,
the HMAC app-signature, RSA-sealed credentials, and refresh rotation. To use it:

1. Run the FastAPI backend (`python run.py` in `Tayanch/backend`).
2. Flip `USE_REAL_BACKEND` to `true` in `app/build.gradle.kts` (debug points at
   `http://10.0.2.2:8000`; release at the pinned `https://api.tayanch.uz`).
3. For release, replace the placeholder `CERT_PIN_*` / `<pin-set>` digests with the
   real server SPKI pins, and set the backend `TAYANCH_REQUIRE_APP_SIGNATURE=true`.

No DTO, repository, view-model, or UI code changes — only the engine flag.

> Mock-data tuning: focus/quiz/battle timers are scaled down for demoing
> (`DEMO_MS_PER_MINUTE` in `ContentScreen`, `duration_sec` in `MockData`).

## ⚠️ Placeholder secrets — replace before production

Every value below is a **fake/dev placeholder** committed for the demo. Shipping any of
them is a security hole (or will simply break the app). Replace them all before a real
release. Items are ordered by severity.

| # | What | Where (current fake value) | Replace with |
|---|------|----------------------------|--------------|
| 1 | **TLS certificate pins** | `app/build.gradle.kts` → `CERT_PIN_PRIMARY` / `CERT_PIN_BACKUP` (`sha256/AAAA…`, `sha256/BBBB…`) **and** `res/xml/network_security_config.xml` `<pin-set>` (same `AAAA…`/`BBBB…`) | The real **SPKI SHA-256** of your server's leaf cert + one backup (intermediate or next-rotation key). Keep both files in sync. Wrong pins block **all** HTTPS. Get them with `openssl s_client -connect host:443 \| openssl x509 -pubkey -noout \| openssl pkey -pubin -outform der \| openssl dgst -sha256 -binary \| base64` |
| 2 | **HMAC app-signature secret** (Pillar 8/13) | `app/src/main/cpp/secrets.cpp` → `SIG[]` table currently masks `shared-secret-baked-into-the-apk-ndk` — the **same dev value as the backend default** | A random ≥32-byte secret. Regenerate the masked `SIG[]` table (see *Regenerating the native tables*) **and** set the backend's `TAYANCH_APP_HMAC_SECRET` to the exact same string. |
| 3 | **Release signing key** | `app/build.gradle.kts` has **no `signingConfig`** → release is signed with the **debug key** | A real upload/release keystore + `signingConfig`. Store the `.jks` and passwords outside VCS (e.g. `keystore.properties`, gitignored). |
| 4 | **Backend host / base URL** | `app/build.gradle.kts` → `PIN_HOST` & release `API_BASE_URL` = `api.tayanch.uz`; debug `API_BASE_URL` = `http://10.0.2.2:8000`; `network_security_config.xml` domain `api.tayanch.uz` | Your real API host. `PIN_HOST` **must** equal the host whose cert you pinned in #1. |
| 5 | **Endpoint XOR key** (Pillar 12) | `app/src/main/cpp/secrets.cpp` → `XOR[]` table masks `Tayanch`; fallback literal `"Tayanch"` in `ApiRoutes.kt` | A random key. Regenerate the `XOR[]` table **and** re-encode every route byte array in `ApiRoutes.kt` with the new key (see below). Replace/remove the `"Tayanch"` JVM fallback. |
| 6 | **Agora video (mock-interview room)** | Client `InterviewScreen` is **layout-only — no Agora SDK, no keys**; backend `TAYANCH_AGORA_APP_ID` / `TAYANCH_AGORA_APP_CERTIFICATE` are blank → **dev fallback tokens** | Add the Agora SDK + your real **App ID** to the client and set the two backend Agora env vars so it issues real RTC tokens. |
| 7 | **`USE_REAL_BACKEND` flag** | `app/build.gradle.kts` = `false` → the app runs on the **MockEngine** (mock tokens `mock.access.*`, public key `"MOCK-NO-RSA"`, all of `MockData.kt`) | Set `true` for production builds. The mock artifacts are then bypassed entirely. |
| 8 | **Demo / seed credentials** | Mock auth accepts **any** `+998` number; backend seeds `+998901234567 / Tayanch2026!` and `+998900000000 / Interview2026!` | Remove the seed accounts and rely on real registration; never ship demo passwords. |

**Backend secrets the app depends on** (in `Tayanch/backend/.env`): `TAYANCH_JWT_SECRET`
(default `change-me-…`) and `TAYANCH_APP_HMAC_SECRET` (must equal the NDK secret in #2).
The RSA transit keypair (Pillar 7) is auto-generated to `backend/rsa_private_key.pem`
(gitignored) — fine to leave, but back it up so the `key_id` stays stable.

### Regenerating the native tables (`secrets.cpp`)

The secrets aren't plain strings — each is XOR-masked with `mask = (0x5A + index) & 0xFF`.
To change a value, regenerate its byte table:

```python
def mask(s):  # prints the C array body for secrets.cpp
    b = s.encode()
    print(", ".join(f"0x{(b[i] ^ ((0x5A+i) & 0xFF)):02x}" for i in range(len(b))))
mask("<your-new-hmac-secret>")   # → paste into SIG[]
mask("<your-new-xor-key>")        # → paste into XOR[]
```

If you change the **XOR key** (#5) you must also re-encode every endpoint in
`ApiRoutes.kt`, because each route's `byteArrayOf(...)` is `path ^ key`:

```python
key = b"<your-new-xor-key>"
def enc(p):  # prints the byte array for one route
    b = p.encode(); print([b[i] ^ key[i % len(key)] for i in range(len(b))])
enc("api/v1/auth/login")  # repeat for every route (BASE, LOGIN, REGISTER, …)
```
