package dev.ridill.oar.core.ui.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path

val Icons.Rounded.Tags: ImageVector
    get() {
        if (_tags != null) {
            return _tags!!
        }
        _tags = materialIcon("Rounded.Tags") {
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
                moveTo(21.526f, 8.284f)
                lineTo(13.937f, 0.879f)
                curveTo(13.278f, 0.219f, 12.33f, -0.104f, 11.409f, 0.028f)
                lineTo(4.521f, 0.97f)
                curveToRelative(-0.547f, 0.075f, -0.93f, 0.579f, -0.855f, 1.126f)
                curveToRelative(0.075f, 0.547f, 0.578f, 0.929f, 1.127f, 0.855f)
                lineToRelative(6.889f, -0.942f)
                curveToRelative(0.306f, -0.042f, 0.622f, 0.063f, 0.851f, 0.292f)
                lineToRelative(7.59f, 7.405f)
                curveToRelative(1.045f, 1.045f, 1.147f, 2.68f, 0.323f, 3.847f)
                curveToRelative(-0.234f, -0.467f, -0.523f, -0.912f, -0.911f, -1.3f)
                lineToRelative(-7.475f, -7.412f)
                curveToRelative(-0.658f, -0.658f, -1.597f, -0.975f, -2.528f, -0.851f)
                lineToRelative(-6.889f, 0.942f)
                curveToRelative(-0.454f, 0.062f, -0.808f, 0.425f, -0.858f, 0.881f)
                lineToRelative(-0.765f, 6.916f)
                curveToRelative(-0.1f, 0.911f, 0.214f, 1.804f, 0.864f, 2.453f)
                lineToRelative(7.416f, 7.353f)
                curveToRelative(0.944f, 0.945f, 2.199f, 1.464f, 3.534f, 1.464f)
                horizontalLineToRelative(0.017f)
                curveToRelative(1.342f, -0.004f, 2.6f, -0.532f, 3.543f, -1.487f)
                lineToRelative(3.167f, -3.208f)
                curveToRelative(0.927f, -0.939f, 1.393f, -2.159f, 1.423f, -3.388f)
                lineToRelative(0.577f, -0.576f)
                curveToRelative(1.925f, -1.95f, 1.914f, -5.112f, -0.032f, -7.057f)
                close()
                moveToRelative(-15.526f, 1.716f)
                curveToRelative(-0.552f, 0f, -1f, -0.448f, -1f, -1f)
                curveToRelative(0.006f, -1.308f, 1.994f, -1.307f, 2f, 0f)
                curveToRelative(0f, 0.552f, -0.448f, 1f, -1f, 1f)
                close()
            }
        }
        return _tags!!
    }

private var _tags: ImageVector? = null