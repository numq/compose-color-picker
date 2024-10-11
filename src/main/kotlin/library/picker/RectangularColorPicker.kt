package library.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import library.picker.core.ColorPickerComponent

@Composable
fun RectangularColorPicker(
    modifier: Modifier,
    indicatorThickness: Float,
    indicatorRadius: Float,
    onColorChange: (Color) -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ColorPickerComponent(
            modifier = Modifier.aspectRatio(1f).fillMaxSize().clipToBounds(),
            indicatorThickness = indicatorThickness,
            indicatorRadius = indicatorRadius,
            onColorChange = onColorChange
        ) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = List(360) { angle ->
                        Color.hsv(angle.toFloat(), 1f, 1f)
                    }
                )
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Transparent, Color.Black)
                )
            )
        }
    }
}