package com.wahdanz.testbridschallenge.domain.repository

import com.wahdanz.testbridschallenge.domain.entities.AppEntity
import io.reactivex.Completable
import io.reactivex.Observable

interface AppsRepository {
    fun lockApp(name: String): Completable
    fun unLockApp(name: String): Completable
    fun allApps(): Observable<List<AppEntity>>
    fun lockedApp(): Observable<List<String>>
    fun getTopApp(): Observable<String>
    fun isAppBlock(name: String): Observable<Boolean>
    fun servesEnable(enable: Boolean): Any
    fun isServicesRunning(): Boolean
    fun isServesAccessibility(): Boolean
    fun servesAccessibilityEnable(enable: Boolean): Any
}