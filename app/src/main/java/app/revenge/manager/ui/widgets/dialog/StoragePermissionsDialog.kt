package app.revenge.manager.ui.widgets.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.google.accompanist.permissions.*
import app.revenge.manager.BuildConfig
import app.revenge.manager.R

@Composable
fun StoragePermissionsDialog() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ManageStorageDialog()
    } else {
        ExternalStorageDialog()
    }
}

@Composable
@SuppressLint("NewApi")
private fun ManageStorageDialog() {
    var manageStorageGranted by remember { mutableStateOf(Environment.isExternalStorageManager()) }

    if (!manageStorageGranted) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                val launcher =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        if (Environment.isExternalStorageManager()) {
                            manageStorageGranted = true
                        }
                    }

                Button(
                    onClick = {
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            .setData("package:${BuildConfig.APPLICATION_ID}".toUri())
                            .let { launcher.launch(it) }
                    }
                ) {
                    Text(stringResource(R.string.action_open_settings))
                }
            },
            title = { Text(stringResource(R.string.title_permission_grant)) },
            text = { Text(stringResource(R.string.msg_permission_grant)) },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ExternalStorageDialog() {
    val writeStorageState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    if (!writeStorageState.status.isGranted) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = writeStorageState::launchPermissionRequest) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            title = { Text(stringResource(R.string.title_permission_grant)) },
            text = { Text(stringResource(R.string.msg_permission_grant)) },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }
}
