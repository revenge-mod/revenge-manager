package app.revenge.manager.ui.widgets.dialog

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import app.revenge.manager.R
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.utils.showToast
import org.koin.compose.koinInject

@Composable
fun BatteryOptimizationDialog() {
    val context = LocalContext.current
    val prefs: PreferenceManager = koinInject()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        checkBatteryOptimizations(context, prefs) {
            showDialog = true
        }
    }

    if (showDialog) {
        val onDismiss = {
            prefs.hasAskedForBatteryOpt = true
            showDialog = false
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.battery_opt_title)) },
            text = { Text(stringResource(R.string.battery_opt_description)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        requestIgnoreBatteryOptimizations(context)
                    }
                ) {
                    Text(stringResource(R.string.battery_opt_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.battery_opt_dismiss))
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }
}

private fun checkBatteryOptimizations(
    context: Context,
    prefs: PreferenceManager,
    showDialog: () -> Unit
) {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (powerManager.isIgnoringBatteryOptimizations(context.packageName) || prefs.hasAskedForBatteryOpt) {
        return
    }
    showDialog()
}

@SuppressLint("BatteryLife")
private fun requestIgnoreBatteryOptimizations(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = "package:${context.packageName}".toUri()
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        context.showToast(R.string.battery_opt_error, short = false)
        Log.e("BatteryOptimization", "Failed to start activity for battery optimization settings", e)
    }
}