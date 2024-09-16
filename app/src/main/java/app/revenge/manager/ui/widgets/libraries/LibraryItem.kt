package app.revenge.manager.ui.widgets.libraries

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.author
import app.revenge.manager.R
import app.revenge.manager.ui.components.Label
import app.revenge.manager.utils.contentDescription

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
fun LibraryItem(
    library: Library
) {
    val linkHandler = LocalUriHandler.current
    var licenseSheetOpened by remember {
        mutableStateOf(false)
    }

    if (licenseSheetOpened && library.licenses.firstOrNull()?.licenseContent != null) {
        LicenseBottomSheet(
            license = library.licenses.first(),
            libraryName = library.name,
            onDismiss = {
                licenseSheetOpened = false
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .combinedClickable(
                onClick = { licenseSheetOpened = true },
                onLongClick = { library.website?.let { linkHandler.openUri(it) } }
            )
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = library.name,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        if (!library.description.isNullOrBlank()) {
            Text(
                text = library.description!!,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 15.sp,
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            val labelColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

            library.artifactVersion?.let {
                Label(
                    text = "v$it",
                    icon = Icons.Outlined.Info,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    borderColor = labelColor,
                    fillColor = labelColor
                )
            }

            if (library.author.isNotBlank()) {
                Label(
                    text = library.author,
                    icon = Icons.Outlined.Person,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    borderColor = labelColor,
                    fillColor = labelColor,
                    modifier = Modifier.contentDescription(
                        R.string.cd_library_author,
                        library.author,
                        merge = true
                    )
                )
            }

            library.licenses.forEach { license ->
                Label(
                    text = license.name,
                    icon = Icons.Outlined.Balance,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    borderColor = labelColor,
                    fillColor = labelColor,
                    modifier = Modifier.contentDescription(
                        R.string.cd_library_license,
                        license.name,
                        merge = true
                    )
                )
            }
        }
    }
}