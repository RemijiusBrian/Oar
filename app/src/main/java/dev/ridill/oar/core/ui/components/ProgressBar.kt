package dev.ridill.oar.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.domain.util.Zero
import kotlin.math.abs

@Composable
fun OarProgressBar(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    startColor: Color = ProgressIndicatorDefaults.linearColor,
    endColor: Color = startColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize,
    valueIndicatorSize: Dp = ProgressIndicatorDefaults.LinearTrackStopIndicatorSize
) {
    val coercedProgress = { progress().coerceIn(Float.Zero, Float.One) }
    val color = lerp(
        start = startColor,
        stop = endColor,
        fraction = coercedProgress()
    )

    Canvas(
        modifier = modifier
            .height(TrackHeight)
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo =
                    ProgressBarRangeInfo(coercedProgress(), Float.Zero..Float.One)
            }
    ) {
        val strokeWidth = size.height
        val adjustedGapSize = if (strokeCap == StrokeCap.Butt || size.height > size.width) {
            gapSize
        } else {
            gapSize + (strokeWidth / 2).toDp()
        }
        val gapSizeFraction = adjustedGapSize / size.width.toDp()
        val currentCoercedProgress = coercedProgress()

        // track
        val trackStartFraction = currentCoercedProgress + gapSizeFraction

        if (trackStartFraction <= 1f) {
            drawLinearIndicator(
                startFraction = trackStartFraction,
                endFraction = 1f,
                color = trackColor,
                strokeWidth = strokeWidth,
                strokeCap = strokeCap
            )
        }
        if (currentCoercedProgress > gapSizeFraction) {
            // indicator
            drawLinearIndicator(
                startFraction = 0f,
                endFraction = currentCoercedProgress - gapSizeFraction,
                color = color,
                strokeWidth = strokeWidth,
                strokeCap = strokeCap
            )

        }

        // Value Indicator
        drawLine(
            color = color,
            start = Offset(
                size.width * currentCoercedProgress,
                0f
            ),
            end = Offset(
                size.width * currentCoercedProgress,
                size.height
            ),
            strokeWidth = valueIndicatorSize.toPx(),
            cap = strokeCap
        )
    }
}

private val TrackHeight = 40.dp

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width

    // if there isn't enough space to draw the stroke caps, fall back to StrokeCap.Butt
    if (strokeCap == StrokeCap.Butt || height > width) {
        // Progress line
        drawLine(
            color = color,
            start = Offset(barStart, yOffset),
            end = Offset(barEnd, yOffset),
//            strokeWidth = strokeWidth
        )
    } else {
        // need to adjust barStart and barEnd for the stroke caps
        val strokeCapOffset = strokeWidth / 2
        val coerceRange = strokeCapOffset..(width - strokeCapOffset)
        val adjustedBarStart = barStart.coerceIn(coerceRange)
        val adjustedBarEnd = barEnd.coerceIn(coerceRange)

        if (abs(endFraction - startFraction) > 0) {
            // Progress line
            drawLine(
                color = color,
                start = Offset(adjustedBarStart, yOffset),
                end = Offset(adjustedBarEnd, yOffset),
                strokeWidth = strokeWidth,
                cap = strokeCap,
            )
        }
    }
}

//private fun DrawScope.drawSlopes(
//    startX: Float,
//    endX: Float,
//    strokeWidth: Float
//) {
//    var xOffset = startX
//    while (xOffset <= endX) {
//        drawLine(
//            color = Color.Black,
//            start = Offset(x = xOffset - size.height, y = -(size.height * 0.5f)),
//            end = Offset(x = xOffset, y = (size.height * 1.5f)),
//            strokeWidth = strokeWidth
//        )
//
//        xOffset += 4.dp.toPx()
//    }
//}