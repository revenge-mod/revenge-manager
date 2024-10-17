package app.revenge.manager.ui.widgets.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revenge.manager.R
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.domain.manager.Theme
import app.revenge.manager.utils.contentDescription
import app.revenge.manager.utils.thenIf
import kotlinx.coroutines.launch

@Composable
@SuppressLint("NewApi") // Dynamic color option shouldn't ever be enabled on unsupported apis anyways
@OptIn(ExperimentalFoundationApi::class)
fun ThemePicker(
    prefs: PreferenceManager
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(prefs.theme.ordinal) {
        Theme.entries.size
    }
    val scope = rememberCoroutineScope()

    val lightScheme = remember(prefs.monet) { if(prefs.monet) dynamicLightColorScheme(context) else lightColorScheme() }
    val darkScheme = remember(prefs.monet) { if(prefs.monet) dynamicDarkColorScheme(context) else darkColorScheme() }
    val systemTheme = if(isSystemInDarkTheme()) darkScheme else lightScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(16.dp)
            ) { page ->
                val (colors, theme) = when (page) {
                    0 -> systemTheme to Theme.SYSTEM
                    1 -> lightScheme to Theme.LIGHT
                    2 -> darkScheme to Theme.DARK
                    else -> systemTheme to Theme.SYSTEM
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .thenIf(prefs.theme == theme) {
                                background(MaterialTheme.colorScheme.tertiaryContainer)
                            }
                            .clickable { prefs.theme = theme }
                            .padding(16.dp)
                    ) {
                        ThemePreview(
                            colorScheme = colors,
                            modifier = Modifier.contentDescription(theme.labelRes, merge = true)
                        )

                        Text(
                            text = stringResource(theme.labelRes),
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (pagerState.currentPage > 0) { // Not first page
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NavigateBefore,
                        contentDescription = stringResource(R.string.action_previous_theme),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            if (pagerState.currentPage < 2) { // Not last page
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NavigateNext,
                        contentDescription = stringResource(R.string.action_next_theme),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .selectableGroup()
        ) {
            ThemePickerOption(
                theme = Theme.SYSTEM,
                colors = systemTheme,
                page = 0,
                pagerState = pagerState,
                prefs = prefs
            )

            ThemePickerOption(
                theme = Theme.LIGHT,
                colors = lightScheme,
                page = 1,
                pagerState = pagerState,
                prefs = prefs
            )

            ThemePickerOption(
                theme = Theme.DARK,
                colors = darkScheme,
                page = 2,
                pagerState = pagerState,
                prefs = prefs
            )
        }
    }
}