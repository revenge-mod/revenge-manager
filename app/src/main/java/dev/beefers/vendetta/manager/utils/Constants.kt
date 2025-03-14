package dev.beefers.vendetta.manager.utils

import android.os.Environment
import dev.beefers.vendetta.manager.BuildConfig

object Constants {

    val TEAM_MEMBERS = listOf(
        TeamMember("Pylix", "Developer - Bunny & Vendetta", "pylixonly"),
        TeamMember("Kasi", "Developer - Xposed Module", "redstonekasi")
    )

    // NOTE: This is no longer used
    val VENDETTA_DIR = Environment.getExternalStorageDirectory().resolve("Bunny")

    val DUMMY_VERSION = DiscordVersion(1, 0, DiscordVersion.Type.STABLE)

    val START_TIME = System.currentTimeMillis()
}

object Intents {

    object Actions {
        const val INSTALL = "${BuildConfig.APPLICATION_ID}.intents.actions.INSTALL"
    }

    object Extras {
        const val VERSION = "${BuildConfig.APPLICATION_ID}.intents.extras.VERSION"
    }

}

object Channels {
    const val UPDATE = "${BuildConfig.APPLICATION_ID}.notifications.UPDATE"
}

data class TeamMember(
    val name: String,
    val role: String,
    val username: String = name
)