package com.nidhi.app.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.nidhi.app.BuildConfig
import com.nidhi.app.data.local.AppDatabase
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.data.remote.LlmApiService
import com.nidhi.app.data.remote.SimulatedLlmEngine
import com.nidhi.app.data.repository.*
import com.nidhi.app.domain.repository.*
import com.nidhi.app.feature.auth.BiometricHelper
import com.nidhi.app.feature.auth.SessionBiometricCache
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    // ── Database ──────────────────────────────────────────────────────────────
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "nidhi_db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()   // safety net for dev builds
            .build()
    }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().documentDao() }
    single { get<AppDatabase>().benefitDao() }
    single { get<AppDatabase>().alertDao() }
    single { get<AppDatabase>().conversationDao() }
    single { get<AppDatabase>().tombstoneDao() }

    // ── Moshi ─────────────────────────────────────────────────────────────────
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // ── OkHttp — standard client ──────────────────────────────────────────────
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ── OkHttp — streaming client (no response buffering, 30s read timeout) ──
    single(named("streaming")) {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.HEADERS   // don't buffer SSE body in logs
            else
                HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)   // 30s read timeout for streaming (Req 8.6)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.LLM_BASE_URL)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    single { get<Retrofit>().create(LlmApiService::class.java) }

    // ── Firebase ──────────────────────────────────────────────────────────────
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseMessaging.getInstance() }

    // ── User Preferences (DataStore) ─────────────────────────────────────────
    single { UserPreferences(androidContext()) }

    // ── Simulated LLM engine ──────────────────────────────────────────────────
    singleOf(::SimulatedLlmEngine)

    // ── Biometric helpers ─────────────────────────────────────────────────────
    single { BiometricHelper(androidContext()) }
    single { SessionBiometricCache() }

    // ── Repositories ──────────────────────────────────────────────────────────
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class

    // DocumentRepositoryImpl now takes TombstoneDao — updated constructor
    single<DocumentRepository> {
        DocumentRepositoryImpl(
            documentDao    = get(),
            tombstoneDao   = get(),
            llmApiService  = get(),
            simulatedLlm   = get(),
            moshi          = get(),
            context        = androidContext()
        )
    }

    singleOf(::BenefitRepositoryImpl) bind BenefitRepository::class
    singleOf(::AlertRepositoryImpl) bind AlertRepository::class

    // AiRepositoryImpl now takes DocumentDao for document-context injection
    single<AiRepository> {
        AiRepositoryImpl(
            conversationDao = get(),
            documentDao     = get(),
            llmApiService   = get(),
            simulatedLlm    = get()
        )
    }
}
