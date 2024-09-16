package app.revenge.manager.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
inline fun <reified E : Enum<E>> SettingsItemChoice(
    label: String,
    title: String = label,
    disabled: Boolean = false,
    pref: E,
    excludedOptions: List<E> = emptyList(),
    crossinline labelFactory: (E) -> String = { it.toString() },
    crossinline onPrefChange: (E) -> Unit,
) {
    val ctx = LocalContext.current
    val choiceLabel = labelFactory(pref)
    var opened = remember {
        mutableStateOf(false)
    }

    SettingsItem(
        modifier = Modifier.clickable(enabled = !disabled) { opened.value = true },
        text = { Text(text = label) },
    ) {
        SettingsChoiceDialog(
            visible = opened.value,
            title = { Text(title) },
            default = pref,
            labelFactory = labelFactory,
            excludedOptions = excludedOptions,
            onRequestClose = {
                opened.value = false
            },
            onChoice = {
                opened.value = false
                onPrefChange(it)
            }
        )
        FilledTonalButton(onClick = { opened.value = true }, enabled = !disabled) {
            Text(choiceLabel)
        }
    }
}
