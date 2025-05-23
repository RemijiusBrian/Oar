package dev.ridill.oar.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.oar.core.ui.theme.elevation
import dev.ridill.oar.core.ui.theme.spacing

@Composable
fun ListSeparator(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RectangleShape,
    tonalElevation: Dp = MaterialTheme.elevation.level1
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = shape,
        tonalElevation = tonalElevation
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    vertical = MaterialTheme.spacing.small,
                    horizontal = MaterialTheme.spacing.medium
                )
        ) {
            TitleMediumText(text = label)
        }
    }
}

@Composable
fun ListItemLeadingContentContainer(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = MaterialTheme.elevation.level1,
    contentPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.small),
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation
    ) {
        Box(
            modifier = Modifier
                .requiredWidthIn(min = ContainerMinWidth)
                .wrapContentHeight()
                .padding(contentPadding)
                .then(modifier),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

private val ContainerMinWidth: Dp = 56.dp