package example

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import library.complex.slider.SliderColorPicker
import library.complex.slider.SliderColorPickerAsync
import library.complex.wheel.WheelColorPicker
import library.complex.wheel.WheelColorPickerAsync
import library.simple.circular.SimpleCircularColorPicker
import library.simple.circular.SimpleCircularColorPickerAsync
import library.simple.square.SimpleSquareColorPicker
import library.simple.square.SimpleSquareColorPickerAsync

fun main() = singleWindowApplication(title = "Color Picker") {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f).padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorPickerItem(
                modifier = Modifier.weight(1f),
                contentDefault = { pickColor ->
                    SimpleCircularColorPicker(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                },
                contentAsync = { pickColor ->
                    SimpleCircularColorPickerAsync(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            ColorPickerItem(
                modifier = Modifier.weight(1f),
                contentDefault = { pickColor ->
                    SimpleSquareColorPicker(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                },
                contentAsync = { pickColor ->
                    SimpleSquareColorPickerAsync(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                }
            )
        }
        Row(
            modifier = Modifier.weight(1f).padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorPickerItem(
                modifier = Modifier.weight(1f),
                contentDefault = { pickColor ->
                    SliderColorPicker(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                },
                contentAsync = { pickColor ->
                    SliderColorPickerAsync(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            ColorPickerItem(
                modifier = Modifier.weight(1f),
                contentDefault = { pickColor ->
                    WheelColorPicker(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                },
                contentAsync = { pickColor ->
                    WheelColorPickerAsync(modifier = Modifier.fillMaxSize(), pickColor = pickColor)
                }
            )
        }
    }
}