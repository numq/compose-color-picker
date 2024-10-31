package com.github.numq.composecolorpicker.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import com.github.numq.composecolorpicker.color.hue
import com.github.numq.composecolorpicker.color.lightness
import com.github.numq.composecolorpicker.color.saturation
import com.github.numq.composecolorpicker.color.value
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal object OffsetPercentageCalculation {
    fun calculateHSVCircleOffsetPercentage(color: Color, radius: Float): OffsetPercentage {
        require(color.isSpecified) { "Color should be specified" }

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
        require(color.isSpecified) { "Color should be specified" }

        val hue = color.hue()
        val lightness = color.lightness()

        return OffsetPercentage(hue / 360f, 1f - lightness)
    }

    fun calculateWheelOffsetPercentage(hue: Float): OffsetPercentage {
        require(hue in 0f..360f) { "Hue should be within 0f..360f" }

        val center = Offset(0.5f, 0.5f)
        val angleInRadians = Math.toRadians(hue.toDouble())

        return OffsetPercentage(
            x = center.x + 0.5f * cos(angleInRadians).toFloat(),
            y = center.y + 0.5f * sin(angleInRadians).toFloat()
        )
    }

    fun calculateHSVRectangleOffsetPercentage(saturation: Float, value: Float): OffsetPercentage {
        require(saturation in 0f..1f) { "Saturation should be within 0f..1f" }

        require(value in 0f..1f) { "Value should be within 0f..1f" }

        return OffsetPercentage(x = saturation, y = 1f - value)
    }

    fun calculateHSVTriangleOffsetPercentage(saturation: Float, value: Float, size: Size): OffsetPercentage {
        require(saturation in 0f..1f) { "Saturation should be within 0f..1f" }

        require(value in 0f..1f) { "Value should be within 0f..1f" }

        require(size.isSpecified) { "Size should be specified" }

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
