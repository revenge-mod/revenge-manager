package app.revenge.manager.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

fun Modifier.contentDescription(description: String, merge: Boolean = false): Modifier = semantics(mergeDescendants = merge) {
    contentDescription = description
}

fun Modifier.contentDescription(res: Int, vararg param: Any, merge: Boolean = false): Modifier
    = composed { contentDescription(stringResource(res, *param), merge) }

inline fun Modifier.thenIf(predicate: Boolean, block: Modifier.() -> Modifier): Modifier =
    if (predicate) then(Modifier.Companion.block()) else this