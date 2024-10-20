package app.revenge.manager.installer.step.patching

import android.os.Build
import com.github.diamondminer88.zip.ZipCompression
import com.github.diamondminer88.zip.ZipReader
import com.github.diamondminer88.zip.ZipWriter
import app.revenge.manager.R
import app.revenge.manager.installer.step.Step
import app.revenge.manager.installer.step.StepGroup
import app.revenge.manager.installer.step.StepRunner
import app.revenge.manager.installer.step.download.DownloadBaseStep
import app.revenge.manager.installer.step.download.DownloadLangStep
import app.revenge.manager.installer.step.download.DownloadLibsStep
import app.revenge.manager.installer.step.download.DownloadResourcesStep
import app.revenge.manager.installer.util.Signer
import java.io.File

/**
 * Sign all patched apks before being ran through LSPatch, this is required due to LSPatch not liking unsigned apks.
 *
 * @param signedDir Where to output all signed apks
 */
class PresignApksStep(
    private val signedDir: File
) : Step() {

    override val group = StepGroup.PATCHING
    override val nameRes = R.string.step_signing

    override suspend fun run(runner: StepRunner) {
        val baseApk = runner.getCompletedStep<DownloadBaseStep>().workingCopy
        val libsApk = runner.getCompletedStep<DownloadLibsStep>().workingCopy
        val langApk = runner.getCompletedStep<DownloadLangStep>().workingCopy
        val resApk = runner.getCompletedStep<DownloadResourcesStep>().workingCopy

        runner.logger.i("Creating dir for signed apks: ${signedDir.absolutePath}")
        signedDir.mkdirs()
        val apks = listOf(baseApk, libsApk, langApk, resApk)

        // Align resources.arsc due to targeting api 30 for silent install
        if(Build.VERSION.SDK_INT >= 30) {
            for (file in apks) {
                runner.logger.i("Byte aligning ${file.name}")
                val bytes = ZipReader(file).use {
                    if (it.entryNames.contains("resources.arsc")) {
                        it.openEntry("resources.arsc")?.read()
                    } else {
                        null
                    }
                } ?: continue

                ZipWriter(file, true).use {
                    runner.logger.i("Removing old resources.arsc")
                    it.deleteEntry("resources.arsc", true)

                    runner.logger.i("Adding aligned resources.arsc")
                    it.writeEntry("resources.arsc", bytes, ZipCompression.NONE, 4096)
                }
            }
        }

        apks.forEach {
            runner.logger.i("Signing ${it.name}")
            Signer.signApk(it, File(signedDir, it.name))
        }
    }

}