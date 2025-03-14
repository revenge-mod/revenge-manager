package dev.beefers.vendetta.manager.installer.step.patching

import com.github.diamondminer88.zip.ZipReader
import com.github.diamondminer88.zip.ZipWriter
import dev.beefers.vendetta.manager.R
import dev.beefers.vendetta.manager.domain.manager.PreferenceManager
import dev.beefers.vendetta.manager.installer.step.Step
import dev.beefers.vendetta.manager.installer.step.StepGroup
import dev.beefers.vendetta.manager.installer.step.StepRunner
import dev.beefers.vendetta.manager.installer.step.download.DownloadBaseStep
import dev.beefers.vendetta.manager.installer.step.download.DownloadLangStep
import dev.beefers.vendetta.manager.installer.step.download.DownloadLibsStep
import dev.beefers.vendetta.manager.installer.step.download.DownloadResourcesStep
import dev.beefers.vendetta.manager.installer.util.ManifestPatcher
import org.koin.core.component.inject

/**
 * Modifies each APKs manifest in order to change the package and app name as well as whether or not its debuggable
 */
class PatchManifestsStep : Step() {

    private val preferences: PreferenceManager by inject()

    override val group = StepGroup.PATCHING
    override val nameRes = R.string.step_patch_manifests

    override suspend fun run(runner: StepRunner) {
        val baseApk = runner.getCompletedStep<DownloadBaseStep>().workingCopy
        val libsApk = runner.getCompletedStep<DownloadLibsStep>().workingCopy
        val langApk = runner.getCompletedStep<DownloadLangStep>().workingCopy
        val resApk = runner.getCompletedStep<DownloadResourcesStep>().workingCopy

        arrayOf(baseApk, libsApk, langApk, resApk).forEach { apk ->
            runner.logger.i("Reading AndroidManifest.xml from ${apk.name}")
            val manifest = ZipReader(apk)
                .use { zip -> zip.openEntry("AndroidManifest.xml")?.read() }
                ?: throw IllegalStateException("No manifest in ${apk.name}")

            ZipWriter(apk, true).use { zip ->
                runner.logger.i("Changing package and app name in ${apk.name}")
                val patchedManifestBytes = if (apk == baseApk) {
                    ManifestPatcher.patchManifest(
                        manifestBytes = manifest,
                        packageName = preferences.packageName,
                        appName = preferences.appName,
                        debuggable = preferences.debuggable,
                    )
                } else {
                    runner.logger.i("Changing package name in ${apk.name}")
                    ManifestPatcher.renamePackage(manifest, preferences.packageName)
                }

                runner.logger.i("Deleting old AndroidManifest.xml in ${apk.name}")
                zip.deleteEntry(
                    "AndroidManifest.xml",
                    /* fillVoid = */ apk == libsApk || apk == baseApk
                ) // Preserve alignment in libs apk

                runner.logger.i("Adding patched AndroidManifest.xml in ${apk.name}")
                zip.writeEntry("AndroidManifest.xml", patchedManifestBytes)
            }
        }
    }

}