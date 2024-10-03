import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap

internal fun colorForPosition(pixelMap: PixelMap, position: Offset) = position.runCatching {
    pixelMap[
        (position.x.coerceIn(0f, pixelMap.width.toFloat() - 1f)).toInt(),
        (position.y.coerceIn(0f, pixelMap.height.toFloat() - 1f)).toInt()
    ]
}.getOrNull() ?: Color.Unspecified