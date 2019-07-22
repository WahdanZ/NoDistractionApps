package com.wahdanz.testbridschallenge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.wahdanz.testbridschallenge.devices.AppsService
import com.wahdanz.testbridschallenge.domain.entities.AppEntity
import com.wahdanz.testbridschallenge.domain.repository.AppsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AppsViewModel(application: Application, private val appsRepository: AppsRepository) :
    AndroidViewModel(application) {

    private val allApps = MutableLiveData<List<AppEntity>>()
    private val disposables = CompositeDisposable()

    fun getAllApps() {
        disposables.add(appsRepository.allApps().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                allApps.postValue(it)
            })
    }

    fun allApps(): MutableLiveData<List<AppEntity>> = allApps

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    fun lockApp(packageName: String) {
        appsRepository.lockApp(packageName).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun unLockApp(packageName: String) {
        appsRepository.unLockApp(packageName).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun serviceEnable(serviceEnable: Boolean, accessibilityAppServices: Boolean) {
        appsRepository.servesAccessibilityEnable(accessibilityAppServices)
        if (accessibilityAppServices && serviceEnable) {
            AppsService.cancelJob(getApplication())
        } else if (serviceEnable) {
            AppsService.startAppsService(getApplication())
        }
    }

    fun isServesRunning() = appsRepository.isServicesRunning()
    fun isAccessibilityEnable() = appsRepository.isServesAccessibility()
}