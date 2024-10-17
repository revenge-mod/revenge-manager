package app.revenge.manager.ui.widgets.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.revenge.manager.R

@Composable
fun BackWarningDialog(
    onExitClick: () -> Unit,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            FilledTonalButton(
                onClick = onClose,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(stringResource(R.string.action_dismiss_nevermind))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onExitClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.action_confirm_exit))
            }
        },
        title = {
            Text(stringResource(R.string.title_warning))
        },
        text = {
            Text(stringResource(R.string.msg_back_warning))
        },
        icon = {
            Icon(Icons.Filled.Warning, contentDescription = null)
        },
        containerColor = MaterialTheme.colorScheme.errorContainer,
        iconContentColor = MaterialTheme.colorScheme.onErrorContainer,
        titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
        textContentColor = MaterialTheme.colorScheme.onErrorContainer
    )
}