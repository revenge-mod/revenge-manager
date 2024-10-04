package app.revenge.manager.ui.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import app.revenge.manager.BuildConfig
import app.revenge.manager.R
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.ui.screen.libraries.LibrariesScreen
import app.revenge.manager.ui.widgets.about.LinkItem
import app.revenge.manager.ui.widgets.about.ListItem
import app.revenge.manager.ui.widgets.about.UserEntry
import app.revenge.manager.utils.Constants
import app.revenge.manager.utils.DimenUtils
import app.revenge.manager.utils.getBitmap
import app.revenge.manager.utils.showToast
import org.koin.androidx.compose.get

class AboutScreen : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val uriHandler = LocalUriHandler.current
        val prefs: PreferenceManager = get()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val ctx = LocalContext.current
        val bitmap = remember {
            ctx.getBitmap(R.drawable.ic_launcher, 60).asImageBitmap()
        }
        var tapCount by remember {
            mutableIntStateOf(0)
        }

        Scaffold(
            topBar = { TitleBar(scrollBehavior) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = DimenUtils.navBarPadding)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 20.dp)
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalContentColor.current.copy(alpha = 0.5f),
                        modifier = Modifier.clickable(
                            enabled = !prefs.isDeveloper
                        ) {
                            tapCount++
                            when (tapCount) {
                                3 -> ctx.showToast(R.string.msg_seven_left)
                                5 -> ctx.showToast(R.string.msg_five_left)
                                8 -> ctx.showToast(R.string.msg_two_left)
                                10 -> {
                                    ctx.showToast(R.string.msg_unlocked)
                                    prefs.isDeveloper = true
                                }
                            }
                        }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinkItem(
                            icon = R.drawable.ic_github,
                            label = R.string.label_github,
                            link = BuildConfig.ORG_LINK
                        )

                        LinkItem(
                            icon = R.drawable.ic_discord,
                            label = R.string.label_discord,
                            link = BuildConfig.INVITE_LINK
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 20.dp)
                ) {
                    UserEntry("Fiery", "Lead dev\niOS", "FieryFlames")
                    UserEntry("Maisy", "Creator\nVendetta", "maisymoe", isLarge = true)
                    UserEntry("Wing", "Lead dev\nManager", "wingio")
                }

                Text(
                    text = stringResource(R.string.label_team),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    ElevatedCard {
                        Constants.TEAM_MEMBERS.forEachIndexed { i, member ->
                            ListItem(
                                text = member.name,
                                subtext = member.role,
                                imageUrl = "https://github.com/${member.username}.png",
                                onClick = {
                                    uriHandler.openUri("https://github.com/${member.username}")
                                }
                            )
                            if (i != Constants.TEAM_MEMBERS.lastIndex) {
                                Divider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.label_special_thanks),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    ElevatedCard {
                        ListItem(
                            text = "rushii",
                            subtext = "Installer, zip library, and a portion of patching",
                            imageUrl = "https://github.com/rushiiMachine.png",
                            onClick = {
                                uriHandler.openUri("https://github.com/rushiiMachine")
                            }
                        )
                        Divider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        ListItem(
                            text = "Xinto",
                            subtext = "for the preference manager",
                            imageUrl = "https://github.com/X1nto.png",
                            onClick = {
                                uriHandler.openUri("https://github.com/X1nto")
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    ElevatedCard {
                        val navigator = LocalNavigator.currentOrThrow

                        // ListItem(
                        //     text = stringResource(R.string.label_translate),
                        //     onClick = { uriHandler.openUri("https://crowdin.com/project/vendetta-manager") }
                        // )
                        Divider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        ListItem(
                            text = stringResource(R.string.title_os_libraries),
                            onClick = { navigator.push(LibrariesScreen()) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun TitleBar(
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        val navigator = LocalNavigator.currentOrThrow

        TopAppBar(
            title = { Text(stringResource(R.string.title_about)) },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        )
    }

}
