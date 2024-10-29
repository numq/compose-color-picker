package color

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import offset.OffsetPercentage
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

internal object ColorCalculation {
    fun calculateHSVCircleColor(offsetPercentage: OffsetPercentage, radius: Float): Color {
        val center = Offset(radius, radius)

        val x = offsetPercentage.x * radius * 2f
        val y = offsetPercentage.y * radius * 2f

        val dx = x - center.x
        val dy = y - center.y
        val distanceFromCenter = sqrt(dx * dx + dy * dy).coerceIn(0f, radius)

        val angle = atan2(dy, dx)
        val hue = Math.toDegrees(angle.toDouble()).toFloat()
        val normalizedHue = (hue + 360) % 360

        val saturation = distanceFromCenter / radius

        val value = 1f

        return Color.hsv(hue = normalizedHue, saturation = saturation, value = value)
    }

    fun calculateFullRectangleColor(offsetPercentage: OffsetPercentage): Color {
        val hue = offsetPercentage.x * 360f

        val saturation = when {
            offsetPercentage.y <= 0f -> 0f

            offsetPercentage.y >= 1f -> 0f

            else -> 1f - abs(0.5f - offsetPercentage.y) * 2f
        }

        val lightness = 1f - offsetPercentage.y

        return Color.hsl(hue = hue, saturation = saturation, lightness = lightness)
    }

    fun calculateHSVRectangleColor(hue: Float, offsetPercentage: OffsetPercentage): Color {
        require(hue in 0f..360f) { "Hue should be within 0f..360f" }

        return Color.hsv(hue = hue, saturation = offsetPercentage.x, value = 1f - offsetPercentage.y)
    }

    fun calculateHSVTriangleColor(hue: Float, offsetPercentage: OffsetPercentage, size: Size): Color {
        require(hue in 0f..360f) { "Hue should be within 0f..360f" }

        val x = offsetPercentage.x * size.width
        val y = offsetPercentage.y * size.height

        val normalizedX = (x - size.center.x) / (size.minDimension / 2f)
        val normalizedY = (y - size.center.y) / (size.minDimension / 2f)

        val sqrt3 = sqrt(3.0)

        val saturation = (1f - 2f * normalizedY) / (sqrt3 * normalizedX - normalizedY + 2f).toFloat()
        val value = ((sqrt3 * normalizedX - normalizedY + 2f) / 3f).toFloat()

        val normalizedSaturation = saturation.coerceIn(0f, 1f)

        val normalizedValue = value.coerceIn(0f, 1f)

        return Color.hsv(hue = hue, saturation = normalizedSaturation, value = normalizedValue)
    }
}