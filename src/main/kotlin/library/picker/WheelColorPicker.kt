package library.picker

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.min
import library.mapper.OffsetMapper.mapCircularOffset
import library.picker.core.ColorPickerComponent
import library.shape.WheelShape

@Composable
fun WheelColorPicker(
    modifier: Modifier,
    indicatorThickness: Float,
    indicatorRadius: Float,
    thicknessPercentage: Float,
    onColorChange: (Color) -> Unit,
    content: @Composable BoxWithConstraintsScope.(diameter: Float) -> Unit,
) {
    val colorChange by rememberUpdatedState(onColorChange)

    val (baseColor, setBaseColor) = remember { mutableStateOf(Color.Unspecified) }

    LaunchedEffect(baseColor) {
        colorChange(baseColor)
    }

    BoxWithConstraints(modifier = modifier.aspectRatio(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
        val outerDiameter = remember(maxWidth, maxHeight) {
            min(maxWidth, maxHeight).value
        }

        val outerRadius = remember(outerDiameter) {
            outerDiameter / 2f
        }

        val thickness = remember(outerDiameter, thicknessPercentage) {
            outerDiameter * thicknessPercentage
        }

        val innerRadius = remember(outerRadius, thickness) {
            outerRadius - (thickness / 2f)
        }

        val innerDiameter = remember(innerRadius) {
            innerRadius * 2f
        }

        val shape = remember(outerDiameter, innerDiameter) {
            WheelShape(outerDiameter = outerDiameter, innerDiameter = innerDiameter)
        }

        ColorPickerComponent(
            modifier = Modifier.fillMaxSize().clip(shape),
            indicatorThickness = indicatorThickness,
            indicatorRadius = indicatorRadius,
            mapIndicatorOffset = { offset ->
                mapCircularOffset(offset = offset, outerRadius = outerRadius, innerRadius = innerRadius).getOrNull()
            },
            onColorChange = setBaseColor
        ) {
            val colors = List(360) { angle ->
                Color.hsv(angle.toFloat(), 1f, 1f)
            }

            rotate(-90f) {
                drawRect(brush = Brush.sweepGradient(colors = colors))
            }
        }

        content(innerDiameter)
    }
}