package com.github.numq.picker.wheel.triangle

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.isSpecified
import com.github.numq.color.ColorCalculation
import com.github.numq.color.saturation
import com.github.numq.color.value
import com.github.numq.offset.OffsetMapper
import com.github.numq.offset.OffsetPercentageCalculation
import com.github.numq.picker.ColorPickerComponent
import com.github.numq.shape.TriangleShape
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun TriangleColorPickerSVLazy(
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

    val rotationDegrees by remember(isRotating, hue) {
        derivedStateOf {
            if (isRotating) (hue % 360f) + 90f else 0f
        }
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val colorPickerSize = remember(maxWidth, maxHeight) {
            Size(maxWidth.value, maxHeight.value)
        }

        val (indicatorOffsetPercentage, setIndicatorOffsetPercentage) = remember(colorPickerSize) {
            mutableStateOf(
                OffsetPercentageCalculation.calculateHSVTriangleOffsetPercentage(
                    saturation = initialColor.saturation(), value = initialColor.value(), size = colorPickerSize
                )
            )
        }

        val color by remember(hue, indicatorOffsetPercentage, colorPickerSize) {
            derivedStateOf {
                ColorCalculation.calculateHSVTriangleColor(
                    hue = hue, offsetPercentage = indicatorOffsetPercentage, size = colorPickerSize
                )
            }
        }

        LaunchedEffect(color) {
            updatedOnColorChange(color)
        }

        val vertices by remember(colorPickerSize) {
            derivedStateOf {
                val side = min(colorPickerSize.width, colorPickerSize.height)
                val radius = side / 2f
                Array(3) { index ->
                    val angle = Math.toRadians(120f * index - 90.0)
                    Offset(
                        x = colorPickerSize.width / 2f + (radius * cos(angle)).toFloat(),
                        y = colorPickerSize.height / 2f + (radius * sin(angle)).toFloat()
                    )
                }
            }
        }

        val trianglePath = remember(vertices) {
            Path().apply {
                moveTo(vertices[0].x, vertices[0].y)
                lineTo(vertices[1].x, vertices[1].y)
                lineTo(vertices[2].x, vertices[2].y)
                close()
            }
        }

        val triangleBounds = remember(trianglePath) { trianglePath.getBounds() }

        val backgroundGradient = remember(trianglePath) {
            Brush.horizontalGradient(
                colors = listOf(Color.Black, Color.White), startX = triangleBounds.left, endX = triangleBounds.right
            )
        }

        val foregroundGradient = remember(hue, trianglePath) {
            Brush.verticalGradient(
                colors = listOf(Color.hsv(hue, 1f, 1f), Color.Transparent),
                startY = triangleBounds.top,
                endY = triangleBounds.bottom
            )
        }

        val shape = remember(vertices) { TriangleShape(vertices = vertices) }

        ColorPickerComponent(modifier = Modifier.fillMaxSize().rotate(rotationDegrees).clip(shape),
            mapIndicatorOffset = { offset ->
                OffsetMapper.mapTriangularOffset(offset = offset, vertices = vertices)
            },
            indicatorOffsetPercentage = indicatorOffsetPercentage,
            onIndicatorOffsetPercentage = setIndicatorOffsetPercentage,
            indicatorContent = indicatorContent,
            content = {
                drawPath(path = trianglePath, brush = backgroundGradient)
                drawPath(path = trianglePath, brush = foregroundGradient)
            })
    }
}