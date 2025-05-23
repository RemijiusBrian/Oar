package dev.ridill.oar.core.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.ui.theme.PaddingScrollEnd
import dev.ridill.oar.core.ui.theme.SelectableColorsList
import dev.ridill.oar.core.ui.theme.contentColor
import dev.ridill.oar.core.ui.theme.spacing

@Composable
fun HorizontalColorSelectionList(
    onColorSelect: (Color) -> Unit,
    modifier: Modifier = Modifier,
    colorsList: List<Color> = remember { SelectableColorsList },
    selectedColorCode: () -> Int? = { null },
    contentPadding: PaddingValues = PaddingValues(
        start = MaterialTheme.spacing.medium,
        end = PaddingScrollEnd
    ),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(MaterialTheme.spacing.small)
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier,
        reverseLayout = reverseLayout
    ) {
        items(
            items = colorsList,
            key = { it.toArgb() },
            contentType = { "SelectableColor" }
        ) { color ->
            val selected by remember {
                derivedStateOf { color.toArgb() == selectedColorCode() }
            }
            ColorSelector(
                color = color,
                selected = selected,
                onClick = { onColorSelect(color) },
                modifier = Modifier
                    .animateItem()
            )
        }
    }
}

@Composable
private fun ColorSelector(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndicatorColor = remember(color) { color.contentColor() }
    val selectedTransition = updateTransition(
        targetState = selected,
        label = "SelectedTransition"
    )
    val borderWidth by selectedTransition.animateDp(
        targetValueByState = { if (it) SelectedBorderWidth else UnselectedBorderWidth },
        label = "BorderWidthAnimation"
    )
    val selectedIndicatorPainter = rememberVectorPainter(image = Icons.Default.Check)
    val indicatorScale by selectedTransition.animateFloat(
        targetValueByState = { if (it) Float.One else Float.Zero },
        label = "IndicatorScaleAnimation"
    )

    Spacer(
        modifier = Modifier
            .size(ColorSelectorSize)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .drawBehind {
                // Draw Color
                drawCircle(color = color)

                // Draw border
                drawCircle(
                    color = selectedIndicatorColor,
                    style = Stroke(borderWidth.toPx())
                )

                // Draw selected indicator
                withTransform(
                    transformBlock = {
                        translate(
                            left = this@drawBehind.center.x - (selectedIndicatorPainter.intrinsicSize.width / 2f),
                            top = this@drawBehind.center.y - (selectedIndicatorPainter.intrinsicSize.height / 2f)
                        )
                        scale(
                            scaleX = indicatorScale,
                            scaleY = indicatorScale
                        )
                    }
                ) {
                    with(selectedIndicatorPainter) {
                        draw(
                            size = selectedIndicatorPainter.intrinsicSize,
                            colorFilter = ColorFilter.tint(selectedIndicatorColor)
                        )
                    }
                }
            }
            .then(modifier),
    )
}

private val ColorSelectorSize = 32.dp
private val SelectedBorderWidth = 2.dp
private val UnselectedBorderWidth = 1.dp