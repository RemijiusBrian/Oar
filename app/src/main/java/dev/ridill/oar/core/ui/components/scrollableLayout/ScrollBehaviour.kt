package dev.ridill.oar.core.ui.components.scrollableLayout

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.domain.util.Zero
import kotlin.math.abs
import kotlin.math.absoluteValue

@Stable
interface ScrollableLayoutBehaviour {

    val state: ScrollableLayoutState

    /**
     * Indicates whether the top app bar is pinned.
     *
     * A pinned app bar will stay fixed in place when content is scrolled and will not react to any
     * drag gestures.
     */
    val isPinned: Boolean

    /**
     * An optional [AnimationSpec] that defines how the top app bar snaps to either fully collapsed
     * or fully extended state when a fling or a drag scrolled it into an intermediate position.
     */
    val snapAnimationSpec: AnimationSpec<Float>?

    /**
     * An optional [DecayAnimationSpec] that defined how to fling the top app bar when the user
     * flings the app bar itself, or the content below it.
     */
    val flingAnimationSpec: DecayAnimationSpec<Float>?

    /**
     * A [NestedScrollConnection] that should be attached to a [Modifier.nestedScroll] in order to
     * keep track of the scroll events.
     */
    val nestedScrollConnection: NestedScrollConnection
}

object ScrollableLayoutDefaults {

    @Composable
    fun exitUntilCollapsedScrollBehavior(
        parallaxFactor: Float = 1f,
        state: ScrollableLayoutState = rememberCollapsibleHeaderState(
            parallaxFactor = parallaxFactor
        ),
        canScroll: () -> Boolean = { true },
        snapAnimationSpec: AnimationSpec<Float>? = null,
        flingAnimationSpec: DecayAnimationSpec<Float>? = null,
    ): ScrollableLayoutBehaviour =
        remember(state, canScroll, snapAnimationSpec, flingAnimationSpec) {
            ExitUntilCollapsedScrollBehavior(
                state = state,
                snapAnimationSpec = snapAnimationSpec,
                flingAnimationSpec = flingAnimationSpec,
                canScroll = canScroll,
            )
        }
}

private class ExitUntilCollapsedScrollBehavior(
    override val state: ScrollableLayoutState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true },
) : ScrollableLayoutBehaviour {
    override val isPinned: Boolean = false
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Don't intercept if scrolling down.
                if (!canScroll() || available.y > 0f) return Offset.Zero

                val prevHeightOffset = state.offsetY
                state.offsetY = state.offsetY + available.y
                return if (prevHeightOffset != state.offsetY) {
                    // We're in the middle of top app bar collapse or expand.
                    // Consume only the scroll on the Y axis.
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll()) return Offset.Zero

                if (available.y < 0f || consumed.y < 0f) {
                    // When scrolling up, just update the state's height offset.
                    val oldHeightOffset = state.offsetY
                    state.offsetY = state.offsetY + consumed.y
                    return Offset(0f, state.offsetY - oldHeightOffset)
                }

                if (available.y > 0f) {
                    // Adjust the height offset in case the consumed delta Y is less than what was
                    // recorded as available delta Y in the pre-scroll.
                    val oldHeightOffset = state.offsetY
                    state.offsetY = state.offsetY + available.y
                    return Offset(0f, state.offsetY - oldHeightOffset)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed +
                        settleHeader(state, available.y, flingAnimationSpec, snapAnimationSpec)
            }
        }
}

private suspend fun settleHeader(
    state: ScrollableLayoutState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?,
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(initialValue = 0f, initialVelocity = velocity).animateDecay(
            flingAnimationSpec
        ) {
            val delta = value - lastValue
            val initialHeightOffset = state.offsetY
            state.offsetY = initialHeightOffset + delta
            val consumed = abs(initialHeightOffset - state.offsetY)
            lastValue = value
            remainingVelocity = this.velocity
            // avoid rounding errors and stop if anything is unconsumed
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.offsetY < 0 && state.offsetY > state.offsetY) {
            AnimationState(initialValue = state.offsetY).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.offsetY
                },
                animationSpec = snapAnimationSpec,
            ) {
                state.offsetY = value
            }
        }
    }

    return Velocity(0f, remainingVelocity)
}

data class ScrollableLayoutState(
    val parallaxFactor: Float,
) {
    companion object {
        private const val PARALLAX_FACTOR_KEY = "PARALLAX_FACTOR_KEY"
        private const val OFFSET_Y_LIMIT_KEY = "HEADER_HEIGHT_KEY"
        private const val OFFSET_Y_KEY = "Y_OFFSET_KEY"

        fun saver() = mapSaver(
            save = { state ->
                mapOf(
                    PARALLAX_FACTOR_KEY to state.parallaxFactor,
                    OFFSET_Y_KEY to state.offsetY,
                    OFFSET_Y_LIMIT_KEY to state.offsetYLimit,
                )
            },
            restore = { map ->
                ScrollableLayoutState(
                    parallaxFactor = map[PARALLAX_FACTOR_KEY] as Float,
                ).also { state ->
                    state.updateOffsetYLimit(map[OFFSET_Y_LIMIT_KEY] as Float)
                    state._offsetYState.floatValue = map[OFFSET_Y_KEY] as Float
                }
            }
        )
    }

    var offsetYLimit: Float = 0f
        private set

    fun updateOffsetYLimit(limit: Float) {
        offsetYLimit = limit
    }

    var offsetY: Float
        get() = _offsetYState.floatValue
        set(value) {
            _offsetYState.floatValue = value.coerceIn(
                minimumValue = -offsetYLimit,
                maximumValue = Float.Zero
            )
        }
    private val _offsetYState = mutableFloatStateOf(Float.Zero)

    val collapsedFraction: Float
        get() = if (offsetYLimit != 0f) {
            offsetY / offsetYLimit
        } else {
            Float.Zero
        }.absoluteValue
}

@Composable
fun rememberCollapsibleHeaderState(
    parallaxFactor: Float = Float.One,
): ScrollableLayoutState = rememberSaveable(
    parallaxFactor,
    saver = ScrollableLayoutState.saver()
) {
    ScrollableLayoutState(
        parallaxFactor = parallaxFactor,
    )
}