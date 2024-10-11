package library.picker

import library.shape.TriangleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import library.hue
import library.mapper.OffsetMapper.mapTriangularOffset
import library.picker.core.ColorPickerComponent
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WheelTriangularColorPicker(
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
                if (isRotating && baseColor != Color.Unspecified) baseColor.hue() else 0f
            }
        }

        WheelColorPicker(
            modifier = Modifier.aspectRatio(1f).fillMaxSize(),
            indicatorThickness = indicatorThickness,
            indicatorRadius = indicatorRadius,
            thicknessPercentage = wheelThicknessPercentage,
            onColorChange = setBaseColor
        ) { diameter ->
            val radius = remember(diameter) { diameter / 2f }

            val center = remember(radius) { Offset(radius, radius) }

            val vertices by remember(center, radius) {
                derivedStateOf {
                    Array(3) { index ->
                        val angle = Math.toRadians((120f * index).toDouble() - 90f)
                        Offset(
                            x = center.x + (radius * cos(angle)).toFloat(),
                            y = center.y + (radius * sin(angle)).toFloat()
                        )
                    }
                }
            }

            val shape = remember(vertices) { TriangleShape(vertices = vertices) }

            ColorPickerComponent(
                modifier = Modifier.size(diameter.dp).rotate(rotationDegrees).clip(shape),
                indicatorThickness = indicatorThickness,
                indicatorRadius = indicatorRadius,
                onColorChange = onColorChange,
                mapIndicatorOffset = { offset ->
                    mapTriangularOffset(offset = offset, vertices = vertices).getOrNull()
                }
            ) {
                drawRect(brush = Brush.horizontalGradient(listOf(Color.Black, Color.White)))

                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(baseColor, Color.Transparent),
                        startY = 0f,
                        endY = size.height * .75f
                    )
                )
            }
        }
    }
}