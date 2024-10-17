package app.revenge.manager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.revenge.manager.di.httpModule
import app.revenge.manager.di.managerModule
import app.revenge.manager.di.repositoryModule
import app.revenge.manager.di.viewModelModule
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.domain.manager.UpdateCheckerDuration
import app.revenge.manager.updatechecker.worker.UpdateWorker
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initNotificationChannels()

        startKoin {
            androidContext(this@ManagerApplication)
            modules(
                httpModule,
                managerModule,
                viewModelModule,
                repositoryModule
            )
        }

        val prefs: PreferenceManager = get()

        if (prefs.updateDuration != UpdateCheckerDuration.DISABLED) {
            val duration = prefs.updateDuration
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "app.revenge.manager.UPDATE_CHECK",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<UpdateWorker>(duration.time, duration.unit).build()
            )
        }
    }

    private fun initNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val updates = NotificationChannel(
            "${BuildConfig.APPLICATION_ID}.notifications.UPDATE",
            "Discord updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        nm.createNotificationChannel(updates)
    }

}