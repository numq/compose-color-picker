package library.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import library.color.hue
import library.color.lightness
import library.color.saturation
import library.color.value
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal object OffsetPercentageCalculation {
    fun calculateHSVCircleOffsetPercentage(color: Color, radius: Float): OffsetPercentage {
        val hue = color.hue()
        val saturation = color.saturation()
        val value = color.value()

        val angle = Math.toRadians(hue.toDouble()).toFloat()

        val distanceFromCenter = saturation * value * radius

        val dx = distanceFromCenter * cos(angle)
        val dy = distanceFromCenter * sin(angle)

        val x = (dx + radius) / (2 * radius)
        val y = (dy + radius) / (2 * radius)

        return OffsetPercentage(x, y)
    }

    fun calculateFullRectangleOffsetPercentage(color: Color): OffsetPercentage {
        val hue = color.hue()
        val lightness = color.lightness()

        return OffsetPercentage(hue / 360f, 1f - lightness)
    }

    fun calculateWheelOffsetPercentage(hue: Float): OffsetPercentage {
        val center = Offset(0.5f, 0.5f)
        val angleInRadians = Math.toRadians(hue.toDouble())

        return OffsetPercentage(
            x = center.x + 0.5f * cos(angleInRadians).toFloat(),
            y = center.y + 0.5f * sin(angleInRadians).toFloat()
        )
    }

    fun calculateHSVRectangleOffsetPercentage(color: Color): OffsetPercentage {
        if (color.isUnspecified) return OffsetPercentage.Zero

        return OffsetPercentage(x = color.saturation(), y = 1f - color.value())
    }

    fun calculateHSVTriangleOffsetPercentage(
        color: Color,
        size: Size,
    ): OffsetPercentage {
        if (color.isUnspecified) return OffsetPercentage.Zero

        val saturation = color.saturation()
        val value = color.value()

        val radius = size.minDimension / 2f
        val center = size.center
        val sqrt3 = sqrt(3.0)

        val x = radius * (2f * value - saturation * value - 1f) * sqrt3 / 2f
        val y = radius * (1f - 3f * saturation * value) / 2f

        return OffsetPercentage(
            x = ((center.x + x) / size.width).toFloat().coerceIn(0f, 1f),
            y = ((center.y + y) / size.height).coerceIn(0f, 1f)
        )
    }
}
