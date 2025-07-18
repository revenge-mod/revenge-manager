package app.revenge.manager.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import app.revenge.manager.BuildConfig
import app.revenge.manager.R
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.ui.components.SegmentedButton
import app.revenge.manager.ui.screen.installer.InstallerScreen
import app.revenge.manager.ui.screen.settings.SettingsScreen
import app.revenge.manager.ui.viewmodel.home.HomeViewModel
import app.revenge.manager.ui.widgets.AppIcon
import app.revenge.manager.ui.widgets.dialog.StoragePermissionsDialog
import app.revenge.manager.ui.widgets.home.CommitList
import app.revenge.manager.ui.widgets.updater.UpdateDialog
import app.revenge.manager.utils.Constants
import app.revenge.manager.utils.DiscordVersion
import app.revenge.manager.utils.navigate
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.get

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val prefs: PreferenceManager = koinInject()
        val viewModel: HomeViewModel = koinScreenModel()

        val currentVersion = remember {
            DiscordVersion.fromVersionCode(viewModel.installManager.current?.longVersionCode.toString())
        }

        val latestVersion =
            remember(prefs.discordVersion, viewModel.discordVersions, prefs.channel) {
                when {
                    prefs.discordVersion.isBlank() -> viewModel.discordVersions?.get(prefs.channel)
                    else -> DiscordVersion.fromVersionCode(prefs.discordVersion)
                }
            }

        // == Dialogs == //

        StoragePermissionsDialog()

        if (
            viewModel.showUpdateDialog &&
            viewModel.release != null &&
            !BuildConfig.DEBUG
        ) {
            var progress by remember { mutableStateOf<Float?>(null) }

            UpdateDialog(
                release = viewModel.release!!,
                isUpdating = viewModel.isUpdating,
                progress = progress,
                onDismiss = { viewModel.showUpdateDialog = false },
                onConfirm = {
                    viewModel.downloadAndInstallUpdate { newProgress -> progress = newProgress }
                }
            )
        }

        // == Screen == //

        Scaffold(
            topBar = { TitleBar() },
        ) { pv ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(pv)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                AppIcon(
                    customIcon = prefs.patchIcon,
                    releaseChannel = prefs.channel,
                    modifier = Modifier.size(60.dp)
                )

                Text(
                    text = prefs.appName,
                    style = MaterialTheme.typography.titleLarge
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedVisibility(visible = currentVersion != null) {
                        Text(
                            text = stringResource(
                                R.string.version_current,
                                currentVersion.toString()
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = LocalContentColor.current.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }

                    val latestLabel =
                        if (prefs.discordVersion.isNotBlank()) R.string.version_target else R.string.version_latest

                    AnimatedVisibility(visible = latestVersion != null) {
                        Text(
                            text = stringResource(latestLabel, latestVersion.toString()),
                            style = MaterialTheme.typography.labelLarge,
                            color = LocalContentColor.current.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Button(
                    onClick = {
                        navigator.navigate(InstallerScreen(latestVersion!!))
                    },
                    enabled = latestVersion != null && (prefs.allowDowngrade || latestVersion >= (currentVersion ?: Constants.DUMMY_VERSION)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val label = when {
                        latestVersion == null -> R.string.msg_loading
                        currentVersion == null -> R.string.action_install
                        currentVersion == latestVersion -> R.string.action_reinstall
                        latestVersion > currentVersion -> R.string.action_update
                        else -> if (prefs.allowDowngrade) R.string.msg_downgrade else R.string.msg_downgrade_disallowed
                    }

                    Text(
                        text = stringResource(label),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee()
                            .fillMaxWidth()
                    )
                }

                AnimatedVisibility(visible = viewModel.installManager.current != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        SegmentedButton(
                            icon = Icons.AutoMirrored.Filled.OpenInNew,
                            text = stringResource(R.string.action_launch),
                            onClick = { viewModel.launchMod() }
                        )
                        SegmentedButton(
                            icon = Icons.Filled.Info,
                            text = stringResource(R.string.action_info),
                            onClick = { viewModel.launchModInfo() }
                        )
                        SegmentedButton(
                            icon = Icons.Filled.Delete,
                            text = stringResource(R.string.action_uninstall),
                            onClick = { viewModel.uninstallMod() }
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CommitList(
                        commits = viewModel.commits.collectAsLazyPagingItems()
                    )
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun TitleBar() {
        TopAppBar(
            title = { Text(stringResource(R.string.title_home)) },
            actions = { Actions() }
        )
    }

    @Composable
    private fun Actions() {
        val viewModel: HomeViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        IconButton(onClick = { viewModel.getDiscordVersions() }) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = stringResource(R.string.action_reload)
            )
        }
        IconButton(onClick = { navigator.navigate(SettingsScreen()) }) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.action_open_about)
            )
        }
    }

}