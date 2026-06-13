plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "uz.tayanch.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "uz.tayanch.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        vectorDrawables { useSupportLibrary = true }

        // Pillar 8/18 — compile the native secret store (libtayanch_secrets.so).
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86") }

        // Networking target. USE_REAL_BACKEND=false keeps the offline MockEngine
        // demo as the default; flip it (or the debug field below) to hit FastAPI.
        buildConfigField("boolean", "USE_REAL_BACKEND", "false")
        // SPKI pin for the production host (Pillar 18 — certificate pinning).
        // Replace with the real leaf/intermediate pin before shipping; the build
        // pin + a backup are both honoured by OkHttp's CertificatePinner.
        buildConfigField("String", "PIN_HOST", "\"api.tayanch.uz\"")
        buildConfigField(
            "String", "CERT_PIN_PRIMARY",
            "\"sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\"",
        )
        buildConfigField(
            "String", "CERT_PIN_BACKUP",
            "\"sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=\"",
        )
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildTypes {
        release {
            // Pillar: R8 compiler hardening (code shrinking + obfuscation).
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            // Production hits the real, certificate-pinned backend over HTTPS.
            buildConfigField("String", "API_BASE_URL", "\"https://api.tayanch.uz/\"")
        }
        debug {
            isMinifyEnabled = false
            // Emulator loopback to a locally running FastAPI server (10.0.2.2 = host).
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.biometric)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    // Networking — Ktor client wired against a MockEngine so the whole
    // Repository/Network stack is identical to talking to a real server.
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.mock)
    implementation(libs.ktor.client.okhttp)   // real engine + OkHttp CertificatePinner
    implementation(libs.ktor.client.auth)      // bearer + refresh-token rotation
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // Dependency injection — Koin (BOM-aligned).
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
