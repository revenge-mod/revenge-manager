package app.revenge.manager.installer.step

import androidx.annotation.StringRes
import app.revenge.manager.R

/**
 * Represents a group of [Step]s
 */
enum class StepGroup(@StringRes val nameRes: Int) {
    /**
     * All steps deal with downloading files remotely
     */
    DL(R.string.group_download),

    /**
     * Steps that modify the APKs
     */
    PATCHING(R.string.group_patch),

    /**
     * Only contains the [install step][app.revenge.manager.installer.step.installing.InstallStep]
     */
    INSTALLING(R.string.group_installing)
}