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

val Icons.Outlined.CalendarClock: ImageVector
    get() {
        if (_calendarClock != null) {
            return _calendarClock!!
        }
        _calendarClock = materialIcon("Outlined.CalendarClock") {
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
                moveTo(17f, 10.039f)
                curveToRelative(-3.859f, 0f, -7f, 3.14f, -7f, 7f)
                curveToRelative(0f, 3.838f, 3.141f, 6.961f, 7f, 6.961f)
                reflectiveCurveToRelative(7f, -3.14f, 7f, -7f)
                curveToRelative(0f, -3.838f, -3.141f, -6.961f, -7f, -6.961f)
                close()
                moveToRelative(0f, 11.961f)
                curveToRelative(-2.757f, 0f, -5f, -2.226f, -5f, -4.961f)
                curveToRelative(0f, -2.757f, 2.243f, -5f, 5f, -5f)
                reflectiveCurveToRelative(5f, 2.226f, 5f, 4.961f)
                curveToRelative(0f, 2.757f, -2.243f, 5f, -5f, 5f)
                close()
                moveToRelative(1.707f, -4.707f)
                curveToRelative(0.391f, 0.391f, 0.391f, 1.023f, 0f, 1.414f)
                curveToRelative(-0.195f, 0.195f, -0.451f, 0.293f, -0.707f, 0.293f)
                reflectiveCurveToRelative(-0.512f, -0.098f, -0.707f, -0.293f)
                lineToRelative(-1f, -1f)
                curveToRelative(-0.188f, -0.188f, -0.293f, -0.442f, -0.293f, -0.707f)
                verticalLineToRelative(-2f)
                curveToRelative(0f, -0.552f, 0.447f, -1f, 1f, -1f)
                reflectiveCurveToRelative(1f, 0.448f, 1f, 1f)
                verticalLineToRelative(1.586f)
                lineToRelative(0.707f, 0.707f)
                close()
                moveToRelative(5.293f, -10.293f)
                verticalLineToRelative(2f)
                curveToRelative(0f, 0.552f, -0.447f, 1f, -1f, 1f)
                reflectiveCurveToRelative(-1f, -0.448f, -1f, -1f)
                verticalLineToRelative(-2f)
                curveToRelative(0f, -1.654f, -1.346f, -3f, -3f, -3f)
                horizontalLineTo(5f)
                curveToRelative(-1.654f, 0f, -3f, 1.346f, -3f, 3f)
                verticalLineToRelative(1f)
                horizontalLineTo(11f)
                curveToRelative(0.552f, 0f, 1f, 0.448f, 1f, 1f)
                reflectiveCurveToRelative(-0.448f, 1f, -1f, 1f)
                horizontalLineTo(2f)
                verticalLineToRelative(9f)
                curveToRelative(0f, 1.654f, 1.346f, 3f, 3f, 3f)
                horizontalLineToRelative(4f)
                curveToRelative(0.552f, 0f, 1f, 0.448f, 1f, 1f)
                reflectiveCurveToRelative(-0.448f, 1f, -1f, 1f)
                horizontalLineTo(5f)
                curveToRelative(-2.757f, 0f, -5f, -2.243f, -5f, -5f)
                verticalLineTo(7f)
                curveTo(0f, 4.243f, 2.243f, 2f, 5f, 2f)
                horizontalLineToRelative(1f)
                verticalLineTo(1f)
                curveToRelative(0f, -0.552f, 0.448f, -1f, 1f, -1f)
                reflectiveCurveToRelative(1f, 0.448f, 1f, 1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(8f)
                verticalLineTo(1f)
                curveToRelative(0f, -0.552f, 0.447f, -1f, 1f, -1f)
                reflectiveCurveToRelative(1f, 0.448f, 1f, 1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                curveToRelative(2.757f, 0f, 5f, 2.243f, 5f, 5f)
                close()
            }
        }
        return _calendarClock!!
    }

private var _calendarClock: ImageVector? = null