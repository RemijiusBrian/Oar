package dev.ridill.oar.core.ui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.asFloatState
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Velocity
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.domain.util.Zero
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun CollapsibleHeaderLayout(
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: CollapsibleHeaderState = rememberCollapsibleHeaderState(),
    windowInsets: WindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout)
) {
    val windowInsetsPadding = windowInsets.asPaddingValues()
    Layout(
        modifier = modifier
            .nestedScroll(state.nestedScrollConnection),
        content = {
            header()
            body()
        },
    ) { measureables, constraints ->
        val windowInsetsTopPaddingPx = windowInsetsPadding.calculateTopPadding().roundToPx()

        val minHeightZeroConstraints = constraints.copy(
            minHeight = 0
        )

        val headerMeasureable = measureables[0]
        val headerPlaceable = headerMeasureable.measure(minHeightZeroConstraints)

        state.updateHeaderHeight(headerPlaceable.measuredHeight)

        val bodyMeasureable = measureables[1]
        val bodyPlaceable = bodyMeasureable.measure(minHeightZeroConstraints)

        layout(
            width = maxOf(headerPlaceable.measuredWidth, bodyPlaceable.measuredWidth),
            height = maxOf(headerPlaceable.measuredHeight, bodyPlaceable.measuredHeight)
        ) {
            val headerOffsetY = (state.offsetYState.floatValue / state.parallaxFactor).roundToInt()
            headerPlaceable.placeRelative(x = 0, y = headerOffsetY)

            val paddingPerProgress = windowInsetsTopPaddingPx * state.collapseProgress.floatValue
            val bodyOffsetY = (state.headerHeight.intValue +
                    state.offsetYState.floatValue +
                    paddingPerProgress).roundToInt()
            bodyPlaceable.placeRelative(
                x = 0,
                y = bodyOffsetY
            )
        }
    }
}

data class CollapsibleHeaderState(
    val parallaxFactor: Float,
    private val flingAnimationSpec: DecayAnimationSpec<Float>?,
    private val snapAnimationSpec: AnimationSpec<Float>?
) {
    companion object {
        private const val PARALLAX_FACTOR_KEY = "PARALLAX_FACTOR_KEY"
        private const val HEADER_HEIGHT_KEY = "HEADER_HEIGHT_KEY"
        private const val Y_OFFSET_KEY = "Y_OFFSET_KEY"

        fun saver(
            flingAnimationSpec: DecayAnimationSpec<Float>?,
            snapAnimationSpec: AnimationSpec<Float>?
        ) = mapSaver<CollapsibleHeaderState>(
            save = { state ->
                mapOf(
                    PARALLAX_FACTOR_KEY to state.parallaxFactor,
                    Y_OFFSET_KEY to state.offsetYState.floatValue,
                    HEADER_HEIGHT_KEY to state.headerHeight.intValue,
                )
            },
            restore = { map ->
                CollapsibleHeaderState(
                    parallaxFactor = map[PARALLAX_FACTOR_KEY] as Float,
                    flingAnimationSpec = flingAnimationSpec,
                    snapAnimationSpec = snapAnimationSpec
                ).also { state ->
                    state.updateHeaderHeight(map[HEADER_HEIGHT_KEY] as Int)
                    state._offsetYState.floatValue = map[Y_OFFSET_KEY] as Float
                }
            }
        )
    }

    private val _headerHeight = mutableIntStateOf(Int.Zero)
    val headerHeight = _headerHeight.asIntState()
    fun updateHeaderHeight(height: Int) {
        _headerHeight.intValue = height
    }

    private val _offsetYState = mutableFloatStateOf(Float.Zero)
    val offsetYState = _offsetYState.asFloatState()

    val collapseProgress = derivedStateOf {
        offsetYState.floatValue.absoluteValue / headerHeight.intValue
    }.asFloatState()
    val expansionProgress = derivedStateOf {
        Float.One - collapseProgress.floatValue
    }.asFloatState()

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Don't intercept if scrolling down.
            if (available.y > 0f) return Offset.Zero

            val delta = available.y
            val oldScrollOffset = offsetYState.floatValue
            val newOffset = (offsetYState.floatValue + delta).coerceIn(
                minimumValue = -headerHeight.intValue.toFloat(),
                maximumValue = Float.Zero
            )
            _offsetYState.floatValue = newOffset

            val consumed = offsetYState.floatValue - oldScrollOffset
            return Offset(x = 0f, y = consumed)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (available.y < 0f || consumed.y < 0f) {
                // When scrolling up, just update the state's height offset.
                val oldHeightOffset = offsetYState.floatValue
                _offsetYState.floatValue = (offsetYState.floatValue + consumed.y).coerceIn(
                    minimumValue = -headerHeight.intValue.toFloat(),
                    maximumValue = Float.Zero
                )
                return Offset(0f, offsetYState.floatValue - oldHeightOffset)
            }

            if (available.y > 0f) {
                // Adjust the height offset in case the consumed delta Y is less than what was
                // recorded as available delta Y in the pre-scroll.
                val oldHeightOffset = offsetYState.floatValue
                _offsetYState.floatValue = (offsetYState.floatValue + available.y).coerceIn(
                    minimumValue = -headerHeight.intValue.toFloat(),
                    maximumValue = Float.Zero
                )
                return Offset(0f, offsetYState.floatValue - oldHeightOffset)
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (collapseProgress.floatValue < 0.01f || collapseProgress.floatValue == 1f)
                return Velocity.Zero

            val superConsumed = super.onPostFling(consumed, available)

            val velocity = available.y
            var remainingVelocity = velocity
            // In case there is an initial velocity that was left after a previous user fling, animate to
            // continue the motion to expand or collapse the app bar.
            if (flingAnimationSpec != null && abs(velocity) > 1f) {
                var lastValue = 0f
                AnimationState(initialValue = 0f, initialVelocity = velocity).animateDecay(
                    flingAnimationSpec
                ) {
                    val delta = value - lastValue
                    val initialOffset = offsetYState.floatValue
                    _offsetYState.floatValue = (initialOffset + delta).coerceIn(
                        minimumValue = -headerHeight.intValue.toFloat(),
                        maximumValue = Float.Zero
                    )
                    val consumed = abs(initialOffset - offsetYState.floatValue)
                    lastValue = value
                    remainingVelocity = this.velocity

                    // avoid rounding errors and stop if anything is unconsumed
                    if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
                }
            }

            // Snap if animation specs were provided.
            if (snapAnimationSpec != null) {
                if (offsetYState.floatValue < 0 && offsetYState.floatValue > -headerHeight.intValue) {
                    AnimationState(initialValue = offsetYState.floatValue).animateTo(
                        if (collapseProgress.floatValue < 0.5f) {
                            0f
                        } else {
                            -headerHeight.intValue.toFloat()
                        },
                        animationSpec = snapAnimationSpec,
                    ) {
                        _offsetYState.floatValue = value
                    }
                }
            }

            return superConsumed + Velocity(0f, remainingVelocity)
        }
    }
}

@Composable
fun rememberCollapsibleHeaderState(
    parallaxFactor: Float = Float.One,
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
    snapAnimationSpec: AnimationSpec<Float>? = null,
): CollapsibleHeaderState = rememberSaveable(
    parallaxFactor,
    saver = CollapsibleHeaderState.saver(flingAnimationSpec, snapAnimationSpec)
) {
    CollapsibleHeaderState(
        parallaxFactor = parallaxFactor,
        flingAnimationSpec = flingAnimationSpec,
        snapAnimationSpec = snapAnimationSpec,
    )
}