package dev.beefers.vendetta.manager.ui.screen.installer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.beefers.vendetta.manager.R
import dev.beefers.vendetta.manager.domain.manager.PreferenceManager
import dev.beefers.vendetta.manager.installer.util.LogEntry
import dev.beefers.vendetta.manager.ui.viewmodel.installer.LogViewerViewModel
import dev.beefers.vendetta.manager.ui.widgets.installer.LogLine
import dev.beefers.vendetta.manager.utils.DimenUtils
import dev.beefers.vendetta.manager.utils.rememberFileSaveLauncher
import dev.beefers.vendetta.manager.utils.thenIf
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
class LogViewerScreen(
    val logs: List<LogEntry>
) : Screen {

    @Composable
    override fun Content() {
        val prefs: PreferenceManager = koinInject()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val viewModel: LogViewerViewModel = koinScreenModel {
            parametersOf(logs)
        }

        Scaffold(
            topBar = { Toolbar(scrollBehavior, viewModel) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { pv ->
            LazyColumn(
                contentPadding = PaddingValues(bottom = DimenUtils.navBarPadding),
                modifier = Modifier
                    .padding(pv)
                    .thenIf(!prefs.logsLineWrap) {
                        horizontalScroll(rememberScrollState())
                    }
            ) {
                itemsIndexed(viewModel.logs) { i, log ->
                    LogLine(
                        log = log,
                        alternateBackground = i % 2 == 0 && prefs.logsAlternateBackground,
                        wrapText = prefs.logsLineWrap,
                        logPadding = viewModel.maxLogLength,
                        onLongClick = { viewModel.copyLog(log) }
                    )
                }
            }
        }
    }

    @Composable
    private fun Toolbar(
        scrollBehavior: TopAppBarScrollBehavior,
        viewModel: LogViewerViewModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val saveFile = rememberFileSaveLauncher(content = viewModel.logsString)

        TopAppBar(
            title = { Text(stringResource(R.string.title_logs)) },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            },
            actions = {
                var showDropdown by remember {
                    mutableStateOf(false)
                }

                IconButton(onClick = { saveFile.launch("VD-Manager-${System.currentTimeMillis()}.log") }) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = stringResource(R.string.action_save_logs)
                    )
                }

                IconButton(onClick = { viewModel.shareLogs(context) }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.action_share_logs)
                    )
                }

                IconButton(onClick = { showDropdown = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.action_more_options)
                    )
                }

                Dropdown(
                    viewModel,
                    expanded = showDropdown,
                    onDismiss = { showDropdown = false }
                )
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    fun Dropdown(
        viewModel: LogViewerViewModel,
        expanded: Boolean,
        onDismiss: () -> Unit
    ) {
        val prefs: PreferenceManager = koinInject()

        Box {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,
                offset = DpOffset(
                    10.dp, 26.dp
                )
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.settings_logs_line_wrap)) },
                    onClick = { prefs.logsLineWrap = !prefs.logsLineWrap },
                    trailingIcon = {
                        Checkbox(
                            checked = prefs.logsLineWrap,
                            onCheckedChange = { prefs.logsLineWrap = it }
                        )
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.settings_logs_alternate_lines)) },
                    onClick = { prefs.logsAlternateBackground = !prefs.logsAlternateBackground },
                    trailingIcon = {
                        Checkbox(
                            checked = prefs.logsAlternateBackground,
                            onCheckedChange = { prefs.logsAlternateBackground = it }
                        )
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_copy_logs)) },
                    onClick = { viewModel.copyLogs() }
                )
            }
        }
    }

}