package dev.ridill.oar.core.ui.components

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.ui.theme.OarTheme
import dev.ridill.oar.core.ui.theme.spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwipeToAction(
    state: SwipeToActionState,
    modifier: Modifier = Modifier,
    thumb: @Composable BoxScope.() -> Unit = {
        Thumb(
            modifier = Modifier
                .matchParentSize()
                .layoutId(ThumbId)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {},
                        onDrag = { _, offset -> state.onDrag(offset.x) },
                        onDragEnd = { state.onDragEnd() },
                        onDragCancel = { state.onDragCancel() }
                    )
                }
        )
    },
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val isThumbBeingInteracted by state.isDragInProgress
    val thumbWidthMultiplier by animateFloatAsState(
        targetValue = if (isThumbBeingInteracted) 2f else 1f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val decoratedThumb = @Composable {
        Box(
            propagateMinConstraints = true,
            modifier = Modifier
                .layoutId(ThumbId)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {},
                        onDrag = { _, offset -> state.onDrag(offset.x) },
                        onDragEnd = { state.onDragEnd() },
                        onDragCancel = { state.onDragCancel() }
                    )
                },
            content = thumb
        )
    }
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .layoutId(TrackId)
            ) {
                Track(
                    swipeProgress = { state.swipeProgress },
                    color = trackColor,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            decoratedThumb()
        },
        modifier = modifier
    ) { measurables, constraints ->
        val trackPlaceable = measurables
            .fastFirst { it.layoutId == TrackId }
            .measure(constraints)

        val height = trackPlaceable.measuredHeight
        val width = trackPlaceable.measuredWidth

        val thumbWidth = (height * thumbWidthMultiplier).roundToInt()
        val thumbConstraints = constraints.copy(
            minHeight = height,
            maxHeight = height,
            minWidth = thumbWidth,
            maxWidth = thumbWidth
        )
        val thumbPlaceable = measurables
            .fastFirst { it.layoutId == ThumbId }
            .measure(thumbConstraints)

        state.updateOffsetXLimit((width - thumbWidth).toFloat())

        layout(width = width, height = height) {
            trackPlaceable.place(0, 0)

            val thumbX = state.offsetX.roundToInt()
            thumbPlaceable.placeRelative(
                x = thumbX,
                y = (height - thumbPlaceable.measuredHeight) / 2
            )
        }
    }
}

private const val TrackId = "Track"
private const val ThumbId = "Thumb"

@Composable
private fun Track(
    swipeProgress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .heightIn(min = SliderHeight)
            .drawWithCache {
                val width = size.width
                onDrawWithContent {
                    drawRoundRect(
                        color = color,
                        cornerRadius = CornerRadius(size.minDimension / 2)
                    )
                    clipRect(left = width * swipeProgress()) {
                        this@onDrawWithContent.drawContent()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        LabelLargeText(
            text = stringResource(R.string.swipe_to_complete_cycle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .graphicsLayer {
                    alpha = Float.One - swipeProgress()
                }
        )
    }
}

@Composable
private fun Thumb(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .sizeIn(minWidth = SliderHeight, minHeight = SliderHeight)
            .minimumInteractiveComponentSize()
            .drawWithCache {
                onDrawBehind {
                    drawRoundRect(
                        color = color,
                        cornerRadius = CornerRadius(size.minDimension / 2)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_double_arrow_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private val SliderHeight = 48.dp

@Preview
@Composable
private fun PreviewSwipeToAction() {
    OarTheme {
        Surface {
            SwipeToAction(
                state = rememberSwipeToActionState(onSwiped = {}),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.small)
            )
        }
    }
}

data class SwipeToActionState(
    private val coroutineScope: CoroutineScope,
    private val onSwiped: () -> Unit,
    private val settleAnimationSpec: FiniteAnimationSpec<Float>
) {
    var offsetXLimit: Float = Float.Zero
        private set

    fun updateOffsetXLimit(limit: Float) {
        offsetXLimit = limit
    }

    val isDragInProgress = derivedStateOf {
        offsetX > Float.One
    }

    var offsetX: Float
        get() = _offsetYState.floatValue
        private set(value) {
            _offsetYState.floatValue = value.coerceIn(
                minimumValue = Float.Zero,
                maximumValue = offsetXLimit
            )
        }
    private val _offsetYState = mutableFloatStateOf(Float.Zero)

    private var settleJob: Job? = null
    fun settleOffsetXToZero() {
        settleJob?.cancel()
        settleJob = coroutineScope.launch {
            AnimationState(offsetX).animateTo(
                targetValue = Float.Zero,
                animationSpec = settleAnimationSpec
            ) {
                offsetX = this.value
            }
        }
    }

    fun onDrag(offsetX: Float) {
        this.offsetX = this.offsetX + offsetX
    }

    fun onDragEnd() {
        if (offsetX >= (offsetXLimit * 0.95f)) {
            onSwiped()
        }
        settleOffsetXToZero()
    }

    fun onDragCancel() {
        settleOffsetXToZero()
    }

    val swipeProgress: Float
        get() = if (offsetXLimit != 0f) {
            offsetX / offsetXLimit
        } else {
            Float.Zero
        }.absoluteValue
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun rememberSwipeToActionState(
    onSwiped: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    settleAnimationSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.defaultSpatialSpec()
): SwipeToActionState = remember(onSwiped, coroutineScope) {
    SwipeToActionState(
        coroutineScope = coroutineScope,
        onSwiped = onSwiped,
        settleAnimationSpec = settleAnimationSpec
    )
}