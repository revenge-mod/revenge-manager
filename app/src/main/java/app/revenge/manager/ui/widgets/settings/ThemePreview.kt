package app.revenge.manager.ui.widgets.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.revenge.manager.BuildConfig
import app.revenge.manager.R
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.utils.DiscordVersion
import org.koin.androidx.compose.get

@Composable
fun ThemePreview(
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    val prefs: PreferenceManager = koinInject()
    val light = colorScheme.background.luminance() > 0.5f
    val layerModifier = Modifier.height(300.dp)
    val iconColor = remember(prefs.patchIcon, prefs.channel) {
        when {
            prefs.patchIcon -> Color(BuildConfig.MODDED_APP_ICON)
            prefs.channel == DiscordVersion.Type.ALPHA -> Color(BuildConfig.MODDED_APP_ICON_ALPHA)
            else -> Color(BuildConfig.MODDED_APP_ICON_OTHER)
        }
    }

    Box(
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ts_bg),
            contentDescription = null,
            tint = colorScheme.background,
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_surface_l2),
            contentDescription = null,
            tint = colorScheme.surfaceColorAtElevation(2.dp),
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_surface_l1),
            contentDescription = null,
            tint = colorScheme.surfaceColorAtElevation(1.dp),
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_outline),
            contentDescription = null,
            tint = colorScheme.outline.copy(alpha = 0.3f),
            modifier = layerModifier
        )

        Image(
            painter = painterResource(R.drawable.ts_avatars),
            contentDescription = null,
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_primary),
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_content_50),
            contentDescription = null,
            tint = colorScheme.contentColorFor(colorScheme.background).copy(alpha = 0.5f),
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_content),
            contentDescription = null,
            tint = colorScheme.contentColorFor(colorScheme.background),
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_icon),
            contentDescription = null,
            tint = iconColor,
            modifier = layerModifier
        )

        Icon(
            painter = painterResource(R.drawable.ts_status),
            contentDescription = null,
            tint = if(light) Color(0xFF686568) else Color.White,
            modifier = layerModifier
        )
    }
}