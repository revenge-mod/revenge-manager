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

class ShizukuInstaller(private val context: Context) : Installer, KoinComponent {

    val installManager: InstallManager by inject()

    override suspend fun installApks(silent: Boolean, vararg apks: File) {
        if (!ShizukuPermissions.waitShizukuPermissions()) {
            withContext(Dispatchers.Main) {
                context.showToast(R.string.msg_shizuku_denied, short = false)
            }

            throw Error("Failed to install due to missing Shizuku permissions")
        }

        val tempDir = File("/data/local/tmp")
        val movedApks = mutableListOf<File>()

        // Copy each split to tmp
        apks.forEach {
            val moveCommand = "cp ${it.absolutePath} ${tempDir.absolutePath}"
            val moveResult = executeShellCommand(moveCommand)

            if(moveResult.isBlank())
                movedApks.add(File(tempDir.absolutePath, it.name))
            else
                throw RuntimeException("Failed to move ${it.absolutePath} to temp dir")
        }

        val installCommand = "pm install ${movedApks.joinToString(" ") { it.absolutePath }}"
        executeShellCommand(installCommand)

        installManager.getInstalled()
        movedApks.forEach {
            it.delete()
        }
    }

    private fun executeShellCommand(command: String): String {
        @Suppress("DEPRECATION")
        val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)

        val errorStr = process.errorStream.bufferedReader().use { it.readText().trim() }
        if(errorStr.isNotBlank()) throw RuntimeException("Failed to execute $command:\n\n$errorStr")

        return process.inputStream.bufferedReader().use { it.readText().trim() }
    }

}
