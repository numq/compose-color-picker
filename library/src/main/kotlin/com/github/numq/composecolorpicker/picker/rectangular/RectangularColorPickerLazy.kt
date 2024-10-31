package com.github.numq.composecolorpicker.picker.rectangular

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import com.github.numq.composecolorpicker.color.ColorCalculation
import com.github.numq.composecolorpicker.offset.OffsetPercentageCalculation
import com.github.numq.composecolorpicker.picker.ColorPickerComponent
import com.github.numq.composecolorpicker.picker.ColorPickerConstant

@Composable
fun RectangularColorPickerLazy(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    initialColor: Color,
    onColorChange: (Color) -> Unit,
) {
    require(initialColor.isSpecified) { "Initial color should be specified" }

    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    val backgroundGradient = remember {
        Brush.horizontalGradient(
            colors = List(360) { angle ->
                Color.hsv(hue = angle.toFloat(), saturation = 1f, value = 1f)
            }
        )
    }

    val foregroundGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.White,
                Color.Transparent,
                Color.Black
            )
        )
    }

    val (indicatorOffsetPercentage, setIndicatorOffsetPercentage) = remember {
        mutableStateOf(
            OffsetPercentageCalculation.calculateFullRectangleOffsetPercentage(color = initialColor)
        )
    }

    val color by remember(indicatorOffsetPercentage) {
        derivedStateOf {
            ColorCalculation.calculateFullRectangleColor(offsetPercentage = indicatorOffsetPercentage)
        }
    }

    LaunchedEffect(color) {
        updatedOnColorChange(color)
    }

    Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        ColorPickerComponent(
            modifier = Modifier.fillMaxSize().clipToBounds(),
            indicatorOffsetPercentage = indicatorOffsetPercentage,
            onIndicatorOffsetPercentage = setIndicatorOffsetPercentage,
            indicatorContent = { offset ->
                drawCircle(
                    color = if (color.luminance() > .5f) Color.Black else Color.White,
                    radius = indicatorRadius,
                    center = offset,
                    style = Stroke(width = indicatorThickness)
                )
            },
            content = {
                drawRect(brush = backgroundGradient)
                drawRect(brush = foregroundGradient)
            }
        )
    }
}