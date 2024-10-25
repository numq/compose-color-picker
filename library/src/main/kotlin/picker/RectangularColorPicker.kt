package picker

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
import color.ColorCalculation
import offset.OffsetPercentageCalculation
import picker.core.ColorPickerComponent
import picker.core.ColorPickerConstant

@Composable
fun RectangularColorPicker(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    color: Color,
    onColorChange: (Color) -> Unit,
) {
    require(color.isSpecified) { "Color should be specified" }

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

    val indicatorOffsetPercentage by remember(color) {
        derivedStateOf {
            OffsetPercentageCalculation.calculateFullRectangleOffsetPercentage(color = color)
        }
    }

    Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        ColorPickerComponent(
            modifier = Modifier.fillMaxSize().clipToBounds(),
            indicatorOffsetPercentage = indicatorOffsetPercentage,
            onIndicatorOffsetPercentage = { offsetPercentage ->
                updatedOnColorChange(ColorCalculation.calculateFullRectangleColor(offsetPercentage = offsetPercentage))
            },
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