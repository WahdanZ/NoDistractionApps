package com.wahdanz.testbridschallenge.di

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.wahdanz.testbridschallenge.data.AppRepositoryImp
import com.wahdanz.testbridschallenge.domain.repository.AppsRepository
import com.wahdanz.testbridschallenge.viewmodel.AppsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module(override = true) {
    single<AppsRepository> { AppRepositoryImp(get(), get(), get()) }
    single {
        getSharedPrefs(androidApplication())
    }
    single {
        getPackageManager(androidApplication())
    }
    single {
        getUsageStatsManager(androidApplication())
    }
    viewModel { AppsViewModel(get(), get()) }
}

fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    return androidApplication.getSharedPreferences("test_birds", Context.MODE_PRIVATE)
}

fun getPackageManager(androidApplication: Application): PackageManager {
    return androidApplication.packageManager
}

fun getUsageStatsManager(androidApplication: Application): UsageStatsManager {
    return androidApplication.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
}