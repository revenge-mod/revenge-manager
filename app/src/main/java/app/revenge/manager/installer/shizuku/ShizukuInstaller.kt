package app.revenge.manager.installer.shizuku

import android.content.Context
import app.revenge.manager.R
import app.revenge.manager.domain.manager.InstallManager
import app.revenge.manager.installer.Installer
import app.revenge.manager.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import rikka.shizuku.Shizuku
import java.io.File
import java.util.UUID

class ShizukuInstaller(private val context: Context) : Installer, KoinComponent {

    val installManager: InstallManager by inject()

    override suspend fun installApks(silent: Boolean, unattended: Boolean, vararg apks: File) {
        val hasPermission = if (unattended) {
            ShizukuPermissions.hasPermission()
        } else {
            ShizukuPermissions.waitShizukuPermissions()
        }
        if (!hasPermission) {
            if (!unattended) {
                withContext(Dispatchers.Main) {
                    context.showToast(R.string.msg_shizuku_denied, short = false)
                }
            }

            throw Error("Failed to install due to missing Shizuku permissions")
        }

        withContext(Dispatchers.IO) {
            val remoteApks = mutableListOf<File>()
            try {
                apks.forEachIndexed { index, apk ->
                    val remote = File("/data/local/tmp", "revenge-update-${UUID.randomUUID()}-$index.apk")
                    remoteApks.add(remote)
                    executeShellCommand("cp ${shellQuote(apk.absolutePath)} ${shellQuote(remote.absolutePath)}")
                }

                val installCommand = if (remoteApks.size == 1) {
                    "pm install -r ${shellQuote(remoteApks.single().absolutePath)}"
                } else {
                    "pm install-multiple -r ${remoteApks.joinToString(" ") { shellQuote(it.absolutePath) }}"
                }
                val result = executeShellCommand(installCommand)
                if (result.lineSequence().none { it.trim() == "Success" }) {
                    throw RuntimeException("Package Manager did not report a successful install: $result")
                }

                installManager.getInstalled()
            } finally {
                if (remoteApks.isNotEmpty()) {
                    runCatching {
                        executeShellCommand("rm -f ${remoteApks.joinToString(" ") { shellQuote(it.absolutePath) }}")
                    }
                }
            }
        }
    }

    private fun executeShellCommand(command: String): String {
        @Suppress("DEPRECATION")
        val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)

        val output = process.inputStream.bufferedReader().use { it.readText().trim() }
        val error = process.errorStream.bufferedReader().use { it.readText().trim() }
        val exitCode = process.waitFor()
        if (exitCode != 0 || error.isNotBlank()) {
            throw RuntimeException("Failed to execute $command (exit $exitCode):\n${error.ifBlank { output }}")
        }
        return output
    }

    private fun shellQuote(value: String) = "'${value.replace("'", "'\\''")}'"

}
