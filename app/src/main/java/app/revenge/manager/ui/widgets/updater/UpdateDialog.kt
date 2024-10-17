package app.revenge.manager.ui.widgets.updater

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import app.revenge.manager.R
import app.revenge.manager.network.dto.Release

@Composable
fun UpdateDialog(
    release: Release,
    isUpdating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            FilledTonalButton(
                onClick = onConfirm,
                enabled = !isUpdating
            ) {
                Box {
                    Text(
                        text = stringResource(R.string.action_start_update),
                        color = if (isUpdating) Color.Transparent else LocalContentColor.current
                    )
                    if (isUpdating) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

            }
        },
        title = {
            Text(stringResource(R.string.title_update))
        },
        text = {
            Text(
                text = stringResource(R.string.update_description, release.versionName),
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.SystemUpdate,
                contentDescription = null
            )
        }
    )
}