import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// AGP 9: configure the typed ApplicationExtension directly. The generated
// `android { }` accessor resolves to the deprecated BaseAppModuleExtension
// overload (removed in AGP 10); this uses the new public DSL type instead.
configure<ApplicationExtension> {
    namespace = "uz.tayanch.app"
    compileSdk = 37
    // Pin to an installed NDK (AGP 9's default differs); this one is verified to
    // emit 16 KB-aligned .so files for libtayanch_secrets.
    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "uz.tayanch.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        // Pillar 8/18 — compile the native secret store (libtayanch_secrets.so).
        // 64-bit ABIs only: 16 KB page size is a 64-bit-only concern, and dropping
        // the legacy 32-bit ABIs avoids shipping RootBeer's 4 KB-aligned 32-bit
        // libtoolChecker.so. arm64-v8a = real devices, x86_64 = emulators.
        ndk { abiFilters += listOf("arm64-v8a", "x86_64") }

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

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
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
    implementation(libs.rootbeer)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.mock)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
