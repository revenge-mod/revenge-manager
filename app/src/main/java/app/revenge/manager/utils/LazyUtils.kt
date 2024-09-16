package app.revenge.manager.utils

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType

inline fun <T : Any> LazyListScope.itemsIndexed(
    items: LazyPagingItems<T>,
    noinline key: ((i: Int, item: T) -> Any)? = null,
    crossinline itemContent: @Composable (i: Int, item: T) -> Unit
) {
    items(
        count = items.itemCount,
        key = { i -> key?.invoke(i, items[i]!!) ?: Unit },
        contentType = items.itemContentType()
    ) {
        itemContent(it, items[it]!!)
    }
}