package app.revenge.manager.ui.widgets.installer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revenge.manager.R
import app.revenge.manager.installer.step.Step
import app.revenge.manager.installer.step.StepStatus
import app.revenge.manager.installer.step.download.base.DownloadStep
import app.revenge.manager.utils.thenIf

/**
 * Collapsable card containing a group of steps
 *
 * @param name The name of this group
 * @param isCurrent Whether this card is expanded
 * @param steps The steps belonging to this group
 * @param onClick Action taken when the header of the group is clicked
 */
@Composable
fun StepGroupCard(
    name: String,
    isCurrent: Boolean,
    steps: List<Step>,
    onClick: () -> Unit
) {
    val status = when {
        steps.all { it.status == StepStatus.QUEUED } -> StepStatus.QUEUED
        steps.all { it.status == StepStatus.SUCCESSFUL } -> StepStatus.SUCCESSFUL
        steps.any { it.status == StepStatus.ONGOING } -> StepStatus.ONGOING
        else -> StepStatus.UNSUCCESSFUL
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .thenIf(isCurrent) {
                background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            StepIcon(status, 24.dp, progress = null)

            Text(text = name)

            Spacer(modifier = Modifier.weight(1f))

            if (status != StepStatus.ONGOING && status != StepStatus.QUEUED) {
                Text(
                    text = "%.2fs".format(steps.sumOf { it.durationMs } / 1000f), // Displays the duration rounded to the hundredths place. ex. 10.13s
                    style = MaterialTheme.typography.labelMedium
                )
            }

            val (arrow, cd) = when {
                isCurrent -> Icons.Filled.KeyboardArrowUp to R.string.action_collapse
                else -> Icons.Filled.KeyboardArrowDown to R.string.action_expand
            }

            Icon(
                imageVector = arrow,
                contentDescription = stringResource(cd)
            )
        }

        AnimatedVisibility(visible = isCurrent) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.copy(0.6f))
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(start = 4.dp)
            ) {
                steps.forEach { step ->
                    StepRow(
                        name = stringResource(step.nameRes),
                        status = step.status,
                        progress = step.progress,
                        cached = (step as? DownloadStep)?.cached ?: false,
                        duration = step.durationMs / 1000f
                    )
                }
            }
        }
    }
}