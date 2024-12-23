package com.github.numq.composecolorpicker.picker.wheel.rectangle

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.github.numq.composecolorpicker.color.ColorCalculation
import com.github.numq.composecolorpicker.color.saturation
import com.github.numq.composecolorpicker.color.value
import com.github.numq.composecolorpicker.offset.OffsetPercentageCalculation
import com.github.numq.composecolorpicker.picker.ColorPickerComponent

@Composable
fun RectangleColorPickerSV(
    modifier: Modifier,
    isRotating: Boolean,
    indicatorContent: DrawScope.(indicatorOffset: Offset) -> Unit,
    hue: Float,
    saturation: Float,
    onSaturationChange: (Float) -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
    onEndOfChange: () -> Unit,
) {
    require(hue in 0f..360f) { "Hue should be within 0f..360f" }

    require(saturation in 0f..1f) { "Saturation should be within 0f..1f" }

    require(value in 0f..1f) { "Value should be within 0f..1f" }

    val updatedOnSaturationChange by rememberUpdatedState(onSaturationChange)

    val updatedOnValueChange by rememberUpdatedState(onValueChange)

    val updatedOnEndOfChange by rememberUpdatedState(onEndOfChange)

    val indicatorOffsetPercentage by remember(saturation, value) {
        derivedStateOf {
            OffsetPercentageCalculation.calculateHSVRectangleOffsetPercentage(saturation = saturation, value = value)
        }
    }

    val rotationDegrees by remember(isRotating, hue) {
        derivedStateOf {
            if (isRotating) (hue % 360f) + 45f else 0f
        }
    }

    val backgroundGradient = remember(hue) {
        Brush.horizontalGradient(
            colors = listOf(Color.White, Color.hsv(hue, 1f, 1f))
        )
    }

    val foregroundGradient = remember {
        Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black)
        )
    }

    ColorPickerComponent(
        modifier = modifier.fillMaxSize().rotate(rotationDegrees).clipToBounds(),
        indicatorOffsetPercentage = indicatorOffsetPercentage,
        onIndicatorOffsetPercentageChange = { offsetPercentage ->
            ColorCalculation.calculateHSVRectangleColor(hue, offsetPercentage).run {
                updatedOnSaturationChange(saturation())
                updatedOnValueChange(value())
            }
        },
        onEndOfIndicatorOffsetPercentageChange = updatedOnEndOfChange,
        indicatorContent = indicatorContent,
        content = {
            drawRect(brush = backgroundGradient)
            drawRect(brush = foregroundGradient)
        }
    )
}