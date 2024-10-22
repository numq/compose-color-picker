package library.picker

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
import library.color.ColorCalculation
import library.offset.OffsetMapper
import library.offset.OffsetPercentageCalculation
import library.picker.core.ColorPickerComponent
import library.picker.core.ColorPickerConstant
import library.shape.CircularShape

@Composable
fun CircularHSVColorPicker(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    color: Color,
    onColorChange: (Color) -> Unit,
) {
    require(color.isSpecified) { "Color should be specified" }

    val updatedOnColorChange by rememberUpdatedState(onColorChange)

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

        val indicatorOffsetPercentage by remember(color, radius) {
            derivedStateOf {
                OffsetPercentageCalculation.calculateHSVCircleOffsetPercentage(color = color, radius = radius)
            }
        }

        val colorPickerSize = remember(diameter) { Size(width = diameter, height = diameter) }

        val shape = remember(diameter) { CircularShape(diameter = diameter) }

        ColorPickerComponent(
            modifier = Modifier.size(diameter.dp).clip(shape),
            mapIndicatorOffset = { offset ->
                OffsetMapper.mapCircularOffset(
                    outerRadius = radius,
                    innerRadius = 0f,
                    offset = offset,
                    center = colorPickerSize.center
                )
            },
            indicatorOffsetPercentage = indicatorOffsetPercentage,
            onIndicatorOffsetPercentage = { offsetPercentage ->
                updatedOnColorChange(
                    ColorCalculation.calculateHSVCircleColor(
                        offsetPercentage = offsetPercentage,
                        radius = radius
                    )
                )
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
                drawCircle(brush = backgroundGradient)
                drawCircle(brush = foregroundGradient)
            }
        )
    }
}
