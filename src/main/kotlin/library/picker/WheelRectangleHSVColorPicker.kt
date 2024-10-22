package library.picker

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import library.color.hue
import library.color.saturation
import library.color.value
import library.picker.core.ColorPickerConstant
import library.picker.core.WheelColorPicker
import kotlin.math.sqrt

@Composable
fun WheelRectangleHSVColorPicker(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    wheelThicknessPercentage: Float = ColorPickerConstant.DEFAULT_THICKNESS_PERCENTAGE,
    isRotating: Boolean,
    color: Color,
    onColorChange: (Color) -> Unit,
) {
    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    val (wheelHue, setWheelHue) = remember { mutableStateOf(color.hue()) }

    val (saturation, setSaturation) = remember { mutableStateOf(color.saturation()) }

    val (value, setValue) = remember { mutableStateOf(color.saturation()) }

    WheelColorPicker(
        modifier = modifier.aspectRatio(1f),
        thicknessPercentage = wheelThicknessPercentage,
        hue = wheelHue,
        onHueChange = { changedWheelHue ->
            updatedOnColorChange(
                Color.hsv(
                    hue = changedWheelHue.also(setWheelHue),
                    saturation = saturation,
                    value = value
                )
            )
        },
        content = { diameter ->
            RectangleHSVColorPicker(
                modifier = Modifier.size((diameter / sqrt(2f)).dp.let { side -> DpSize(side, side) }).composed {
                    if (isRotating) rotate(-90f) else this
                },
                indicatorThickness = indicatorThickness,
                indicatorRadius = indicatorRadius,
                isRotating = isRotating,
                color = color,
                onColorChange = { changedColor ->
                    updatedOnColorChange(
                        Color.hsv(
                            hue = wheelHue,
                            saturation = changedColor.saturation().also(setSaturation),
                            value = changedColor.value().also(setValue)
                        )
                    )
                }
            )
        }
    )
}