package app.revenge.manager.installer.step

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.koin.core.component.KoinComponent
import kotlin.time.measureTimedValue

/**
 * A distinct step to be ran while patching
 */
@Stable
abstract class Step: KoinComponent {

    /**
     * Group this step belongs to
     */
    abstract val group: StepGroup

    /**
     * Label used in the installer ui
     */
    @get:StringRes
    abstract val nameRes: Int

    /**
     * Current status for this step
     */
    var status by mutableStateOf(StepStatus.QUEUED)
        protected set

    /**
     * How much progress this step has made, use null if unknown
     */
    var progress by mutableStateOf<Float?>(null)
        protected set

    /**
     * How long this step took to run, in milliseconds
     */
    var durationMs by mutableIntStateOf(0)
        private set

    /**
     * Runs this step
     *
     * @param runner The host runner, used to share information between steps
     */
    protected abstract suspend fun run(runner: StepRunner)

    /**
     * Safely runs this step, catching any errors and timing how long it runs.
     *
     * @param runner The host runner, used to share information between steps
     */
    suspend fun runCatching(runner: StepRunner): Throwable? {
        if (status != StepStatus.QUEUED)
            throw IllegalStateException("Cannot execute a step that has already started")

        status = StepStatus.ONGOING

        val (error, time) = measureTimedValue {
            try {
                run(runner)
                status = StepStatus.SUCCESSFUL
                null
            } catch (t: Throwable) {
                status = StepStatus.UNSUCCESSFUL
                t
            }
        }

        durationMs = time.inWholeMilliseconds.toInt()
        return error
    }

}