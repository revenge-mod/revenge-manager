package app.revenge.manager.domain.manager

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.core.content.getSystemService
import app.revenge.manager.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import java.io.File

class DownloadManager(
    private val context: Context,
    private val prefs: PreferenceManager
) {

    suspend fun downloadDiscordApk(version: String, out: File, onProgressUpdate: (Float?) -> Unit): DownloadResult =
        download("${prefs.mirror.baseUrl}/tracker/download/$version/base", out, onProgressUpdate)

    suspend fun downloadSplit(version: String, split: String, out: File, onProgressUpdate: (Float?) -> Unit): DownloadResult =
        download("${prefs.mirror.baseUrl}/tracker/download/$version/$split", out, onProgressUpdate)

    suspend fun downloadMod(out: File, onProgressUpdate: (Float?) -> Unit) =
        download(
            "https://github.com/revenge-mod/revenge-xposed/releases/latest/download/app-release.apk",
            out,
            onProgressUpdate
        )

    suspend fun downloadUpdate(out: File) =
        download(
            "https://github.com/revenge-mod/revenge-manager/releases/latest/download/Manager.apk",
            out
        ) {
            /* TODO: Update a progress bar in the update dialog */
        }

    /**
     * Start a cancellable download with the system [DownloadManager].
     * If the current [CoroutineScope] is cancelled, then the system download will be cancelled
     * almost immediately.
     * @param url Remote src url
     * @param out Target path to download to
     * @param onProgressUpdate Download progress update in a `[0,1]` range, and if null then the
     *                         download is currently in a pending state. This is called every 100ms.
     */
    suspend fun download(
        url: String,
        out: File,
        onProgressUpdate: (Float?) -> Unit
    ): DownloadResult {
        val downloadManager = context.getSystemService<DownloadManager>()
            ?: throw IllegalStateException("DownloadManager service is not available")

        val downloadId = DownloadManager.Request(Uri.parse(url))
            .setTitle("${BuildConfig.MOD_NAME} Manager")
            .setDescription("Downloading ${out.name}...")
            .setDestinationUri(Uri.fromFile(out))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .let(downloadManager::enqueue)

        var lastProgressTime = System.currentTimeMillis()

        // Repeatedly request download state until it is finished
        while (true) {
            try {
                // Hand over control to a suspend function to check for cancellation
                delay(100)
            } catch (_: CancellationException) {
                // If the running CoroutineScope has been cancelled, then gracefully cancel download
                downloadManager.remove(downloadId)
                return DownloadResult.Cancelled(systemTriggered = false)
            }

            // Request download status
            val cursor = DownloadManager.Query()
                .setFilterById(downloadId)
                .let(downloadManager::query)

            // No results in cursor, download was cancelled
            if (!cursor.moveToFirst()) {
                cursor.close()
                return DownloadResult.Cancelled(systemTriggered = true)
            }

            val statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusColumn)

            cursor.use {
                when (status) {
                    DownloadManager.STATUS_PENDING, DownloadManager.STATUS_PAUSED -> {
                        val currentTime = System.currentTimeMillis()
                        val elapsedTime = currentTime - lastProgressTime

                        if (elapsedTime >= 5000L) {
                            return DownloadResult.Error(debugReason = "Download timeout")
                        }

                        onProgressUpdate(null)
                    }

                    DownloadManager.STATUS_RUNNING ->
                        onProgressUpdate(getDownloadProgress(cursor))

                    DownloadManager.STATUS_SUCCESSFUL ->
                        return DownloadResult.Success

                    DownloadManager.STATUS_FAILED -> {
                        val reasonColumn = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                        val reason = cursor.getInt(reasonColumn)

                        return DownloadResult.Error(debugReason = convertErrorCode(reason))
                    }
                }
            }
        }
    }

    private fun getDownloadProgress(queryCursor: Cursor): Float? {
        val bytesColumn = queryCursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val bytes = queryCursor.getLong(bytesColumn)

        val totalBytesColumn = queryCursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        val totalBytes = queryCursor.getLong(totalBytesColumn)

        if (totalBytes <= 0) return null
        return bytes.toFloat() / totalBytes
    }

    private fun convertErrorCode(code: Int) = when (code) {
        DownloadManager.ERROR_UNKNOWN -> "UNKNOWN"
        DownloadManager.ERROR_FILE_ERROR -> "FILE_ERROR"
        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "UNHANDLED_HTTP_CODE"
        DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP_DATA_ERROR"
        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "TOO_MANY_REDIRECTS"
        DownloadManager.ERROR_INSUFFICIENT_SPACE -> "INSUFFICIENT_SPACE"
        DownloadManager.ERROR_DEVICE_NOT_FOUND -> "DEVICE_NOT_FOUND"
        DownloadManager.ERROR_CANNOT_RESUME -> "CANNOT_RESUME"
        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "FILE_ALREADY_EXISTS"
        /* DownloadManager.ERROR_BLOCKED */ 1010 -> "NETWORK_BLOCKED"
        else -> "UNKNOWN_CODE"
    }

}

sealed interface DownloadResult {
    data object Success : DownloadResult
    data class Cancelled(val systemTriggered: Boolean) : DownloadResult
    data class Error(val debugReason: String) : DownloadResult
}
