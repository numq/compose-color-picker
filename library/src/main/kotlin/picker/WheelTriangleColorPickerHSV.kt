package picker

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import picker.core.ColorPickerConstant
import picker.core.WheelColorPicker

@Composable
fun WheelTriangleColorPickerHSV(
    modifier: Modifier,
    indicatorThickness: Float = ColorPickerConstant.DEFAULT_INDICATOR_THICKNESS,
    indicatorRadius: Float = ColorPickerConstant.DEFAULT_INDICATOR_RADIUS,
    wheelThicknessPercentage: Float = ColorPickerConstant.DEFAULT_THICKNESS_PERCENTAGE,
    isRotating: Boolean,
    hue: Float,
    onHueChange: (Float) -> Unit,
    saturation: Float,
    onSaturationChange: (Float) -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    require(hue in 0f..360f) { "Hue should be within 0f..360f" }

    require(saturation in 0f..1f) { "Saturation should be within 0f..1f" }

    require(value in 0f..1f) { "Value should be within 0f..1f" }

    val updatedOnHueChange by rememberUpdatedState(onHueChange)

    val updatedOnSaturationChange by rememberUpdatedState(onSaturationChange)

    val updatedOnValueChange by rememberUpdatedState(onValueChange)

    WheelColorPicker(
        modifier = modifier.aspectRatio(1f),
        thicknessPercentage = wheelThicknessPercentage,
        hue = hue,
        onHueChange = updatedOnHueChange,
        content = { diameter ->
            TriangleHSVColorPicker(
                modifier = Modifier.size(diameter.dp).composed {
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
                saturation = saturation,
                onSaturationChange = updatedOnSaturationChange,
                value = value,
                onValueChange = updatedOnValueChange
            )
        }
    )
}
