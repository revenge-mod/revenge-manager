package app.revenge.manager.updatechecker.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import app.revenge.manager.domain.manager.InstallMethod
import app.revenge.manager.domain.manager.InstallManager
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.domain.manager.UpdateCheckerDuration
import app.revenge.manager.domain.repository.RestRepository
import app.revenge.manager.installer.shizuku.ShizukuPermissions
import app.revenge.manager.installer.step.StepRunner
import app.revenge.manager.network.utils.ApiResponse
import app.revenge.manager.updatechecker.UpdateNotifications
import app.revenge.manager.updatechecker.reciever.UpdateBroadcastReceiver
import app.revenge.manager.utils.DiscordVersion
import app.revenge.manager.utils.Intents
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    val api: RestRepository by inject()
    val prefs: PreferenceManager by inject()
    val installManager: InstallManager by inject()

    override suspend fun doWork(): Result {
        if (prefs.discordVersion.isNotBlank()) return Result.success()
        return when (val res = api.getLatestDiscordVersions()) {
            is ApiResponse.Success -> {
                installManager.getInstalled()
                val currentVersion =
                    DiscordVersion.fromVersionCode(installManager.current?.versionCode.toString())
                val latestVersion = res.data[prefs.channel]

                if (latestVersion == null) return Result.failure()
                if (currentVersion == null) return Result.success()

                if (latestVersion > currentVersion) {
                    val versionCode = latestVersion.toVersionCode()
                    if (!prefs.unattendedUpdates) {
                        notifyUpdateAvailable(versionCode)
                        return Result.success()
                    }

                    if (prefs.installMethod == InstallMethod.SHIZUKU && !ShizukuPermissions.hasPermission()) {
                        Log.w(TAG, "Cannot run unattended update because Shizuku is not available")
                        notifyUpdateAvailable(versionCode)
                        return Result.success()
                    }

                    val error = runCatching {
                        StepRunner(latestVersion, unattended = true).runAll()
                    }.getOrElse { it }

                    if (error is CancellationException) throw error

                    if (error != null) {
                        Log.e(TAG, "Unattended update to $latestVersion failed", error)
                        notifyUpdateAvailable(versionCode)
                    } else if (prefs.installMethod == InstallMethod.SHIZUKU) {
                        UpdateNotifications.showInstalled(context)
                    }
                }

                Result.success()
            }

            else -> Result.failure()
        }
    }

    private fun notifyUpdateAvailable(versionCode: String) {
        context.sendBroadcast(
            Intent(context, UpdateBroadcastReceiver::class.java).apply {
                putExtra(Intents.Extras.VERSION, versionCode)
            }
        )
    }

    companion object {
        const val WORK_NAME = "app.revenge.manager.UPDATE_CHECK"
        private const val TAG = "UpdateWorker"

        fun periodicRequest(duration: UpdateCheckerDuration): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<UpdateWorker>(duration.time, duration.unit)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
    }

}
