package app.revenge.manager.ui.widgets.installer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revenge.manager.R
import app.revenge.manager.installer.util.LogEntry
import app.revenge.manager.utils.thenIf

/**
 * UI for a log entry, displays the level and message and can be expanded with a click to show the timestamp
 *
 * @param log The log to display
 * @param alternateBackground Whether or not to use the alternating background
 * @param wrapText Whether or not the message should have line wrapping
 * @param logPadding Force message to contain this many characters, used to make all lines equal in length
 * @param onLongClick Action to take when long clicking, should just copy the log to clipboard
 * @param modifier [Modifier] for this component
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun LogLine(
    log: LogEntry,
    alternateBackground: Boolean,
    wrapText: Boolean,
    logPadding: Int,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val levelColor = when (log.level) {
        LogEntry.Level.DEBUG -> Color(0xFF10AF6F) // Green
        LogEntry.Level.INFO -> if (MaterialTheme.colorScheme.background.luminance() >= 0.5f) Color.Black else Color.White
        LogEntry.Level.ERROR -> MaterialTheme.colorScheme.error
    }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onLongClickLabel = stringResource(R.string.action_copy_log),
                onLongClick = onLongClick,
                onClickLabel = stringResource(R.string.action_show_timestamp),
                onClick = {
                    expanded = !expanded
                }
            )
            .thenIf(alternateBackground) { // Alternate background on each line
                background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f))
            }
            .thenIf(log.level == LogEntry.Level.ERROR) {
                background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
            }
            .padding(vertical = 4.5.dp, horizontal = 16.dp)
    ) {
        Text(
            text = "[${log.level.name[0]}]  ",
            color = levelColor.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = log.message.padEnd(logPadding),
                softWrap = wrapText,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = LocalContentColor.current.copy(alpha = if (log.level == LogEntry.Level.DEBUG) 0.5f else 0.85f)
            )

            if (expanded) {
                Text(
                    text = log.formatTimestamp(),
                    softWrap = false,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }
        }
    }
}