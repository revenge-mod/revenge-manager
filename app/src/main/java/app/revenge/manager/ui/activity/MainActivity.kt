package app.revenge.manager.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import app.revenge.manager.domain.manager.InstallMethod
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.installer.shizuku.ShizukuPermissions
import app.revenge.manager.ui.screen.home.HomeScreen
import app.revenge.manager.ui.screen.installer.InstallerScreen
import app.revenge.manager.ui.theme.RevengeManagerTheme
import app.revenge.manager.utils.DiscordVersion
import app.revenge.manager.utils.Intents
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferences: PreferenceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val version = intent.getStringExtra(Intents.Extras.VERSION)

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                0
            )
        }

        if (preferences.installMethod == InstallMethod.SHIZUKU) {
            lifecycleScope.launch {
                if (!ShizukuPermissions.waitShizukuPermissions()) {
                    preferences.installMethod = InstallMethod.DEFAULT
                }
            }
        }

        val screen = if (intent.action == Intents.Actions.INSTALL && version != null) {
            InstallerScreen(DiscordVersion.fromVersionCode(version)!!)
        } else {
            HomeScreen()
        }

        setContent {
            RevengeManagerTheme {
                Navigator(screen) {
                    SlideTransition(it)
                }
            }
        }
    }

}
