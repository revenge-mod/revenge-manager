package dev.beefers.vendetta.manager.domain.manager

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.StringRes
import androidx.compose.ui.unit.Constraints
import dev.beefers.vendetta.manager.R
import dev.beefers.vendetta.manager.domain.manager.base.BasePreferenceManager
import dev.beefers.vendetta.manager.utils.Constants
import dev.beefers.vendetta.manager.utils.DiscordVersion
import java.io.File
import java.util.concurrent.TimeUnit

class PreferenceManager(context: Context) :
    BasePreferenceManager(context.getSharedPreferences("prefs", Context.MODE_PRIVATE)) {

    val DEFAULT_MODULE_LOCATION =
        (context.externalCacheDir ?: File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS).resolve(Constants.MANAGER_NAME).also { it.mkdirs() }).resolve("xposed.apk")

    var packageName by stringPreference("package_name", Constants.PACKAGE_NAME)

    var appName by stringPreference("app_name", Constants.MOD_NAME)

    var discordVersion by stringPreference("discord_version", "")

    var moduleVersion by stringPreference("module_version", "")

    var patchIcon by booleanPreference("patch_icon", true)

    var debuggable by booleanPreference("debuggable", false)

    var mirror by enumPreference("mirror", Mirror.DEFAULT)

    var monet by booleanPreference("monet", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    var isDeveloper by booleanPreference("is_developer", false)

    var autoClearCache by booleanPreference("auto_clear_cache", true)

    var theme by enumPreference("theme", Theme.SYSTEM)

    var channel by enumPreference("channel", DiscordVersion.Type.STABLE)

    var updateDuration by enumPreference("update_duration", UpdateCheckerDuration.HOURLY)

    var moduleLocation by filePreference("module_location", DEFAULT_MODULE_LOCATION)

    var installMethod by enumPreference("install_method", InstallMethod.DEFAULT)

    var logsAlternateBackground by booleanPreference("logs_alternate_bg", true)

    var logsLineWrap by booleanPreference("logs_line_wrap", false)

    var allowDowngrade by booleanPreference("allow_downgrade", false)

    init {
        // Will be removed next update
        if(mirror == Mirror.VENDETTA_ROCKS) mirror = Mirror.VENDETTA_ROCKS_ALT
    }

}

enum class Theme(@StringRes val labelRes: Int) {
    SYSTEM(R.string.theme_system),
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark)
}

enum class UpdateCheckerDuration(@StringRes val labelRes: Int, val time: Long, val unit: TimeUnit) {
    DISABLED(R.string.duration_disabled, 0, TimeUnit.SECONDS),
    QUARTERLY(R.string.duration_fifteen_min, 15, TimeUnit.MINUTES),
    HALF_HOUR(R.string.duration_half_hour, 30, TimeUnit.MINUTES),
    HOURLY(R.string.duration_hourly, 1, TimeUnit.HOURS),
    BIHOURLY(R.string.duration_bihourly, 2, TimeUnit.HOURS),
    TWICE_DAILY(R.string.duration_twice_daily, 12, TimeUnit.HOURS),
    DAILY(R.string.duration_daily, 1, TimeUnit.DAYS),
    WEEKLY(R.string.duration_weekly, 7, TimeUnit.DAYS)
}

enum class Mirror(val baseUrl: String) {
    DEFAULT("https://tracker.vendetta.rocks"),
    VENDETTA_ROCKS("https://proxy.vendetta.rocks"), // Temporarily added for compatibility
    VENDETTA_ROCKS_ALT("https://proxy.vendetta.rocks"),
    K6("https://vd.k6.tf"),
    NEXPID("https://tracker.vd.nexpid.xyz")
}

enum class InstallMethod(@StringRes val labelRes: Int) {
    DEFAULT(R.string.default_installer),
    SHIZUKU(R.string.shizuku_installer)
}