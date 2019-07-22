package com.wahdanz.testbridschallenge.devices

import android.app.*
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.wahdanz.testbridschallenge.MainActivity
import com.wahdanz.testbridschallenge.NoDistractionActivity
import com.wahdanz.testbridschallenge.domain.repository.AppsRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class AppsService : JobService() {
    private val appsRepository: AppsRepository by inject()
    private val disposables = CompositeDisposable()
    private lateinit var sUsageStatsManager: UsageStatsManager

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("AppsService", "Start")
        appsRepository.servesEnable(true)

        disposables.add(Observable
            .interval(1, TimeUnit.SECONDS)
            .flatMap<String> { appsRepository.getTopApp() }.distinctUntilChanged()
            .flatMap { appsRepository.isAppBlock(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it) {
                    val lockIntent = Intent(this, NoDistractionActivity::class.java)
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(lockIntent)
                }
            }, {
                Log.e("AppsService", it.message)
            })
        )

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("AppsService", "Start")
        appsRepository.servesEnable(false)
        return false
    }

    override fun onCreate() {
        super.onCreate()
        sUsageStatsManager = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val resultIntent = Intent(this, MainActivity::class.java)
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val resultPendingIntent = PendingIntent.getActivity(
                this,
                112 /* Request code */,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel("3007", " App background ", importance)
            mNotificationManager.createNotificationChannel(notificationChannel)
            val mBuilder = Notification.Builder(this)
            mBuilder.setSmallIcon(com.wahdanz.testbridschallenge.R.mipmap.ic_launcher)
            mBuilder.setChannelId("3007")
            mBuilder.setContentTitle("Test Birds")
                .setContentText("Focus")
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
            startForeground(145, mBuilder.build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    companion object {
        @JvmStatic
        fun startAppsService(context: Context) {
            val serviceComponent = ComponentName(context, AppsService::class.java)
            val builder = JobInfo.Builder(1337, serviceComponent)
            builder.setMinimumLatency((300).toLong()) // Wait at least 30s
            builder.setOverrideDeadline((600).toLong()) // Maximum delay 60s
            builder.setPersisted(true)
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(builder.build())
        }

        fun cancelJob(context: Context) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(1337)
        }
    }
}
