package app.revenge.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.revenge.manager.R
import app.revenge.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the split containing all images, fonts, and other assets
 */
@Stable
class DownloadResourcesStep(
    dir: File,
    workingDir: File,
    version: String
): DownloadStep() {

    override val nameRes = R.string.step_dl_res

    override val url: String = "$baseUrl/tracker/download/$version/config.xxhdpi"
    override val destination = dir.resolve("config.xxhdpi-$version.apk")
    override val workingCopy = workingDir.resolve("config.xxhdpi-$version.apk")

}