package app.revenge.manager.ui.screen.libraries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import app.revenge.manager.R
import app.revenge.manager.ui.components.ThinDivider
import app.revenge.manager.ui.viewmodel.libraries.LibrariesViewModel
import app.revenge.manager.ui.widgets.libraries.LibraryItem

class LibrariesScreen: Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() {
        val viewModel: LibrariesViewModel = koinScreenModel()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            topBar = { TitleBar(scrollBehavior) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { pv ->
            LazyColumn(
                modifier = Modifier.padding(pv)
            ) {
                itemsIndexed(viewModel.libraries.libraries) { i, library ->
                    Column {
                        LibraryItem(
                            library = library
                        )
                        if(i != viewModel.libraries.libraries.lastIndex) {
                            ThinDivider()
                        }
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun TitleBar(
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        val navigator = LocalNavigator.currentOrThrow

        LargeTopAppBar(
            title = {
                Text(stringResource(R.string.title_libraries))
            },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

}