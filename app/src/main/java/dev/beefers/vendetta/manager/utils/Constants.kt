package dev.beefers.vendetta.manager.utils

import android.os.Environment
import androidx.compose.ui.graphics.Color
import dev.beefers.vendetta.manager.BuildConfig

object Constants {
    val MOD_NAME = "Revenge"
    val MANAGER_NAME = MOD_NAME + "Manager"
    val PACKAGE_NAME = "app.revenge.app"
    val REPO = "revenge-mod/revenge"
    val ORG_LINK = "https://github.com/revenge-mod"
    val INVITE_LINK = "https://discord.gg/ddcQf3s2Uq"
    val MOD_COLOR = Color(0xFEB23A42)
    val MOD_COLOR_ALPHA = Color(0xFFFBB33C)
    val MOD_COLOR_OTHER = Color(0xFFD3575E)

    val TEAM_MEMBERS = listOf(
        TeamMember("Pylix", "Developer - Bunny & Vendetta", "amsyarasyiq"),
        TeamMember("Kasi", "Developer - Xposed Module", "redstonekasi")
    )

    // NOTE: This is no longer used
    val VENDETTA_DIR = Environment.getExternalStorageDirectory().resolve(MOD_NAME)

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