package controls

import slider.LabeledSlider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RGBControls(
    modifier: Modifier,
    tint: Color,
    color: Color,
    onColorChange: (Color) -> Unit,
) {
    val updatedOnColorChange by rememberUpdatedState(onColorChange)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "RGB", color = tint)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabeledSlider(modifier = Modifier.weight(1f),
                tint = tint,
                label = "Red",
                value = color.red,
                onValueChange = { red ->
                    updatedOnColorChange(color.copy(red = red))
                }
            )
            LabeledSlider(modifier = Modifier.weight(1f),
                tint = tint,
                label = "Green",
                value = color.green,
                onValueChange = { green ->
                    updatedOnColorChange(color.copy(green = green))
                }
            )
            LabeledSlider(
                modifier = Modifier.weight(1f),
                tint = tint,
                label = "Blue",
                value = color.blue,
                onValueChange = { blue ->
                    updatedOnColorChange(color.copy(blue = blue))
                }
            )
        }
    }
}