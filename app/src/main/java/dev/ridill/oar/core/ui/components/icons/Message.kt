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

val Icons.Rounded.Message: ImageVector
    get() {
        if (_message != null) {
            return _message!!
        }
        _message = materialIcon("Rounded.Message") {
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
                moveTo(19.675f, 2.758f)
                arcTo(
                    11.936f,
                    11.936f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    10.474f,
                    0.1f
                )
                arcTo(12f, 12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12.018f, 24f)
                horizontalLineTo(19f)
                arcToRelative(
                    5.006f,
                    5.006f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    5f,
                    -5f
                )
                verticalLineTo(11.309f)
                lineToRelative(0f, -0.063f)
                arcTo(
                    12.044f,
                    12.044f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    19.675f,
                    2.758f
                )
                close()
                moveTo(8f, 7f)
                horizontalLineToRelative(4f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 2f)
                horizontalLineTo(8f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 7f)
                close()
                moveToRelative(8f, 10f)
                horizontalLineTo(8f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -2f)
                horizontalLineToRelative(8f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 2f)
                close()
                moveToRelative(0f, -4f)
                horizontalLineTo(8f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -2f)
                horizontalLineToRelative(8f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 2f)
                close()
            }
        }
        return _message!!
    }

private var _message: ImageVector? = null