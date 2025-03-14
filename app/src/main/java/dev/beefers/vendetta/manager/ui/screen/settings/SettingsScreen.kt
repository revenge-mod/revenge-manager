package dev.beefers.vendetta.manager.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.beefers.vendetta.manager.BuildConfig
import dev.beefers.vendetta.manager.R
import dev.beefers.vendetta.manager.domain.manager.PreferenceManager
import dev.beefers.vendetta.manager.ui.components.settings.SettingsCategory
import dev.beefers.vendetta.manager.ui.screen.about.AboutScreen
import dev.beefers.vendetta.manager.utils.DimenUtils
import org.koin.compose.koinInject

class SettingsScreen : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val preferences: PreferenceManager = koinInject()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                SettingsCategory(
                    icon = Icons.Outlined.Palette,
                    text = stringResource(R.string.settings_appearance),
                    subtext = stringResource(R.string.settings_appearance_description),
                    destination = ::AppearanceSettings
                )

                SettingsCategory(
                    icon = Icons.Outlined.AutoAwesome,
                    text = stringResource(R.string.settings_customization),
                    subtext = stringResource(R.string.settings_customization_description),
                    destination = ::CustomizationSettings
                )

                SettingsCategory(
                    icon = Icons.Outlined.Tune,
                    text = stringResource(R.string.settings_advanced),
                    subtext = stringResource(R.string.settings_advanced_description),
                    destination = ::AdvancedSettings
                )

                if (preferences.isDeveloper) {
                    SettingsCategory(
                        icon = Icons.Outlined.Code,
                        text = stringResource(R.string.settings_developer),
                        subtext = stringResource(R.string.settings_developer_description),
                        destination = ::DeveloperSettings
                    )
                }

                SettingsCategory(
                    icon = Icons.Outlined.Info,
                    text = stringResource(R.string.title_about),
                    subtext = buildString {
                        append(stringResource(R.string.app_name))
                        append(" v${BuildConfig.VERSION_NAME}")
                        if (preferences.isDeveloper) {
                            append(" (${BuildConfig.GIT_COMMIT}")
                            if (BuildConfig.GIT_LOCAL_CHANGES || BuildConfig.GIT_LOCAL_COMMITS) {
                                append(" - Local")
                            }
                            append(")")
                        }
                    },
                    destination = ::AboutScreen
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
                Text(stringResource(R.string.title_settings))
            },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

}