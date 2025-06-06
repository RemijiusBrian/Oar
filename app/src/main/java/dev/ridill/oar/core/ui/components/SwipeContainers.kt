package dev.ridill.oar.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.ui.theme.ContentAlpha
import dev.ridill.oar.core.ui.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun SwipeToDismissContainer(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    animationDuration: Int = DEFAULT_ANIM_DURATION,
    gesturesEnabled: Boolean = true,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    backgroundContent: @Composable RowScope.(SwipeToDismissBoxState) -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.Settled -> true
                SwipeToDismissBoxValue.StartToEnd -> {
                    if (enableDismissFromStartToEnd) {
                        isRemoved = true
                        true
                    } else {
                        false
                    }
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    if (enableDismissFromEndToStart) {
                        isRemoved = true
                        true
                    } else {
                        false
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        state.snapTo(SwipeToDismissBoxValue.Settled)
    }

    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut(),
        modifier = modifier
    ) {
        SwipeToDismissBox(
            state = state,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            gesturesEnabled = gesturesEnabled,
            backgroundContent = { backgroundContent(state) },
            content = content
        )
    }
}

private const val DEFAULT_ANIM_DURATION = 500

@Composable
fun DismissBackground(
    swipeDismissState: SwipeToDismissBoxState,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.errorContainer,
    contentColor: Color = contentColorFor(containerColor),
    contentInsets: WindowInsets = SwipeContainerDefaults.contentInsets
) {

    BottomSheetDefaults.windowInsets
    val color = if (
        (enableDismissFromStartToEnd && swipeDismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) ||
        (enableDismissFromEndToStart && swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
    ) containerColor
    else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawRect(color) }
            .padding(contentInsets.asPaddingValues())
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (enableDismissFromStartToEnd) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor
            )
        }
        SpacerMedium()
        if (enableDismissFromEndToStart) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor
            )
        }
    }
}

@Composable
fun SwipeActionsContainer(
    isRevealed: Boolean,
    onRevealedChange: (Boolean) -> Unit,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    actionsSide: SwipeActionsSide = SwipeActionsSide.End,
    gesturesEnabled: Boolean = true,
    animationSpec: AnimationSpec<Float> = SwipeContainerDefaults.animationSpec,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = ContentAlpha.PERCENT_50),
    positionalThreshold: Float = SwipeContainerDefaults.POSITIONAL_THRESHOLD_FRACTION,
    previewFraction: Float = SwipeContainerDefaults.PREVIEW_FRACTION,
    actionContentInsets: WindowInsets = SwipeContainerDefaults.contentInsets,
    animatePreview: Boolean = false,
    content: @Composable () -> Unit
) {
    var actionsRowWidth by remember { mutableFloatStateOf(Float.Zero) }
    val scope = rememberCoroutineScope()
    val offset = remember {
        Animatable(
            initialValue = Float.Zero,
            typeConverter = Float.VectorConverter,
            label = "SwipeActionsOffset"
        )
    }

    // Preview Animation
    var runPreviewAnimation by remember { mutableStateOf(animatePreview) }
    LaunchedEffect(gesturesEnabled, actionsSide, actionsRowWidth, runPreviewAnimation) {
        val previewOffset = actionsRowWidth * previewFraction * actionsSide.directionMultiplier
        delay(PreviewAnimInitialDelay)
        while (gesturesEnabled && runPreviewAnimation) {
            offset.animateTo(
                targetValue = previewOffset,
                animationSpec = tween(durationMillis = 1_000)
            )
            offset.animateTo(
                targetValue = Float.Zero,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            delay(PreviewAnimIterationDelay)
        }
    }

    LaunchedEffect(isRevealed, actionsRowWidth) {
        if (isRevealed) {
            offset.animateTo(
                targetValue = actionsRowWidth * actionsSide.directionMultiplier,
                animationSpec = animationSpec
            )
        } else {
            offset.animateTo(
                targetValue = Float.Zero,
                animationSpec = animationSpec
            )
        }
    }

    val actionsInsets = remember(actionsSide) {
        when (actionsSide) {
            SwipeActionsSide.Start -> actionContentInsets.only(WindowInsetsSides.Start)
            SwipeActionsSide.End -> actionContentInsets.only(WindowInsetsSides.End)
        }
    }

    val actionsAlignment = remember(actionsSide) {
        when (actionsSide) {
            SwipeActionsSide.Start -> Alignment.CenterStart
            SwipeActionsSide.End -> Alignment.CenterEnd
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(containerColor)
    ) {
        Row(
            modifier = Modifier
                .align(actionsAlignment)
                .onSizeChanged { measuresSize ->
                    actionsRowWidth = measuresSize.width.toFloat()
                }
                .padding(actionsInsets.asPaddingValues())
                .then(
                    when (actionsSide) {
                        SwipeActionsSide.Start -> Modifier.padding(end = MaterialTheme.spacing.small)
                        SwipeActionsSide.End -> Modifier.padding(start = MaterialTheme.spacing.small)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )

        Surface(
            content = content,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = offset.value.roundToInt(),
                        y = 0
                    )
                }
                .pointerInput(actionsRowWidth, gesturesEnabled) {
                    if (gesturesEnabled)
                        detectHorizontalDragGestures(
                            onDragStart = { runPreviewAnimation = false },
                            onHorizontalDrag = { _, dragAmount ->
                                scope.launch {
                                    val newOffset = (offset.value + dragAmount)
                                    val coercedOffset = when (actionsSide) {
                                        SwipeActionsSide.Start -> newOffset.coerceIn(
                                            Float.Zero,
                                            actionsRowWidth
                                        )

                                        SwipeActionsSide.End -> newOffset.coerceIn(
                                            -actionsRowWidth,
                                            Float.Zero
                                        )
                                    }
                                    offset.snapTo(coercedOffset)
                                }
                            },
                            onDragEnd = {
                                val threshold = actionsRowWidth * positionalThreshold
                                when {
                                    offset.value.absoluteValue >= threshold -> {
                                        scope.launch {
                                            offset.animateTo(
                                                targetValue = actionsRowWidth * actionsSide.directionMultiplier,
                                                animationSpec = animationSpec
                                            )
                                            onRevealedChange(true)
                                        }
                                    }

                                    else -> {
                                        scope.launch {
                                            offset.animateTo(
                                                targetValue = Float.Zero,
                                                animationSpec = animationSpec
                                            )
                                            onRevealedChange(false)
                                        }
                                    }
                                }
                            }
                        )
                }
        )
    }
}

private val PreviewAnimInitialDelay = 1.seconds
private val PreviewAnimIterationDelay = 3.seconds

enum class SwipeActionsSide(
    val directionMultiplier: Int
) {
    Start(1),
    End(-1)
}

object SwipeContainerDefaults {

    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceVariant

    val contentInsets: WindowInsets
        @Composable get() = WindowInsets.safeGestures.only(WindowInsetsSides.Horizontal)

    val animationSpec: AnimationSpec<Float>
        get() = spring()

    const val POSITIONAL_THRESHOLD_FRACTION = 0.5f

    const val PREVIEW_FRACTION = 0.48f
}