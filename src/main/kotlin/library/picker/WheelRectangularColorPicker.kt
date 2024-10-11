package library.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import library.hue
import library.picker.core.ColorPickerComponent
import kotlin.math.sqrt

@Composable
fun WheelRectangularColorPicker(
    modifier: Modifier,
    indicatorThickness: Float,
    indicatorRadius: Float,
    wheelThicknessPercentage: Float,
    isRotating: Boolean,
    onColorChange: (Color) -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val (baseColor, setBaseColor) = remember { mutableStateOf(Color.Unspecified) }

        val rotationDegrees by remember(isRotating, baseColor) {
            derivedStateOf {
                if (isRotating && baseColor != Color.Unspecified) baseColor.hue() - 45f else 0f
            }
        }

        WheelColorPicker(
            modifier = Modifier.aspectRatio(1f).fillMaxSize(),
            indicatorThickness = indicatorThickness,
            indicatorRadius = indicatorRadius,
            thicknessPercentage = wheelThicknessPercentage,
            onColorChange = setBaseColor,
        ) { diameter ->
            val innerSize by remember(diameter) {
                derivedStateOf {
                    (diameter / sqrt(2f)).dp.let { side ->
                        DpSize(side, side)
                    }
                }
            }

            ColorPickerComponent(
                modifier = Modifier.size(innerSize).composed {
                    if (isRotating) rotate(rotationDegrees) else this
                }.clipToBounds(),
                indicatorThickness = indicatorThickness,
                indicatorRadius = indicatorRadius,
                onColorChange = onColorChange
            ) {
                drawRect(brush = Brush.horizontalGradient(listOf(Color.White, baseColor)))

                drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            }
        }
    }
}