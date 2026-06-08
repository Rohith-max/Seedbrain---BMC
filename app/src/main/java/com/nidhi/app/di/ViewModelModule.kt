package com.nidhi.app.di

import com.nidhi.app.data.DemoDataSeeder
import com.nidhi.app.feature.ai.AiViewModel
import com.nidhi.app.feature.auth.AuthViewModel
import com.nidhi.app.feature.benefits.BenefitsViewModel
import com.nidhi.app.feature.calendar.CalendarViewModel
import com.nidhi.app.feature.document.DocumentViewModel
import com.nidhi.app.feature.emergency.EmergencyViewModel
import com.nidhi.app.feature.home.HomeViewModel
import com.nidhi.app.feature.notifications.AlertsViewModel
import com.nidhi.app.feature.settings.FamilyMembersViewModel
import com.nidhi.app.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::DocumentViewModel)
    viewModelOf(::BenefitsViewModel)
    viewModelOf(::AiViewModel)
    viewModelOf(::AlertsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::FamilyMembersViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::EmergencyViewModel)

    singleOf(::DemoDataSeeder)
}
