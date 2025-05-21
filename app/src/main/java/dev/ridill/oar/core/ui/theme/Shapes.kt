package dev.ridill.oar.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CornerRadiusExtraSmall = 4.dp
val CornerRadiusSmall = 8.dp
val CornerRadiusMedium = 12.dp
val CornerRadiusLarge = 16.dp
val CornerRadiusExtraLarge = 24.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(CornerRadiusExtraSmall),
    small = RoundedCornerShape(CornerRadiusSmall),
    medium = RoundedCornerShape(CornerRadiusMedium),
    large = RoundedCornerShape(CornerRadiusLarge),
    extraLarge = RoundedCornerShape(CornerRadiusExtraLarge),
)