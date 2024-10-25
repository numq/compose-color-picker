package picker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import color.ColorCalculation
import offset.OffsetPercentageCalculation
import picker.core.ColorPickerComponent

@Composable
fun RectangleHSVColorPicker(
    modifier: Modifier,
    indicatorThickness: Float,
    indicatorRadius: Float,
    isRotating: Boolean,
    hue: Float,
    color: Color,
    onColorChange: (Color) -> Unit,
) {
    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    val innerIndicatorOffsetPercentage by remember(color) {
        derivedStateOf {
            OffsetPercentageCalculation.calculateHSVRectangleOffsetPercentage(color)
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
        indicatorOffsetPercentage = innerIndicatorOffsetPercentage,
        onIndicatorOffsetPercentage = { offsetPercentage ->
            updatedOnColorChange(ColorCalculation.calculateHSVRectangleColor(hue, offsetPercentage))
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