package com.github.numq.picker.wheel

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.github.numq.offset.OffsetMapper
import com.github.numq.offset.OffsetPercentageCalculation
import com.github.numq.picker.ColorPickerComponent
import com.github.numq.shape.WheelShape
import kotlin.math.atan2

@Composable
fun WheelColorPicker(
    modifier: Modifier,
    thicknessPercentage: Float,
    indicatorContent: (DrawScope.(indicatorOffset: Offset) -> Unit),
    hue: Float,
    onHueChange: (Float) -> Unit,
    content: @Composable BoxWithConstraintsScope.(diameter: Float) -> Unit,
) {
    require(hue in 0f..360f) { "Hue should be within 0f..360f" }

    val updatedOnHueChange by rememberUpdatedState(onHueChange)

    val indicatorOffsetPercentage by remember(hue) {
        derivedStateOf {
            OffsetPercentageCalculation.calculateWheelOffsetPercentage(hue)
        }
    }

    val gradient = remember {
        Brush.sweepGradient(colors = List(360) { angle ->
            Color.hsv(hue = angle.toFloat(), saturation = 1f, value = 1f)
        })
    }

    BoxWithConstraints(modifier = modifier.aspectRatio(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
        val colorPickerSize = remember(maxWidth, maxHeight) {
            Size(maxWidth.value, maxHeight.value)
        }

        val outerDiameter = remember(colorPickerSize) {
            minOf(colorPickerSize.width, colorPickerSize.height)
        }

        val outerRadius = remember(outerDiameter) {
            outerDiameter / 2f
        }

        val thickness = remember(outerDiameter, thicknessPercentage) {
            outerDiameter * thicknessPercentage
        }

        val innerDiameter = remember(outerDiameter, thickness) {
            outerDiameter - thickness * 2f
        }

        val innerRadius = remember(innerDiameter) {
            innerDiameter / 2f
        }

        val path = remember(colorPickerSize, outerRadius, innerRadius) {
            Path().apply {
                op(Path().apply {
                    addOval(
                        Rect(
                            colorPickerSize.center - Offset(outerRadius, outerRadius),
                            Size(outerRadius * 2f, outerRadius * 2f)
                        )
                    )
                    close()
                }, Path().apply {
                    addOval(
                        Rect(
                            colorPickerSize.center - Offset(innerRadius, innerRadius),
                            Size(innerRadius * 2f, innerRadius * 2f)
                        )
                    )
                    close()
                }, PathOperation.Difference
                )

                close()
            }
        }

        val shape = remember(outerDiameter, innerDiameter) {
            WheelShape(outerDiameter = outerDiameter, innerDiameter = innerDiameter)
        }

        ColorPickerComponent(modifier = Modifier.fillMaxSize().rotate(-90f).clip(shape),
            mapIndicatorOffset = { offset ->
                OffsetMapper.mapCircularOffset(
                    outerRadius = outerRadius,
                    innerRadius = innerRadius,
                    offset = offset,
                    center = colorPickerSize.center
                )
            },
            indicatorOffsetPercentage = indicatorOffsetPercentage,
            onIndicatorOffsetPercentage = { (offsetX, offsetY) ->
                Offset(0.5f, 0.5f).run {
                    val angleInRadians = atan2(offsetY - y, offsetX - x)
                    var angleInDegrees = Math.toDegrees(angleInRadians.toDouble()).toFloat()

                    if (angleInDegrees < 0) {
                        angleInDegrees += 360f
                    }

                    updatedOnHueChange(angleInDegrees)
                }
            },
            indicatorContent = indicatorContent,
            content = {
                drawPath(path = path, brush = gradient)
            })

        content(innerDiameter)
    }
}