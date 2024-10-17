package app.revenge.manager.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTextField(
    label: String,
    disabled: Boolean = false,
    pref: String,
    error: Boolean = false,
    supportingText: String? = null,
    onPrefChange: (String) -> Unit,
) {
    Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = pref,
            onValueChange = onPrefChange,
            enabled = !disabled,
            label = { Text(label) },
            isError = error,
            singleLine = true,
            supportingText = if (supportingText != null) { -> Text(supportingText) } else null
        )
    }
}