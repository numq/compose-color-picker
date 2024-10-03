import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap

internal fun colorForPosition(pixelMap: PixelMap, position: Offset) = position.runCatching {
    pixelMap[x.toInt(), y.toInt()]
}.getOrNull() ?: Color.Unspecified