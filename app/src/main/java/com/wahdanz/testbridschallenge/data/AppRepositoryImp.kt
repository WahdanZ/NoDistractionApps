package com.wahdanz.testbridschallenge.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.wahdanz.testbridschallenge.BuildConfig
import com.wahdanz.testbridschallenge.domain.entities.AppEntity
import com.wahdanz.testbridschallenge.domain.repository.AppsRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class AppRepositoryImp(
    private val preferences: SharedPreferences,
    private val packageManager: PackageManager,
    private val sUsageStatsManager: UsageStatsManager
) : AppsRepository {
    private val prefSubject = BehaviorSubject.createDefault(preferences)
    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
        prefSubject.onNext(sharedPreferences)
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun isServicesRunning() =
        preferences.getBoolean("ServiceRunning", false)

    override fun servesEnable(enable: Boolean): Any = preferences.edit().putBoolean("ServiceRunning", enable).apply()

    override fun isServesAccessibility() =
        preferences.getBoolean("serviceAccessibility", false)

    override fun servesAccessibilityEnable(enable: Boolean): Any =
        preferences.edit().putBoolean("serviceAccessibility", enable).apply()

    override fun lockApp(name: String): Completable {
        return prefSubject.firstOrError()
            .editSharedPreferences {
                putBoolean(name, true)
            }
    }

    override fun unLockApp(name: String): Completable {
        return prefSubject.firstOrError()
            .editSharedPreferences {
                remove(name)
            }
    }

    override fun isAppBlock(name: String): Observable<Boolean> {
        return Observable.just(preferences.contains(name))
    }

    override fun allApps(): Observable<List<AppEntity>> {
        return Observable.zip(allAppInstalled(), lockedApp(),
            BiFunction<List<PackageInfo>, List<String>, List<AppEntity>> { installedApps, lockedApps
                ->
                installedApps.map {
                    AppEntity(
                        getAppName(it.applicationInfo),
                        it.packageName,
                        lockedApps.contains(it.packageName)
                    )
                }
            })
    }

    private fun getAppName(applicationInfo: ApplicationInfo?) =
        packageManager.getApplicationLabel(applicationInfo).toString()

    override fun lockedApp(): Observable<List<String>> {
        return prefSubject.map { it.all.keys.toList() }
    }

    override fun getTopApp(): Observable<String> {
        return Observable.just(topApp()).filter { it != BuildConfig.APPLICATION_ID }
    }

    private fun topApp(): String {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }
        return result
    }

    private fun allAppInstalled() = Observable.just(packageManager.getInstalledPackages(PackageManager.GET_META_DATA))

    private fun Single<SharedPreferences>.editSharedPreferences(batch: SharedPreferences.Editor.() -> Unit): Completable =
        flatMapCompletable {
            Completable.fromAction {
                it.edit().also(batch).apply()
            }
        }
}