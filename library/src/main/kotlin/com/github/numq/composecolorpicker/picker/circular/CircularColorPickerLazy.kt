package com.github.numq.composecolorpicker.picker.circular

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.github.numq.composecolorpicker.color.ColorCalculation
import com.github.numq.composecolorpicker.offset.OffsetMapper
import com.github.numq.composecolorpicker.offset.OffsetPercentageCalculation
import com.github.numq.composecolorpicker.picker.ColorPickerComponent
import com.github.numq.composecolorpicker.picker.ColorPickerConstant
import com.github.numq.composecolorpicker.shape.CircularShape

@Composable
fun CircularColorPickerLazy(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    initialColor: Color,
    onEndOfColorChange: () -> Unit = {},
    onColorChange: (Color) -> Unit,
) {
    require(initialColor.isSpecified) { "Initial color should be specified" }

    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    val updatedOnEndOfColorChange by rememberUpdatedState(onEndOfColorChange)

    val backgroundGradient = remember {
        Brush.sweepGradient(colors = List(360) { angle ->
            Color.hsv(hue = angle.toFloat(), saturation = 1f, value = 1f)
        })
    }

    val foregroundGradient = remember {
        Brush.radialGradient(colors = listOf(Color.White, Color.Transparent))
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val diameter = remember(maxWidth, maxHeight) { min(maxWidth, maxHeight).value }

        val radius = remember(diameter) { diameter / 2f }

        val (indicatorOffsetPercentage, setIndicatorOffsetPercentage) = remember {
            mutableStateOf(
                OffsetPercentageCalculation.calculateHSVCircleOffsetPercentage(color = initialColor, radius = radius)
            )
        }

        val color by remember(indicatorOffsetPercentage, radius) {
            derivedStateOf {
                ColorCalculation.calculateHSVCircleColor(offsetPercentage = indicatorOffsetPercentage, radius = radius)
            }
        }

        LaunchedEffect(color) {
            updatedOnColorChange(color)
        }

        val colorPickerSize = remember(diameter) { Size(width = diameter, height = diameter) }

        val shape = remember(diameter) { CircularShape(diameter = diameter) }

        ColorPickerComponent(modifier = Modifier.size(diameter.dp).clip(shape),
            mapIndicatorOffset = { offset ->
                OffsetMapper.mapCircularOffset(
                    outerRadius = radius, innerRadius = 0f, offset = offset, center = colorPickerSize.center
                )
            },
            indicatorOffsetPercentage = indicatorOffsetPercentage,
            onIndicatorOffsetPercentageChange = setIndicatorOffsetPercentage,
            onEndOfIndicatorOffsetPercentageChange = updatedOnEndOfColorChange,
            indicatorContent = { offset ->
                drawCircle(
                    color = if (color.luminance() > .5f) Color.Black else Color.White,
                    radius = indicatorRadius,
                    center = offset,
                    style = Stroke(width = indicatorThickness)
                )
            },
            content = {
                drawCircle(brush = backgroundGradient)
                drawCircle(brush = foregroundGradient)
            })
    }
}
