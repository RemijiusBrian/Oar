package dev.ridill.oar.core.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.oar.core.domain.util.Zero

data class Spacing(
    val default: Dp = Dp.Zero,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

val PaddingScrollEnd = 80.dp

val BorderWidthStandard = 1.dp

val IconSizeSmall = 12.dp
val IconSizeMedium = 16.dp

data class Elevation(
    val default: Dp = Dp.Zero,
    val level0: Dp = 0.0.dp,
    val level1: Dp = 1.0.dp,
    val level2: Dp = 3.0.dp,
    val level3: Dp = 6.0.dp,
    val level4: Dp = 8.0.dp,
    val level5: Dp = 12.0.dp
)

val LocalElevation = compositionLocalOf { Elevation() }