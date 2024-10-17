package app.revenge.manager.installer.step.download.base

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.revenge.manager.R
import app.revenge.manager.domain.manager.DownloadManager
import app.revenge.manager.domain.manager.DownloadResult
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.installer.step.Step
import app.revenge.manager.installer.step.StepGroup
import app.revenge.manager.installer.step.StepRunner
import app.revenge.manager.installer.step.StepStatus
import app.revenge.manager.utils.mainThread
import app.revenge.manager.utils.showToast
import kotlinx.coroutines.CancellationException
import org.koin.core.component.inject
import java.io.File
import kotlin.math.roundToInt

/**
 * Specialized step used to download a file
 *
 * Files are downloaded to [destination] then copied to [workingCopy] for safe patching
 */
@Stable
abstract class DownloadStep: Step() {

    protected val preferenceManager: PreferenceManager by inject()
    protected val baseUrl = preferenceManager.mirror.baseUrl

    private val downloadManager: DownloadManager by inject()
    private val context: Context by inject()

    /**
     * Url of the desired file to download
     */
    abstract val url: String

    /**
     * Where to download the file to
     */
    abstract val destination: File

    /**
     * Where the downloaded file should be copied to so that it can be used for patching
     */
    abstract val workingCopy: File

    override val group: StepGroup = StepGroup.DL

    var cached by mutableStateOf(false)
        private set

    /**
     * Verifies that a file was properly downloaded
     */
    open suspend fun verify() {
        if (!destination.exists())
            error("Downloaded file is missing: ${destination.absolutePath}")

        if (destination.length() <= 0)
            error("Downloaded file is empty: ${destination.absolutePath}")
    }

    override suspend fun run(runner: StepRunner) {
        val fileName = destination.name
        runner.logger.i("Checking if $fileName is cached")
        if (destination.exists()) {
            runner.logger.i("Checking if $fileName isn't empty")
            if (destination.length() > 0) {
                runner.logger.i("$fileName is cached")
                cached = true

                runner.logger.i("Moving $fileName to working directory")
                destination.copyTo(workingCopy, true)

                status = StepStatus.SUCCESSFUL
                return
            }

            runner.logger.i("Deleting empty file: $fileName")
            destination.delete()
        }

        runner.logger.i("$fileName was not properly cached, downloading now")
        var lastProgress: Float? = null
        val result = downloadManager.download(url, destination) { newProgress ->
            progress = newProgress
            if (newProgress != lastProgress && newProgress != null) {
                lastProgress = newProgress
                runner.logger.d("$fileName download progress: ${(lastProgress!! * 100f).roundToInt()}%")
            }
        }

        when (result) {
            is DownloadResult.Success -> {
                try {
                    runner.logger.i("Verifying downloaded file")
                    verify()
                    runner.logger.i("$fileName downloaded successfully")
                } catch (t: Throwable) {
                    mainThread {
                        context.showToast(R.string.msg_download_verify_failed)
                    }

                    throw t
                }
                runner.logger.i("Moving $fileName to working directory")
                destination.copyTo(workingCopy, true)
            }

            is DownloadResult.Error -> {
                mainThread {
                    context.showToast(R.string.msg_download_failed)
                    runner.downloadErrored = true
                }

                throw Error("Failed to download: ${result.debugReason}")
            }

            is DownloadResult.Cancelled -> {
                status = StepStatus.UNSUCCESSFUL
                throw CancellationException("$fileName download cancelled")
            }
        }
    }

}