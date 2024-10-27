package picker

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import color.hue
import color.saturation
import color.value
import picker.core.ColorPickerConstant
import picker.core.WheelColorPicker

@Composable
fun WheelTriangleHSLColorPicker(
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
    
    WheelColorPicker(
        modifier = modifier.aspectRatio(1f),
        thicknessPercentage = wheelThicknessPercentage,
        hue = wheelHue,
        onHueChange = setWheelHue,
        content = { diameter ->
            TriangleHSVColorPicker(
                modifier = Modifier.size(diameter.dp).composed {
                    if (isRotating) rotate(-90f) else this
                },
                indicatorThickness = indicatorThickness,
                indicatorRadius = indicatorRadius,
                isRotating = isRotating,
                hue = wheelHue,
                color = Color.hsv(hue = wheelHue, saturation = color.saturation(), value = color.value()),
                onColorChange = { changedColor ->
                    updatedOnColorChange(
                        Color.hsv(hue = wheelHue, saturation = changedColor.saturation(), value = changedColor.value())
                    )
                }
            )
        }
    )
}
