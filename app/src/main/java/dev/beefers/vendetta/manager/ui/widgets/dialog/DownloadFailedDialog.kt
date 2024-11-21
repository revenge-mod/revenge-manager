package dev.beefers.vendetta.manager.ui.widgets.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.beefers.vendetta.manager.R
import dev.beefers.vendetta.manager.domain.manager.PreferenceManager
import dev.beefers.vendetta.manager.ui.components.settings.SettingsChoiceDialog
import org.koin.compose.koinInject

@Composable
fun DownloadFailedDialog(
    onTryAgainClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val prefs: PreferenceManager = koinInject()
    var mirrorPickerOpened by remember {
        mutableStateOf(false)
    }

    SettingsChoiceDialog(
        visible = mirrorPickerOpened,
        default = prefs.mirror,
        title = { Text(stringResource(R.string.settings_mirror)) },
        labelFactory = { it.baseUrl.toUri().authority ?: it.baseUrl },
        onRequestClose = { mirrorPickerOpened = false },
        onChoice = {
            prefs.mirror = it
            mirrorPickerOpened = false
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(R.string.msg_change_mirror))
                FilledTonalButton(
                    onClick = { mirrorPickerOpened = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = prefs.mirror.baseUrl.toUri().authority ?: prefs.mirror.baseUrl
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_dismiss_no_thanks))
            }
        },
        confirmButton = {
            Button(onClick = onTryAgainClick) {
                Text(stringResource(R.string.action_try_again))
            }
        },
        title = {
            Text(stringResource(R.string.title_dl_failed))
        }
    )
}