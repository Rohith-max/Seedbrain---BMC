import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

// ── Load secrets from secrets.properties (never committed) ───────────────────
val secretsFile = rootProject.file("secrets.properties")
val secrets = Properties().apply {
    if (secretsFile.exists()) load(secretsFile.inputStream())
}

android {
    namespace = "com.nidhi.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nidhi.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ── Groq API (OpenAI-compatible) ──────────────────────────────────────
        val groqKey  = secrets.getProperty("GROQ_API_KEY",  "SIMULATED")
        val groqUrl  = secrets.getProperty("GROQ_BASE_URL", "https://api.groq.com/openai/v1/")
        val useReal  = groqKey != "SIMULATED" && groqKey.isNotBlank()

        buildConfigField("String",  "LLM_API_KEY",       "\"$groqKey\"")
        buildConfigField("String",  "LLM_BASE_URL",      "\"$groqUrl\"")
        buildConfigField("Boolean", "USE_SIMULATED_LLM", "${!useReal}")
        buildConfigField("String",  "LLM_MODEL",         "\"llama3-8b-8192\"")

        // ── Supabase ──────────────────────────────────────────────────────────
        val supaUrl  = secrets.getProperty("SUPABASE_URL",      "")
        val supaAnon = secrets.getProperty("SUPABASE_ANON_KEY", "")
        buildConfigField("String", "SUPABASE_URL",      "\"$supaUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supaAnon\"")

        // ── Clerk (publishable key — safe to embed, web-style auth reference) ─
        buildConfigField("String", "CLERK_PUBLISHABLE_KEY", "\"pk_test_cmFyZS16ZWJyYS0zNC5jbGVyay5hY2NvdW50cy5kZXYk\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility        = JavaVersion.VERSION_11
        targetCompatibility        = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental",    "true")
}

dependencies {
    // ── Core ──────────────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

    // ── Compose ───────────────────────────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)

    // ── Navigation ────────────────────────────────────────────────────────────
    implementation(libs.androidx.navigation.compose)

    // ── Koin DI ───────────────────────────────────────────────────────────────
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.androidx.workmanager)

    // ── Room ──────────────────────────────────────────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ── DataStore ─────────────────────────────────────────────────────────────
    implementation(libs.androidx.datastore.preferences)

    // ── WorkManager ───────────────────────────────────────────────────────────
    implementation(libs.androidx.work.runtime.ktx)

    // ── Retrofit + OkHttp + Moshi ─────────────────────────────────────────────
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // ── Coil ──────────────────────────────────────────────────────────────────
    implementation(libs.coil.compose)

    // ── ML Kit text recognition ───────────────────────────────────────────────
    implementation(libs.mlkit.text.recognition)

    // ── CameraX ───────────────────────────────────────────────────────────────
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ── Accompanist ───────────────────────────────────────────────────────────
    implementation(libs.accompanist.permissions)

    // ── Lottie ────────────────────────────────────────────────────────────────
    implementation(libs.lottie.compose)

    // ── Biometric ─────────────────────────────────────────────────────────────
    implementation(libs.androidx.biometric)

    // ── Firebase ──────────────────────────────────────────────────────────────
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)

    // ── Google Services ───────────────────────────────────────────────────────
    implementation(libs.google.play.services.auth)
    implementation(libs.play.services.location)

    // ── Coroutines ────────────────────────────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // ── Serialization ─────────────────────────────────────────────────────────
    implementation(libs.kotlinx.serialization.json)

    // ── Supabase ──────────────────────────────────────────────────────────────
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.storage)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.android)

    // ── Testing ───────────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
