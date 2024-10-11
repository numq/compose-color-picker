package library.picker

import library.shape.CircularShape
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.min
import library.mapper.OffsetMapper.mapCircularOffset
import library.picker.core.ColorPickerComponent

@Composable
fun CircularColorPicker(
    modifier: Modifier,
    indicatorThickness: Float,
    indicatorRadius: Float,
    onColorChange: (Color) -> Unit,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val diameter = remember(maxWidth, maxHeight) {
            min(maxWidth, maxHeight).value
        }

        val radius = remember(diameter) {
            diameter / 2f
        }

        val shape = remember(diameter) { CircularShape(diameter = diameter) }

        ColorPickerComponent(modifier = Modifier.aspectRatio(1f).fillMaxSize().clip(shape),
            indicatorThickness = indicatorThickness,
            indicatorRadius = indicatorRadius,
            onColorChange = onColorChange,
            mapIndicatorOffset = { offset ->
                mapCircularOffset(offset = offset, outerRadius = radius, innerRadius = 0f).getOrNull()
            }) {
            rotate(-90f) {
                drawRect(brush = Brush.sweepGradient(colors = List(360) { angle ->
                    Color.hsv(angle.toFloat(), 1f, 1f)
                }))
            }

            drawCircle(brush = Brush.radialGradient(colors = listOf(Color.White, Color.Transparent)))
        }
    }
}