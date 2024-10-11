package example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import library.picker.core.ColorPicker

@OptIn(ExperimentalStdlibApi::class)
fun main() = singleWindowApplication(title = "Color Picker") {
    val (backgroundColor, setBackgroundColor) = remember { mutableStateOf(Color.Unspecified) }

    Column(
        modifier = Modifier.fillMaxSize().background(color = backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = backgroundColor.toArgb().toHexString(),
                color = if (backgroundColor.luminance() < .5f) Color.White else Color.Black
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val colorPickerModifier = Modifier.weight(1f)

            val colorPickers = remember(colorPickerModifier) {
                listOf(
                    ColorPicker.Circular(modifier = colorPickerModifier, onColorChange = setBackgroundColor),
                    ColorPicker.Rectangular(modifier = colorPickerModifier, onColorChange = setBackgroundColor),
                    ColorPicker.Wheel.Triangular(
                        modifier = colorPickerModifier,
                        isRotating = false,
                        onColorChange = setBackgroundColor
                    ),
                    ColorPicker.Wheel.Triangular(
                        modifier = colorPickerModifier,
                        isRotating = true,
                        onColorChange = setBackgroundColor
                    ),
                    ColorPicker.Wheel.Rectangular(
                        modifier = colorPickerModifier,
                        isRotating = false,
                        onColorChange = setBackgroundColor
                    ),
                    ColorPicker.Wheel.Rectangular(
                        modifier = colorPickerModifier,
                        isRotating = true,
                        onColorChange = setBackgroundColor
                    ),
                )
            }

            colorPickers.forEach { colorPicker ->
                colorPicker.content()
            }
        }
    }
}