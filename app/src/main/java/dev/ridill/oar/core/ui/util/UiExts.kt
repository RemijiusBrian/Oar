package dev.ridill.oar.core.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.paging.compose.LazyPagingItems
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.ui.theme.ContentAlpha

fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean = this.itemCount == 0
fun <T : Any> LazyPagingItems<T>.isNotEmpty(): Boolean = !this.isEmpty()

fun Modifier.mergedContentDescription(
    contentDescription: String?
): Modifier = this
    .semantics(mergeDescendants = true) {}
    .clearAndSetSemantics {
        contentDescription?.let {
            this.contentDescription = it
        }
    }

fun Modifier.exclusionGraphicsLayer(excluded: Boolean): Modifier = this
    .graphicsLayer {
        alpha = if (excluded) ContentAlpha.EXCLUDED
        else Float.One
    }