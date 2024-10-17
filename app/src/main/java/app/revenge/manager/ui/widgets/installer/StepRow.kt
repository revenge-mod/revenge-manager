package app.revenge.manager.ui.widgets.installer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revenge.manager.R
import app.revenge.manager.installer.step.StepStatus

/**
 * Displays a steps name, status, and progress
 *
 * @param name The name of the step
 * @param status The steps current status
 * @param progress Represents the download progress, as a decimal
 * @param cached Whether the file this step downloads was already cached
 * @param duration How long the step took to run, in seconds
 * @param modifier [Modifier] for this StepRow
 */
@Composable
fun StepRow(
    name: String,
    status: StepStatus,
    progress: Float?,
    cached: Boolean,
    duration: Float,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        @Suppress("LocalVariableName")
        val _progress by animateFloatAsState(progress ?: 0f, label = "Progress") // Smoothly animate the progress indicator

        StepIcon(status, size = 18.dp, progress = if(progress == null) null else _progress)

        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, true),
        )

        if (status != StepStatus.ONGOING && status != StepStatus.QUEUED) { // Only display for completed steps
            if (cached) {
                Text(
                    text = stringResource(R.string.installer_cached),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }

            Text(
                text = "%.2fs".format(duration), // Displays the duration rounded to the hundredths place. ex. 10.13s
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
    }
}