package app.revenge.manager.ui.widgets.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.revenge.manager.R

@Composable
fun DownloadFailedDialog(
    onTryAgainClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(stringResource(R.string.msg_try_again))
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