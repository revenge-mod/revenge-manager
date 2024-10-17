package app.revenge.manager.network.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.revenge.manager.BuildConfig
import app.revenge.manager.domain.repository.RestRepository
import app.revenge.manager.network.dto.Commit

class CommitsPagingSource(
    private val repo: RestRepository
) : PagingSource<Int, Commit>() {

    override fun getRefreshKey(state: PagingState<Int, Commit>): Int? =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Commit> {
        val page = params.key ?: 0

        return when (val response = repo.getCommits(BuildConfig.REPO, page)) {
            is ApiResponse.Success -> LoadResult.Page(
                data = response.data,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (response.data.isNotEmpty()) page + 1 else null
            )

            is ApiResponse.Failure -> LoadResult.Error(response.error)
            is ApiResponse.Error -> LoadResult.Error(response.error)
        }
    }

}