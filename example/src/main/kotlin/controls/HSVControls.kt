package controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import slider.LabeledSlider

@Composable
fun HSVControls(
    modifier: Modifier,
    tint: Color,
    hue: Float,
    onHueChange: (Float) -> Unit,
    saturation: Float,
    onSaturationChange: (Float) -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("HSV", color = tint)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabeledSlider(
                modifier = Modifier.weight(1f),
                tint = tint,
                label = "Hue",
                value = hue,
                valueRange = 0f..360f,
                onValueChange = onHueChange
            )
            LabeledSlider(
                modifier = Modifier.weight(1f),
                tint = tint,
                label = "Saturation",
                value = saturation,
                onValueChange = onSaturationChange
            )
            LabeledSlider(
                modifier = Modifier.weight(1f),
                tint = tint,
                label = "Value",
                value = value,
                onValueChange = onValueChange
            )
        }
    }
}