package com.github.numq.picker.wheel.triangle

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.github.numq.picker.ColorPickerConstant
import com.github.numq.picker.wheel.WheelColorPicker

@Composable
fun WheelTriangleColorPickerHSV(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    wheelThicknessPercentage: Float = ColorPickerConstant.DEFAULT_THICKNESS_PERCENTAGE,
    wheelIndicatorContent: (DrawScope.(indicatorOffset: Offset) -> Unit)? = null,
    isRotating: Boolean,
    hue: Float,
    onHueChange: (Float) -> Unit,
    saturation: Float,
    onSaturationChange: (Float) -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    require(hue in 0f..360f) { "Hue should be within 0f..360f" }

    require(saturation in 0f..1f) { "Saturation should be within 0f..1f" }

    require(value in 0f..1f) { "Value should be within 0f..1f" }

    val updatedOnHueChange by rememberUpdatedState(onHueChange)

    val updatedOnSaturationChange by rememberUpdatedState(onSaturationChange)

    val updatedOnValueChange by rememberUpdatedState(onValueChange)

    WheelColorPicker(
        modifier = modifier.aspectRatio(1f),
        thicknessPercentage = wheelThicknessPercentage,
        indicatorContent = { offset ->
            wheelIndicatorContent?.invoke(this, offset) ?: rotate(hue) {
                rotate(-90f) {
                    drawLine(
                        color = Color.White,
                        start = size.center,
                        end = size.center.plus(Offset(0f, size.height)),
                        strokeWidth = 1f
                    )
                }
            }
        },
        hue = hue,
        onHueChange = updatedOnHueChange,
        content = { diameter ->
            TriangleColorPickerSV(
                modifier = Modifier.size(diameter.dp).composed {
                    if (isRotating) rotate(-90f) else this
                },
                isRotating = isRotating,
                indicatorContent = { offset ->
                    drawCircle(
                        color = if (saturation > .5f) Color.Black else Color.White,
                        radius = indicatorRadius,
                        center = offset,
                        style = Stroke(width = indicatorThickness)
                    )
                },
                hue = hue,
                saturation = saturation,
                onSaturationChange = updatedOnSaturationChange,
                value = value,
                onValueChange = updatedOnValueChange
            )
        }
    )
}
