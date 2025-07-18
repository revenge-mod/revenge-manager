package app.revenge.manager.ui.widgets.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import app.revenge.manager.R
import app.revenge.manager.network.dto.Commit
import app.revenge.manager.utils.itemsIndexed

@Composable
fun CommitList(
    commits: LazyPagingItems<Commit>
) {
    val loading =
        commits.loadState.append is LoadState.Loading || commits.loadState.refresh is LoadState.Loading
    val failed =
        commits.loadState.append is LoadState.Error || commits.loadState.refresh is LoadState.Error

    LazyColumn {
        itemsIndexed(
            items = commits,
            key = { _, commit -> commit.sha }
        ) { i, commit ->
            Column {
                Commit(commit = commit)
                if (i < commits.itemSnapshotList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }

        if (loading) {
            item {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        if (failed) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.msg_load_fail),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )

                    Button(onClick = { commits.retry() }) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }
        }
    }
}