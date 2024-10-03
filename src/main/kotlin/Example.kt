import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication

@OptIn(ExperimentalStdlibApi::class)
fun main() = singleWindowApplication(title = "Color Picker") {
    var backgroundColor by remember { mutableStateOf(Color.Unspecified) }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = backgroundColor.toArgb().toHexString(),
                color = if (backgroundColor.luminance() > .5f) Color.Black else Color.White,
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SimpleCircularColorPicker(modifier = Modifier.weight(1f)) { color ->
                backgroundColor = color
            }
            WheelColorPicker(modifier = Modifier.weight(1f)) { color ->
                backgroundColor = color
            }
            SimpleSquareColorPicker(modifier = Modifier.weight(1f)) { color ->
                backgroundColor = color
            }
            SliderColorPicker(modifier = Modifier.weight(1f)) { color ->
                backgroundColor = color
            }
        }
    }
}