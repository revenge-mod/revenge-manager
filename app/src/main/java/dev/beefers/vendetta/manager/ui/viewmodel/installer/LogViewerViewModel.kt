package dev.beefers.vendetta.manager.ui.viewmodel.installer

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import cafe.adriel.voyager.core.model.ScreenModel
import dev.beefers.vendetta.manager.BuildConfig
import dev.beefers.vendetta.manager.R
import dev.beefers.vendetta.manager.installer.util.LogEntry
import dev.beefers.vendetta.manager.utils.copyText
import dev.beefers.vendetta.manager.utils.showToast
import java.io.File

class LogViewerViewModel(
    private val context: Context,
    val logs: List<LogEntry>
): ScreenModel {

    private val tempLogStorageDir = context.filesDir.resolve("logsTmp").also {
        it.mkdirs()
    }

    val logsString by lazy {
        logs.joinToString("\n") { it.toString() }
    }

    val maxLogLength = logs.maxOf { it.message.length }

    fun copyLog(log: LogEntry) {
        context.copyText(log.toString())
        context.showToast(R.string.msg_copied)
    }

    fun copyLogs() {
        context.copyText(logsString)
        context.showToast(R.string.msg_copied)
    }

    private fun saveToAppStorage(): File {
        tempLogStorageDir.deleteRecursively()
        tempLogStorageDir.mkdirs()

        val tmpFile = tempLogStorageDir.resolve("VD-Manager-${System.currentTimeMillis()}.log")
        tmpFile.outputStream().use { stream ->
            stream.write(logsString.toByteArray())
        }

        return tmpFile
    }

    fun shareLogs(activityContext: Context) {
        val saved = saveToAppStorage()
        val uri = FileProvider.getUriForFile(
            activityContext,
            BuildConfig.APPLICATION_ID + ".provider",
            saved
        )

        ShareCompat.IntentBuilder(activityContext)
            .setType("text/plain")
            .setStream(uri)
            .apply {
                intent.apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            .startChooser()
    }

}