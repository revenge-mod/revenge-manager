package app.revenge.manager.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import app.revenge.manager.R
import app.revenge.manager.domain.manager.InstallManager
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.ui.components.settings.SettingsButton
import app.revenge.manager.ui.components.settings.SettingsSwitch
import app.revenge.manager.ui.components.settings.SettingsTextField
import app.revenge.manager.utils.DimenUtils
import app.revenge.manager.utils.DiscordVersion
import org.koin.androidx.compose.get
import java.io.File

class DeveloperSettings: Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val prefs: PreferenceManager = get()
        val installManager: InstallManager = get()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        var version by remember {
            mutableStateOf(prefs.discordVersion)
        }
        var versionError by remember {
            mutableStateOf(false)
        }

        val supportingText = when {
            versionError -> stringResource(R.string.msg_invalid_version)
            version.isNotBlank() -> DiscordVersion.fromVersionCode(version).toString()
            else -> null
        }

        var moduleLocation by remember {
            mutableStateOf(prefs.moduleLocation.absolutePath)
        }

        Scaffold(
            topBar = { TitleBar(scrollBehavior) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = DimenUtils.navBarPadding)
            ) {
                SettingsTextField(
                    label = stringResource(R.string.settings_package_name),
                    pref = prefs.packageName,
                    onPrefChange = {
                        prefs.packageName = it
                        installManager.getInstalled()
                    }
                )

                SettingsTextField(
                    label = stringResource(R.string.settings_version),
                    pref = version,
                    error = versionError,
                    supportingText = supportingText,
                    onPrefChange = {
                        version = it
                        if (DiscordVersion.fromVersionCode(it) == null && it.isNotBlank()) {
                            versionError = true
                        } else {
                            versionError = false
                            prefs.discordVersion = it
                        }
                    }
                )

                SettingsSwitch(
                    label = stringResource(R.string.settings_debuggable),
                    secondaryLabel = stringResource(R.string.settings_debuggable_description),
                    pref = prefs.debuggable,
                    onPrefChange = { prefs.debuggable = it }
                )

                SettingsTextField(
                    label = stringResource(R.string.settings_module_location),
                    supportingText = stringResource(R.string.settings_module_location_description),
                    pref = moduleLocation,
                    onPrefChange = {
                        moduleLocation = it
                        prefs.moduleLocation = File(it)
                    }
                )

                SettingsButton(
                    label = stringResource(R.string.settings_module_location_reset),
                    onClick = {
                        prefs.moduleLocation = prefs.DEFAULT_MODULE_LOCATION
                    }
                )

                SettingsSwitch(
                    label = stringResource(R.string.settings_allow_downgrade),
                    secondaryLabel = stringResource(R.string.settings_allow_downgrade_description),
                    pref = prefs.allowDowngrade,
                    onPrefChange = { prefs.allowDowngrade = it }
                )
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun TitleBar(
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        val navigator = LocalNavigator.currentOrThrow

        LargeTopAppBar(
            title = {
                Text(stringResource(R.string.settings_developer))
            },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

}