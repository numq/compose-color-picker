package com.github.numq.picker.wheel.rectangle

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.numq.color.hue
import com.github.numq.color.saturation
import com.github.numq.color.value
import com.github.numq.picker.ColorPickerConstant
import com.github.numq.picker.wheel.WheelColorPicker
import kotlin.math.sqrt

@Composable
fun WheelRectangleColorPickerHSVLazy(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    wheelThicknessPercentage: Float = ColorPickerConstant.DEFAULT_THICKNESS_PERCENTAGE,
    wheelIndicatorContent: (DrawScope.(indicatorOffset: Offset) -> Unit)? = null,
    isRotating: Boolean,
    initialColor: Color,
    onColorChange: (Color) -> Unit,
) {
    require(initialColor.isSpecified) { "Initial color should be specified" }

    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    val (hue, setHue) = remember { mutableStateOf(initialColor.hue()) }

    val (saturation, setSaturation) = remember { mutableStateOf(initialColor.saturation()) }

    val (value, setValue) = remember { mutableStateOf(initialColor.value()) }

    val color by remember(hue, saturation, value) {
        derivedStateOf {
            Color.hsv(hue = hue, saturation = saturation, value = value)
        }
    }

    LaunchedEffect(color) {
        updatedOnColorChange(color)
    }

    WheelColorPicker(
        modifier = modifier.aspectRatio(1f),
        thicknessPercentage = wheelThicknessPercentage,
        indicatorContent = { offset ->
            wheelIndicatorContent?.invoke(this, offset) ?: rotate(hue) {
                rotate(-90f) {
                    drawLine(
                        color = Color.White,
                        start = size.center,
                        end = size.center.plus(Offset(0f, size.height)),
                        strokeWidth = 1f
                    )
                }
            }
        },
        hue = hue,
        onHueChange = setHue,
        content = { diameter ->
            RectangleColorPickerSVLazy(modifier = Modifier.size((diameter / sqrt(2f)).dp.let { side ->
                DpSize(side, side)
            }).composed {
                if (isRotating) rotate(-90f) else this
            },
                isRotating = isRotating,
                indicatorContent = { offset ->
                    drawCircle(
                        color = if (saturation > .5f) Color.Black else Color.White,
                        radius = indicatorRadius,
                        center = offset,
                        style = Stroke(width = indicatorThickness)
                    )
                },
                hue = hue,
                initialColor = initialColor,
                onColorChange = { changedColor ->
                    setSaturation(changedColor.saturation())
                    setValue(changedColor.value())
                }
            )
        }
    )
}