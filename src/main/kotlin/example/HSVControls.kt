package example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import library.color.hue
import library.color.saturation
import library.color.value

@Composable
fun HSVControls(
    modifier: Modifier,
    tint: Color,
    color: Color,
    onColorChange: (Color) -> Unit,
) {
    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    val colorHue by remember(color) {
        derivedStateOf {
            color.hue()
        }
    }

    val colorSaturation by remember(color) {
        derivedStateOf {
            color.saturation()
        }
    }

    val colorValue by remember(color) {
        derivedStateOf {
            color.value()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("HSV")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabeledSlider(
                modifier = Modifier.weight(1f),
                tint = tint,
                label = "Hue",
                value = colorHue,
                valueRange = 0f..360f,
                onValueChange = { hue ->
                    updatedOnColorChange(
                        Color.hsv(
                            hue = hue,
                            saturation = colorSaturation,
                            value = colorValue
                        )
                    )
                }
            )
            LabeledSlider(
                modifier = Modifier.weight(1f),
                tint = tint,
                label = "Saturation",
                value = colorSaturation,
                onValueChange = { saturation ->
                    updatedOnColorChange(
                        Color.hsv(
                            hue = colorHue,
                            saturation = saturation,
                            value = colorValue
                        )
                    )
                }
            )
            LabeledSlider(
                modifier = Modifier.weight(1f),
                tint = tint,
                label = "Value",
                value = colorValue,
                onValueChange = { value ->
                    updatedOnColorChange(
                        Color.hsv(
                            hue = colorHue,
                            saturation = colorSaturation,
                            value = value
                        )
                    )
                }
            )
        }
    }
}