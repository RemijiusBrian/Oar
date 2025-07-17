package dev.ridill.oar.core.ui.components.scrollableLayout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.util.fastFirst
import dev.ridill.oar.core.domain.util.One
import kotlin.math.roundToInt

@Composable
fun ScrollableHeaderLayout(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: ScrollableLayoutBehaviour = ScrollableLayoutDefaults.exitUntilCollapsedScrollBehavior(),
    windowInsets: WindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
    body: @Composable () -> Unit
) {
    val windowInsetsPadding = windowInsets.asPaddingValues()
    val decoratedHeader = @Composable {
        Box(
            modifier = Modifier
                .layoutId(HeaderId)
                .graphicsLayer {
                    alpha = Float.One - scrollBehavior.state.collapsedFraction
                }
        ) {
            header()
        }
    }

    val decoratedBody = @Composable {
        Box(
            modifier = Modifier
                .layoutId(ContentId)
        ) {
            body()
        }
    }
    Layout(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        content = {
            decoratedHeader()
            decoratedBody()
        },
    ) { measureables, constraints ->
        val windowInsetsTopPaddingPx = windowInsetsPadding.calculateTopPadding().roundToPx()
        val relaxedConstraints = constraints.copy(minHeight = 0)

        val headerPlaceable = measureables
            .fastFirst { it.layoutId == HeaderId }
            .measure(relaxedConstraints)
        scrollBehavior.state.updateOffsetYLimit(headerPlaceable.measuredHeight.toFloat())

        val bodyPlaceable = measureables
            .fastFirst { it.layoutId == ContentId }
            .measure(relaxedConstraints.copy(minHeight = headerPlaceable.measuredHeight))

        layout(
            width = maxOf(headerPlaceable.measuredWidth, bodyPlaceable.measuredWidth),
            height = bodyPlaceable.measuredHeight
        ) {
            val headerOffsetY =
                (scrollBehavior.state.offsetY / scrollBehavior.state.parallaxFactor).roundToInt()
            headerPlaceable.placeRelative(x = 0, y = headerOffsetY)

            val paddingPerProgress =
                windowInsetsTopPaddingPx * scrollBehavior.state.collapsedFraction
            val bodyOffsetY = (scrollBehavior.state.offsetYLimit +
                    scrollBehavior.state.offsetY +
                    paddingPerProgress).roundToInt()
            bodyPlaceable.placeRelative(
                x = 0,
                y = bodyOffsetY
            )
        }
    }
}

private const val HeaderId = "Header"
private const val ContentId = "Content"