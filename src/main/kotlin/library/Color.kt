package library

import androidx.compose.ui.graphics.Color

fun Color.hue(): Float {
    val r = red / 255f
    val g = green / 255f
    val b = blue / 255f

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)

    val delta = max - min

    return when {
        delta == 0f -> 0f

        max == r -> (60 * ((g - b) / delta) + 360) % 360

        max == g -> (60 * ((b - r) / delta) + 120) % 360

        else -> (60 * ((r - g) / delta) + 240) % 360
    }
}