package app.revenge.manager.updatechecker.worker

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.revenge.manager.domain.manager.InstallManager
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.domain.repository.RestRepository
import app.revenge.manager.network.utils.ApiResponse
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
                val currentVersion =
                    DiscordVersion.fromVersionCode(installManager.current?.longVersionCode.toString())
                val latestVersion = res.data[prefs.channel]

                if (latestVersion == null || currentVersion == null) return Result.failure()

                if (latestVersion > currentVersion) {
                    context.sendBroadcast(
                        Intent(
                            context,
                            UpdateBroadcastReceiver::class.java
                        ).apply {
                            putExtra(Intents.Extras.VERSION, latestVersion.toVersionCode())
                        })
                }

                Result.success()
            }

            else -> Result.failure()
        }
    }

}