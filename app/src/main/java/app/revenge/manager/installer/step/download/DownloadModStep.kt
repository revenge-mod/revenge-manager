package app.revenge.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.revenge.manager.R
import app.revenge.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the Revenge XPosed module
 *
 * https://github.com/revenge-mod/revenge-xposed
 */
@Stable
class DownloadModStep(
    workingDir: File,
    override val url: String
): DownloadStep() {

    override val nameRes = R.string.step_dl_mod

    override val destination = preferenceManager.moduleLocation
    override val workingCopy = workingDir.resolve("xposed.apk")

}
