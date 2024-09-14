package dev.beefers.vendetta.manager.installer.step.download

import androidx.compose.runtime.Stable
import dev.beefers.vendetta.manager.R
import dev.beefers.vendetta.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the Vendetta XPosed module
 *
 * https://github.com/pyoncord/BunnyXposed
 */
@Stable
class DownloadVendettaStep(
    workingDir: File
): DownloadStep() {

    override val nameRes = R.string.step_dl_vd

    override val url: String = "https://github.com/revenge/RevengeXposed/releases/latest/download/app-release.apk"
    override val destination = preferenceManager.moduleLocation
    override val workingCopy = workingDir.resolve("xposed.apk")

}
