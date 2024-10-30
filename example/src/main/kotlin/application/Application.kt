package application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import color.hue
import color.saturation
import color.value
import controls.HSVControls
import controls.RGBControls
import picker.circular.CircularColorPicker
import picker.circular.CircularColorPickerLazy
import picker.rectangular.RectangularColorPicker
import picker.rectangular.RectangularColorPickerLazy
import picker.wheel.rectangle.WheelRectangleColorPickerHSV
import picker.wheel.rectangle.WheelRectangleColorPickerHSVLazy
import picker.wheel.triangle.WheelTriangleColorPickerHSV
import picker.wheel.triangle.WheelTriangleColorPickerHSVLazy

fun main() = singleWindowApplication(
    title = "Color Picker",
    state = WindowState(size = DpSize(width = 768.dp, height = 768.dp))
) {
    val initialColor = Color.White

    val (hue, setHue) = remember {
        mutableStateOf(initialColor.hue())
    }

    val (saturation, setSaturation) = remember {
        mutableStateOf(initialColor.saturation())
    }

    val (value, setValue) = remember {
        mutableStateOf(initialColor.value())
    }

    val backgroundColor by remember(hue, saturation, value) {
        derivedStateOf {
            Color.hsv(hue = hue, saturation = saturation, value = value)
        }
    }

    val indicationColor = remember(backgroundColor) {
        if (backgroundColor.luminance() > .5f) Color.Black else Color.White
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Interactive color pickers", modifier = Modifier.padding(4.dp), color = indicationColor)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularColorPicker(
                    modifier = Modifier.weight(1f),
                    color = backgroundColor,
                    onColorChange = { color ->
                        setHue(color.hue())
                        setSaturation(color.saturation())
                        setValue(color.value())
                    }
                )
                RectangularColorPicker(
                    modifier = Modifier.weight(1f),
                    color = backgroundColor,
                    onColorChange = { color ->
                        setHue(color.hue())
                        setSaturation(color.saturation())
                        setValue(color.value())
                    }
                )
                WheelRectangleColorPickerHSV(
                    modifier = Modifier.weight(1f),
                    isRotating = true,
                    hue = hue,
                    onHueChange = setHue,
                    saturation = saturation,
                    onSaturationChange = setSaturation,
                    value = value,
                    onValueChange = setValue,
                )
                WheelTriangleColorPickerHSV(
                    modifier = Modifier.weight(1f),
                    isRotating = true,
                    hue = hue,
                    onHueChange = setHue,
                    saturation = saturation,
                    onSaturationChange = setSaturation,
                    value = value,
                    onValueChange = setValue,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Lazy color pickers", modifier = Modifier.padding(4.dp), color = indicationColor)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularColorPickerLazy(
                    modifier = Modifier.weight(1f),
                    initialColor = backgroundColor,
                    onColorChange = { color ->
                        setHue(color.hue())
                        setSaturation(color.saturation())
                        setValue(color.value())
                    }
                )
                RectangularColorPickerLazy(
                    modifier = Modifier.weight(1f),
                    initialColor = backgroundColor,
                    onColorChange = { color ->
                        setHue(color.hue())
                        setSaturation(color.saturation())
                        setValue(color.value())
                    }
                )
                WheelRectangleColorPickerHSVLazy(
                    modifier = Modifier.weight(1f),
                    isRotating = true,
                    initialColor = backgroundColor,
                    onColorChange = { color ->
                        setHue(color.hue())
                        setSaturation(color.saturation())
                        setValue(color.value())
                    }
                )
                WheelTriangleColorPickerHSVLazy(
                    modifier = Modifier.weight(1f),
                    isRotating = true,
                    initialColor = backgroundColor,
                    onColorChange = { color ->
                        setHue(color.hue())
                        setSaturation(color.saturation())
                        setValue(color.value())
                    }
                )
            }
        }
        RGBControls(
            modifier = Modifier.fillMaxWidth(),
            tint = indicationColor,
            color = backgroundColor,
            onColorChange = { changedColor ->
                setHue(changedColor.hue())
                setSaturation(changedColor.saturation())
                setValue(changedColor.value())
            }
        )
        HSVControls(
            modifier = Modifier.fillMaxWidth(),
            tint = indicationColor,
            hue = hue,
            onHueChange = setHue,
            saturation = saturation,
            onSaturationChange = setSaturation,
            value = value,
            onValueChange = setValue
        )
    }
}