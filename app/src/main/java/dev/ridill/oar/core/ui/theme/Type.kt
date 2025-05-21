package dev.ridill.oar.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.ridill.oar.R

private val ClashGroteskFontFamily = FontFamily(
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.Thin,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Thin.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.ExtraLight,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.ExtraLight.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Light.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Normal.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Medium.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.SemiBold.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Bold.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.ExtraBold.weight)
        )
    ),
    Font(
        resId = R.font.clash_grotesk,
        weight = FontWeight.Black,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Black.weight)
        )
    )
)

private val DefaultTypography = Typography()
val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = ClashGroteskFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = ClashGroteskFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = ClashGroteskFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = ClashGroteskFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = ClashGroteskFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = ClashGroteskFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = ClashGroteskFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(
        fontFamily = ClashGroteskFontFamily,
        fontSize = 18.sp
    ),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = ClashGroteskFontFamily),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = ClashGroteskFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = ClashGroteskFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = ClashGroteskFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = ClashGroteskFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = ClashGroteskFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = ClashGroteskFontFamily)
)