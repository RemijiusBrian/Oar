package dev.ridill.oar.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import dev.ridill.oar.core.domain.util.Zero

class ArrangementTopWithFooter(override val spacing: Dp = Dp.Zero) : Arrangement.Vertical {
    override fun Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
        val spacingPx = spacing.roundToPx()
        var y = 0
        sizes.forEachIndexed { index, size ->
            outPositions[index] = y
            y += (size + spacingPx)
        }
        if (y < totalSize) {
            val lastIndex = outPositions.lastIndex
            outPositions[lastIndex] = totalSize - sizes.last()
        }
    }
}