package dev.ridill.oar.core.ui.components

import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.RichTooltipColors
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.window.PopupPositionProvider

@Composable
fun OarPlainTooltip(
    tooltipText: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(),
    positionProvider: PopupPositionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
    focusable: Boolean = true,
    enableUserInput: Boolean = true,
    tooltipShape: Shape = TooltipDefaults.plainTooltipContainerShape,
    tooltipContainerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    tooltipContentColor: Color = TooltipDefaults.plainTooltipContentColor,
    hapticFeedback: HapticFeedback = LocalHapticFeedback.current,
    content: @Composable () -> Unit
) {
    // state.isVisible = true means long press was handled and tooltip is shown so perform haptic feedback
    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    TooltipBox(
        positionProvider = positionProvider,
        tooltip = {
            PlainTooltip(
                shape = tooltipShape,
                containerColor = tooltipContainerColor,
                contentColor = tooltipContentColor,
                content = { Text(tooltipText) }
            )
        },
        state = state,
        modifier = modifier,
        focusable = focusable,
        enableUserInput = enableUserInput,
        content = content
    )
}

@Composable
fun OarRichTooltip(
    tooltipText: String,
    modifier: Modifier = Modifier,
    tooltipTitle: String? = null,
    action: @Composable (() -> Unit)? = null,
    state: TooltipState = rememberTooltipState(),
    positionProvider: PopupPositionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
    focusable: Boolean = true,
    enableUserInput: Boolean = true,
    tooltipShape: Shape = TooltipDefaults.richTooltipContainerShape,
    tooltipColors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    hapticFeedback: HapticFeedback = LocalHapticFeedback.current,
    content: @Composable () -> Unit
) {
    // state.isVisible = true means long press was handled and tooltip is shown so perform haptic feedback
    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    TooltipBox(
        positionProvider = positionProvider,
        tooltip = {
            RichTooltip(
                title = tooltipTitle?.let {
                    { Text(tooltipTitle) }
                },
                text = { Text(tooltipText) },
                action = action,
                shape = tooltipShape,
                colors = tooltipColors
            )
        },
        state = state,
        modifier = modifier,
        focusable = focusable,
        enableUserInput = enableUserInput,
        content = content
    )
}