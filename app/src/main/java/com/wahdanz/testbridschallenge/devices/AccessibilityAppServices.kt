package com.wahdanz.testbridschallenge.devices

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import com.wahdanz.testbridschallenge.NoDistractionActivity
import com.wahdanz.testbridschallenge.domain.repository.AppsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class AccessibilityAppServices : AccessibilityService() {
    private val appsRepository: AppsRepository by inject()
    private val disposables = CompositeDisposable()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!appsRepository.isServesAccessibility())
            return
        appsRepository.servesEnable(true)
        if (event.eventType == TYPE_WINDOW_STATE_CHANGED) {
            try {
                val componentName = ComponentName(event.packageName.toString(), event.className.toString())
                val packageName = componentName.packageName
                disposables.add(appsRepository.isAppBlock(packageName).subscribeOn(Schedulers.io()).distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                        if (it) {
                            val lockIntent = Intent(this, NoDistractionActivity::class.java)
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(lockIntent)
                        }
                    })
            } catch (e: Exception) {
                Log.e("Accessibility", e.message)
            }
        }
    }

    override fun onInterrupt() {
        disposables.clear()
    }
}