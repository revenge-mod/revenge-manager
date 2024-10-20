package app.revenge.manager.ui.widgets.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import app.revenge.manager.network.dto.Commit
import kotlinx.datetime.toJavaInstant
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun Commit(
    commit: Commit
) {
    val uriHandler = LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(commit.url) }
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = commit.author?.avatar ?: "https://github.com/ghost.png",
                    contentDescription = commit.author?.username ?: "ghost",
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = commit.author?.username ?: "ghost",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Text(
                "•",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = commit.sha.substring(0, 7),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = SimpleDateFormat
                    .getDateInstance(SimpleDateFormat.SHORT)
                    .format(Date.from(commit.info.committer.date.toJavaInstant())),
                style = MaterialTheme.typography.labelMedium,
                color = LocalContentColor.current.copy(alpha = 0.5f),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = commit.info.message.split("\n").first(),
            style = MaterialTheme.typography.labelLarge
        )
    }
}