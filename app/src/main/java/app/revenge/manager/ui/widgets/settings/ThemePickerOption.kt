package app.revenge.manager.ui.widgets.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.domain.manager.Theme
import app.revenge.manager.utils.contentDescription
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemePickerOption(
    theme: Theme,
    colors: ColorScheme,
    page: Int,
    pagerState: PagerState,
    prefs: PreferenceManager
) {
    val scope = rememberCoroutineScope()
    val isSelected = pagerState.currentPage == page

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .animateContentSize(animationSpec = tween())
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.background)
                .size(50.dp)
                .contentDescription(theme.labelRes, merge = true)
                .selectable(prefs.theme == theme) {
                    prefs.theme = theme
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
                .run {
                    if (prefs.theme == theme)
                        border(BorderStroke(4.dp, MaterialTheme.colorScheme.tertiary), CircleShape)
                    else
                        border(BorderStroke(2.dp, colors.inverseSurface), CircleShape)
                }
        ) {
            if (theme == Theme.SYSTEM) {
                Icon(
                    imageVector = Icons.Outlined.Sync,
                    contentDescription = null, // Covered by parent component
                    tint = colors.onSurface
                )
            }
        }
    }
}