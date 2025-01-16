package app.revenge.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.revenge.manager.R
import app.revenge.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the base Discord APK
 */
@Stable
class DownloadBaseStep(
    dir: File,
    workingDir: File,
    version: String
): DownloadStep() {

    override val nameRes = R.string.step_dl_base

    override val downloadMirrorUrlPath: String = "/tracker/download/$version/base"
    override val destination = dir.resolve("base-$version.apk")
    override val workingCopy = workingDir.resolve("base-$version.apk")

}