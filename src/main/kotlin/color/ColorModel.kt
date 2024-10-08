package color

import kotlin.math.abs
import kotlin.math.roundToInt

sealed interface ColorModel {
    val alpha: Int
    fun toRGB(): RGB
    fun toHSL(): HSL
    fun toHSB(): HSB
    fun toCMYK(): CMYK

    data class RGB(
        val red: Int,
        val green: Int,
        val blue: Int,
        override val alpha: Int = 255,
    ) : ColorModel {
        init {
            require(red in 0..255) { "Red must be in the range 0..255" }
            require(green in 0..255) { "Green must be in the range 0..255" }
            require(blue in 0..255) { "Blue must be in the range 0..255" }
            require(alpha in 0..255) { "Alpha must be in the range 0..255" }
        }

        override fun toRGB(): RGB = this

        override fun toHSL(): HSL {
            val r = red / 255.0
            val g = green / 255.0
            val b = blue / 255.0

            val max = maxOf(r, g, b)
            val min = minOf(r, g, b)
            val delta = max - min

            val lightness = (max + min) / 2.0
            val saturation = if (delta == 0.0) 0.0 else delta / (1 - abs(2 * lightness - 1))
            val roundedLightness = (lightness * 100).roundToInt() / 100.0

            val hue = when {
                delta == 0.0 -> 0.0

                max == r -> 60 * (((g - b) / delta).let { if (it < 0) it + 6 else it })

                max == g -> 60 * ((b - r) / delta + 2)

                else -> 60 * ((r - g) / delta + 4)
            }

            return HSL(hue = hue, saturation = saturation, lightness = roundedLightness, alpha = alpha)
        }

        override fun toHSB(): HSB {
            val r = red / 255.0
            val g = green / 255.0
            val b = blue / 255.0

            val max = maxOf(r, g, b)
            val min = minOf(r, g, b)
            val delta = max - min

            val brightness = max * 100
            val saturation = if (max == 0.0) 0.0 else delta / max

            val hue = when {
                delta == 0.0 -> 0.0

                max == r -> 60 * (((g - b) / delta).let { if (it < 0) it + 6 else it })

                max == g -> 60 * ((b - r) / delta + 2)

                else -> 60 * ((r - g) / delta + 4)
            }

            return HSB(hue = hue, saturation = saturation, brightness = brightness, alpha = alpha)
        }


        override fun toCMYK(): CMYK {
            val r = red / 255.0
            val g = green / 255.0
            val b = blue / 255.0

            val k = 1 - maxOf(r, g, b)
            val c = if (k < 1.0) (1 - r - k) / (1 - k) else 0.0
            val m = if (k < 1.0) (1 - g - k) / (1 - k) else 0.0
            val y = if (k < 1.0) (1 - b - k) / (1 - k) else 0.0

            return CMYK(
                cyan = (c * 100).roundToInt(),
                magenta = (m * 100).roundToInt(),
                yellow = (y * 100).roundToInt(),
                black = (k * 100).roundToInt(),
                alpha = alpha
            )
        }
    }

    data class HSL(
        val hue: Double,
        val saturation: Double,
        val lightness: Double,
        override val alpha: Int = 255,
    ) : ColorModel {
        init {
            require(hue in 0.0..360.0) { "Hue must be in the range 0.0..360.0" }
            require(saturation in 0.0..100.0) { "Saturation must be in the range 0.0..100.0" }
            require(lightness in 0.0..100.0) { "Lightness must be in the range 0.0..100.0" }
            require(alpha in 0..255) { "Alpha must be in the range 0..255" }
        }

        override fun toRGB(): RGB {
            val c = (1 - abs(2 * lightness / 100 - 1)) * (saturation / 100)
            val x = c * (1 - abs((hue / 60) % 2 - 1))
            val m = lightness / 100 - c / 2

            val (r1, g1, b1) = when (hue) {
                in 0.0..60.0 -> Triple(c, x, 0.0)

                in 60.0..120.0 -> Triple(x, c, 0.0)

                in 120.0..180.0 -> Triple(0.0, c, x)

                in 180.0..240.0 -> Triple(0.0, x, c)

                in 240.0..300.0 -> Triple(x, 0.0, c)

                else -> Triple(c, 0.0, x)
            }

            return RGB(
                red = ((r1 + m) * 255).roundToInt().coerceIn(0, 255),
                green = ((g1 + m) * 255).roundToInt().coerceIn(0, 255),
                blue = ((b1 + m) * 255).roundToInt().coerceIn(0, 255),
                alpha = alpha
            )
        }

        override fun toHSL(): HSL = this

        override fun toHSB(): HSB = toRGB().toHSB()

        override fun toCMYK(): CMYK = toRGB().toCMYK()
    }

    data class HSB(
        val hue: Double,
        val saturation: Double,
        val brightness: Double,
        override val alpha: Int = 255,
    ) : ColorModel {
        init {
            require(hue in 0.0..360.0) { "Hue must be in the range 0.0..360.0" }
            require(saturation in 0.0..100.0) { "Saturation must be in the range 0.0..100.0" }
            require(brightness in 0.0..100.0) { "Brightness must be in the range 0.0..100.0" }
            require(alpha in 0..255) { "Alpha must be in the range 0..255" }
        }

        override fun toRGB(): RGB {
            val c = (brightness / 100) * (saturation / 100)
            val x = c * (1 - abs((hue / 60) % 2 - 1))
            val m = (brightness / 100) - c

            val (r1, g1, b1) = when (hue) {
                in 0.0..60.0 -> Triple(c, x, 0.0)

                in 60.0..120.0 -> Triple(x, c, 0.0)

                in 120.0..180.0 -> Triple(0.0, c, x)

                in 180.0..240.0 -> Triple(0.0, x, c)

                in 240.0..300.0 -> Triple(x, 0.0, c)

                else -> Triple(c, 0.0, x)
            }

            return RGB(
                red = ((r1 + m) * 255).roundToInt().coerceIn(0, 255),
                green = ((g1 + m) * 255).roundToInt().coerceIn(0, 255),
                blue = ((b1 + m) * 255).roundToInt().coerceIn(0, 255),
                alpha = alpha
            )
        }

        override fun toHSL(): HSL = toRGB().toHSL()

        override fun toHSB(): HSB = this

        override fun toCMYK(): CMYK = toRGB().toCMYK()
    }

    data class CMYK(
        val cyan: Int,
        val magenta: Int,
        val yellow: Int,
        val black: Int,
        override val alpha: Int = 255,
    ) : ColorModel {
        init {
            require(cyan in 0..100) { "Cyan must be in the range 0..100" }
            require(magenta in 0..100) { "Magenta must be in the range 0..100" }
            require(yellow in 0..100) { "Yellow must be in the range 0..100" }
            require(black in 0..100) { "Black must be in the range 0..100" }
            require(alpha in 0..255) { "Alpha must be in the range 0..255" }
        }

        override fun toRGB(): RGB {
            val c = cyan / 100.0
            val m = magenta / 100.0
            val y = yellow / 100.0
            val k = black / 100.0

            val r = 255 * (1 - c) * (1 - k)
            val g = 255 * (1 - m) * (1 - k)
            val b = 255 * (1 - y) * (1 - k)

            return RGB(
                red = r.roundToInt().coerceIn(0, 255),
                green = g.roundToInt().coerceIn(0, 255),
                blue = b.roundToInt().coerceIn(0, 255),
                alpha = alpha
            )
        }

        override fun toHSL(): HSL = toRGB().toHSL()

        override fun toHSB(): HSB = toRGB().toHSB()

        override fun toCMYK(): CMYK = this
    }
}