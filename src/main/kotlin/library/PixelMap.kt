package library

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap

internal fun colorForPosition(pixelMap: PixelMap, position: Offset) = position.runCatching {
    pixelMap[x.toInt().coerceAtMost(pixelMap.width - 1), y.toInt().coerceAtMost(pixelMap.height - 1)]
}.getOrNull() ?: Color.Unspecified