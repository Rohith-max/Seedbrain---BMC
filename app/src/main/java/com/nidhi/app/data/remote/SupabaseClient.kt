package com.nidhi.app.data.remote

import com.nidhi.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

/**
 * Supabase client singleton.
 * URL and anon key are injected from BuildConfig (read from secrets.properties).
 *
 * Modules enabled:
 *  - Auth       → user sign-in / sign-up / JWT management
 *  - Postgrest  → database read/write (documents, benefits, alerts)
 *  - Storage    → document image uploads
 *  - Realtime   → live sync for alerts and family data
 */
val supabaseClient: SupabaseClient by lazy {
    createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
