package app.revenge.manager.installer.step

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.installer.step.download.DownloadBaseStep
import app.revenge.manager.installer.step.download.DownloadLangStep
import app.revenge.manager.installer.step.download.DownloadLibsStep
import app.revenge.manager.installer.step.download.DownloadResourcesStep
import app.revenge.manager.installer.step.download.DownloadModStep
import app.revenge.manager.installer.step.installing.InstallStep
import app.revenge.manager.installer.step.patching.AddModStep
import app.revenge.manager.installer.step.patching.PatchManifestsStep
import app.revenge.manager.installer.step.patching.PresignApksStep
import app.revenge.manager.installer.step.patching.ReplaceIconStep
import app.revenge.manager.installer.util.LogEntry
import app.revenge.manager.installer.util.Logger
import app.revenge.manager.utils.DiscordVersion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Runs all installation steps in order
 *
 * Credit to rushii (github.com/rushiiMachine)
 *
 * @param discordVersion Version of Discord to inject Revenge into
 */
@Stable
class StepRunner(
    private val discordVersion: DiscordVersion
): KoinComponent {

    private val preferenceManager: PreferenceManager by inject()
    private val context: Context by inject()
    private val debugInfo = """
            ${app.revenge.manager.BuildConfig.MOD_NAME} Manager v${app.revenge.manager.BuildConfig.VERSION_NAME}
            Built from commit ${app.revenge.manager.BuildConfig.GIT_COMMIT} on ${app.revenge.manager.BuildConfig.GIT_BRANCH} ${if (app.revenge.manager.BuildConfig.GIT_LOCAL_CHANGES || app.revenge.manager.BuildConfig.GIT_LOCAL_COMMITS) "(Changes Present)" else ""}
            
            Running Android ${Build.VERSION.RELEASE}, API level ${Build.VERSION.SDK_INT}
            Supported ABIs: ${Build.SUPPORTED_ABIS.joinToString()}
            Device: ${Build.MANUFACTURER} - ${Build.MODEL} (${Build.DEVICE})
            ${if(Build.VERSION.SDK_INT > Build.VERSION_CODES.S) "SOC: ${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}\n" else "\n\n"} 
            Adding ${app.revenge.manager.BuildConfig.MOD_NAME} to Discord v$discordVersion
            
            
        """.trimIndent()

    /**
     * Logger associated with this runner
     */
    val logger = Logger("StepRunner").also { logger ->
        debugInfo.split("\n").forEach {
            logger.logs += LogEntry(it, LogEntry.Level.INFO) // Add debug information to logs but don't print to logcat
        }
    }

    /**
     * Root directory for all downloaded files
     */
    private val cacheDir =
        context.externalCacheDir
        ?: File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)
            .resolve(app.revenge.manager.BuildConfig.MANAGER_NAME)
            .also { it.mkdirs() }

    /**
     * Where version specific downloads are persisted
     */
    private val discordCacheDir = cacheDir.resolve(discordVersion.toVersionCode())

    /**
     * Working directory where apks are directly modified (i.e. replacing the app icon)
     */
    private val patchedDir = discordCacheDir.resolve("patched").also { it.deleteRecursively() }

    /**
     * Where apks are moved to once signed
     */
    private val signedDir = discordCacheDir.resolve("signed").also { it.deleteRecursively() }

    /**
     * Output directory for LSPatch
     */
    private val lspatchedDir = patchedDir.resolve("lspatched").also { it.deleteRecursively() }

    var currentStep by mutableStateOf<Step?>(null)
        private set

    /**
     * Whether or not the patching/installation process has completed.
     * Note that this does not mean all steps were finished successfully
     */
    var completed by mutableStateOf<Boolean>(false)
        private set

    /**
     * Whether or not a download step failed, this is only for errors related to network conditions and not cancellations
     */
    var downloadErrored by mutableStateOf<Boolean>(false)

    /**
     * List of steps to go through for this install
     *
     * ORDER MATTERS
     */
    val steps: ImmutableList<Step> = buildList {
        // Downloading
        add(DownloadBaseStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadLibsStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadLangStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadResourcesStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadModStep(patchedDir))

        // Patching
        if (preferenceManager.patchIcon) add(ReplaceIconStep())
        add(PatchManifestsStep())
        add(PresignApksStep(signedDir))
        add(AddModStep(signedDir, lspatchedDir))

        // Installing
        add(InstallStep(lspatchedDir))
    }.toImmutableList()

    /**
     * Get a step that has already been successfully executed.
     * This is used to retrieve previously executed dependency steps from a later step.
     */
    inline fun <reified T : Step> getCompletedStep(): T {
        val step = steps.asSequence()
            .filterIsInstance<T>()
            .filter { it.status == StepStatus.SUCCESSFUL }
            .firstOrNull()

        if (step == null) {
            throw IllegalArgumentException("No completed step ${T::class.simpleName} exists in container")
        }

        return step
    }

    /**
     * Clears all cached files
     */
    fun clearCache() {
        cacheDir.deleteRecursively()
    }

    /**
     * Run all the [steps] in order
     */
    suspend fun runAll(): Throwable? {
        for (step in steps) {
            if (completed) return null // Failsafe in case runner is incorrectly marked as not completed too early

            currentStep = step
            val error = step.runCatching(this)
            if (error != null) {
                logger.e("Failed on ${step::class.simpleName}", error)

                completed = true
                return error
            }

            // Add delay for human psychology and
            // better group visibility in UI (the active group can change way too fast)
            if (!preferenceManager.isDeveloper && step.durationMs < 1000) {
                delay(1000L - step.durationMs)
            }
        }

        completed = true
        return null
    }

}