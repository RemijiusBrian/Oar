package dev.ridill.oar.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.ridill.oar.core.ui.theme.OarTheme
import dev.ridill.oar.core.ui.theme.SelectableColorsList
import dev.ridill.oar.core.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HorizontalColorSelectionList(
    onColorSelect: (Color) -> Unit,
    modifier: Modifier = Modifier,
    colorsList: List<Color> = remember { SelectableColorsList },
    selectedColorCode: () -> Int? = { null },
    contentPadding: PaddingValues = PaddingValues(horizontal = MaterialTheme.spacing.medium),
    reverseLayout: Boolean = false,
) {
    LazyRow(
        contentPadding = contentPadding,
        modifier = modifier,
        reverseLayout = reverseLayout
    ) {
        itemsIndexed(
            items = colorsList,
            key = { _, color -> color.toArgb() },
            contentType = { _, _ -> "SelectableColor" }
        ) { index, color ->
            val selected by remember {
                derivedStateOf { color.toArgb() == selectedColorCode() }
            }
            val toggleableShapes = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                colorsList.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            }
            OutlinedIconToggleButton(
                checked = selected,
                onCheckedChange = { onColorSelect(color) },
                shapes = IconButtonDefaults.toggleableShapes(
                    shape = toggleableShapes.shape,
                    pressedShape = toggleableShapes.pressedShape,
                    checkedShape = toggleableShapes.checkedShape
                ),
                colors = IconButtonDefaults.outlinedIconToggleButtonColors(
                    containerColor = color,
                    checkedContainerColor = color
                ),
                modifier = Modifier
                    .animateItem()
            ) {}
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewHorizontalColorSelectionList() {
    OarTheme {
        Surface {
            HorizontalColorSelectionList(
                onColorSelect = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}