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
import androidx.compose.ui.graphics.isSpecified
import com.github.numq.composecolorpicker.color.ColorCalculation
import com.github.numq.composecolorpicker.color.saturation
import com.github.numq.composecolorpicker.color.value
import com.github.numq.composecolorpicker.offset.OffsetPercentageCalculation
import com.github.numq.composecolorpicker.picker.ColorPickerComponent

@Composable
fun RectangleColorPickerSVLazy(
    modifier: Modifier,
    isRotating: Boolean,
    indicatorContent: DrawScope.(indicatorOffset: Offset) -> Unit,
    hue: Float,
    initialColor: Color,
    onColorChange: (Color) -> Unit,
) {
    require(hue in 0f..360f) { "Hue should be within 0f..360f" }

    require(initialColor.isSpecified) { "Initial color should be specified" }

    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    val (indicatorOffsetPercentage, setIndicatorOffsetPercentage) = remember {
        mutableStateOf(
            OffsetPercentageCalculation.calculateHSVRectangleOffsetPercentage(
                saturation = initialColor.saturation(),
                value = initialColor.value()
            )
        )
    }

    val color by remember(hue, indicatorOffsetPercentage) {
        derivedStateOf {
            ColorCalculation.calculateHSVRectangleColor(hue = hue, offsetPercentage = indicatorOffsetPercentage)
        }
    }

    LaunchedEffect(color) {
        updatedOnColorChange(color)
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
        onIndicatorOffsetPercentage = setIndicatorOffsetPercentage,
        indicatorContent = indicatorContent,
        content = {
            drawRect(brush = backgroundGradient)
            drawRect(brush = foregroundGradient)
        }
    )
}