package app.revenge.manager.ui.screen.settings

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import app.revenge.manager.R
import app.revenge.manager.domain.manager.Mirror
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.ui.components.settings.SettingsButton
import app.revenge.manager.ui.components.settings.SettingsHeader
import app.revenge.manager.ui.components.settings.SettingsItemChoice
import app.revenge.manager.ui.components.settings.SettingsSwitch
import app.revenge.manager.ui.viewmodel.settings.AdvancedSettingsViewModel
import app.revenge.manager.utils.DimenUtils
import app.revenge.manager.utils.copyText
import app.revenge.manager.utils.showToast
import org.koin.androidx.compose.get

class AdvancedSettings: Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val ctx = LocalContext.current
        val prefs: PreferenceManager = get()
        val viewModel: AdvancedSettingsViewModel = getScreenModel()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        var showImportDialog by remember { mutableStateOf(false) }
        var showReplaceWarning by remember { mutableStateOf(false) }
        var signingKeyText by remember { mutableStateOf("") }
        var signingKeyError by remember { mutableStateOf(false) }

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
                SettingsItemChoice(
                    label = stringResource(R.string.settings_check_updates),
                    pref = prefs.updateDuration,
                    labelFactory = {
                        ctx.getString(it.labelRes)
                    },
                    onPrefChange = {
                        prefs.updateDuration = it
                        viewModel.updateCheckerDuration(it)
                    }
                )

                SettingsItemChoice(
                    label = stringResource(R.string.settings_mirror),
                    pref = prefs.mirror,
                    labelFactory = {
                        it.baseUrl.toUri().authority ?: it.baseUrl
                    },
                    onPrefChange = {
                        prefs.mirror = it
                    }
                )

                SettingsItemChoice(
                    label = stringResource(R.string.install_method),
                    pref = prefs.installMethod,
                    labelFactory = {
                        ctx.getString(it.labelRes)
                    },
                    onPrefChange = viewModel::setInstallMethod,
                )

                SettingsSwitch(
                    label = stringResource(R.string.settings_auto_clear_cache),
                    secondaryLabel = stringResource(R.string.settings_auto_clear_cache_description),
                    pref = prefs.autoClearCache,
                    onPrefChange = {
                        prefs.autoClearCache = it
                    }
                )

                SettingsHeader(stringResource(R.string.settings_signing_key))
                Text(
                    text = stringResource(R.string.settings_signing_key_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
                SettingsButton(
                    label = stringResource(R.string.action_copy_signing_key),
                    onClick = {
                        ctx.copyText(viewModel.exportSigningKey())
                        ctx.showToast(R.string.msg_signing_key_copied)
                    }
                )
                SettingsButton(
                    label = stringResource(R.string.action_import_signing_key),
                    onClick = {
                        signingKeyText = ""
                        signingKeyError = false
                        showImportDialog = true
                    }
                )

                SettingsButton(
                    label = stringResource(R.string.action_clear_cache),
                    onClick = {
                        viewModel.clearCache()
                    }
                )
            }
        }

        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text(stringResource(R.string.title_import_signing_key)) },
                text = {
                    Column {
                        Text(stringResource(R.string.msg_paste_signing_key))
                        TextButton(
                            onClick = {
                                val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipText = clipboard.primaryClip
                                    ?.takeIf { it.itemCount > 0 }
                                    ?.getItemAt(0)
                                    ?.coerceToText(ctx)
                                    ?.toString()
                                if (clipText.isNullOrBlank()) {
                                    signingKeyError = true
                                } else {
                                    signingKeyText = clipText
                                    signingKeyError = false
                                }
                            }
                        ) {
                            Text(stringResource(R.string.action_paste))
                        }
                        OutlinedTextField(
                            value = signingKeyText,
                            onValueChange = {
                                signingKeyText = it
                                signingKeyError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.label_signing_key)) },
                            isError = signingKeyError,
                            minLines = 4,
                            maxLines = 8,
                            supportingText = if (signingKeyError) {
                                { Text(stringResource(R.string.msg_invalid_signing_key)) }
                            } else null
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = signingKeyText.isNotBlank(),
                        onClick = {
                            if (viewModel.validateSigningKey(signingKeyText)) {
                                signingKeyError = false
                                showImportDialog = false
                                showReplaceWarning = true
                            } else {
                                signingKeyError = true
                            }
                        }
                    ) {
                        Text(stringResource(R.string.action_continue))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }

        if (showReplaceWarning) {
            AlertDialog(
                onDismissRequest = {
                    showReplaceWarning = false
                    showImportDialog = true
                },
                title = { Text(stringResource(R.string.title_warning)) },
                text = { Text(stringResource(R.string.msg_replace_signing_key_warning)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showReplaceWarning = false
                            if (viewModel.importSigningKey(signingKeyText)) {
                                signingKeyText = ""
                                ctx.showToast(R.string.msg_signing_key_imported)
                            } else {
                                signingKeyError = true
                                showImportDialog = true
                            }
                        }
                    ) {
                        Text(stringResource(R.string.action_replace_signing_key))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showReplaceWarning = false
                            showImportDialog = true
                        }
                    ) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
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
                Text(stringResource(R.string.settings_advanced))
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
