package library.color

import androidx.compose.ui.graphics.Color

fun Color.hue(): Float {
    val r = red
    val g = green
    val b = blue

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

fun Color.saturation(): Float {
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)

    val delta = max - min

    return if (max == 0f) 0f else delta / max
}

fun Color.value() = maxOf(red, green, blue)

fun Color.lightness(): Float {
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)

    return (max + min) / 2
}