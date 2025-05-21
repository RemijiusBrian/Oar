package dev.ridill.oar.core.ui.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path

val Icons.Filled.Google: ImageVector
    get() {
        if (_google != null) {
            return _google!!
        }
        _google = materialIcon("Filled.Google") {
            group {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12.479f, 14.265f)
                    verticalLineToRelative(-3.279f)
                    horizontalLineToRelative(11.049f)
                    curveToRelative(0.108f, 0.571f, 0.164f, 1.247f, 0.164f, 1.979f)
                    curveToRelative(0f, 2.46f, -0.672f, 5.502f, -2.84f, 7.669f)
                    curveTo(18.744f, 22.829f, 16.051f, 24f, 12.483f, 24f)
                    curveTo(5.869f, 24f, 0.308f, 18.613f, 0.308f, 12f)
                    reflectiveCurveTo(5.869f, 0f, 12.483f, 0f)
                    curveToRelative(3.659f, 0f, 6.265f, 1.436f, 8.223f, 3.307f)
                    lineTo(18.392f, 5.62f)
                    curveToRelative(-1.404f, -1.317f, -3.307f, -2.341f, -5.913f, -2.341f)
                    curveTo(7.65f, 3.279f, 3.873f, 7.171f, 3.873f, 12f)
                    reflectiveCurveToRelative(3.777f, 8.721f, 8.606f, 8.721f)
                    curveToRelative(3.132f, 0f, 4.916f, -1.258f, 6.059f, -2.401f)
                    curveToRelative(0.927f, -0.927f, 1.537f, -2.251f, 1.777f, -4.059f)
                    lineTo(12.479f, 14.265f)
                    close()
                }
            }
        }
        return _google!!
    }

private var _google: ImageVector? = null