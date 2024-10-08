package color

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ColorModelTest {
    @Test
    fun testRGBToHSL() {
        val rgb = ColorModel.RGB(255, 0, 0)
        val expectedHSL = ColorModel.HSL(0.0, 100.0, 50.0)
        assertEquals(expectedHSL, rgb.toHSL())

        val rgbGreen = ColorModel.RGB(0, 255, 0)
        val expectedHSLGreen = ColorModel.HSL(120.0, 100.0, 50.0)
        assertEquals(expectedHSLGreen, rgbGreen.toHSL())

        val rgbBlue = ColorModel.RGB(0, 0, 255)
        val expectedHSLBlue = ColorModel.HSL(240.0, 100.0, 50.0)
        assertEquals(expectedHSLBlue, rgbBlue.toHSL())
    }

    @Test
    fun testRGBToHSB() {
        val rgb = ColorModel.RGB(255, 0, 0)
        val expectedHSB = ColorModel.HSB(0.0, 100.0, 100.0)
        assertEquals(expectedHSB, rgb.toHSB())

        val rgbGreen = ColorModel.RGB(0, 255, 0)
        val expectedHSBGreen = ColorModel.HSB(120.0, 100.0, 100.0)
        assertEquals(expectedHSBGreen, rgbGreen.toHSB())

        val rgbBlue = ColorModel.RGB(0, 0, 255)
        val expectedHSBBlue = ColorModel.HSB(240.0, 100.0, 100.0)
        assertEquals(expectedHSBBlue, rgbBlue.toHSB())
    }

    @Test
    fun testRGBToCMYK() {
        val rgb = ColorModel.RGB(255, 0, 0)
        val expectedCMYK = ColorModel.CMYK(0, 100, 100, 0)
        assertEquals(expectedCMYK, rgb.toCMYK())

        val rgbGreen = ColorModel.RGB(0, 255, 0)
        val expectedCMYKGreen = ColorModel.CMYK(100, 0, 100, 0)
        assertEquals(expectedCMYKGreen, rgbGreen.toCMYK())

        val rgbBlue = ColorModel.RGB(0, 0, 255)
        val expectedCMYKBlue = ColorModel.CMYK(100, 100, 0, 0)
        assertEquals(expectedCMYKBlue, rgbBlue.toCMYK())
    }

    @Test
    fun testHSLToRGB() {
        val hsl = ColorModel.HSL(0.0, 100.0, 50.0)
        val expectedRGB = ColorModel.RGB(255, 0, 0)
        assertEquals(expectedRGB, hsl.toRGB())

        val hslGreen = ColorModel.HSL(120.0, 100.0, 50.0)
        val expectedRGBGreen = ColorModel.RGB(0, 255, 0)
        assertEquals(expectedRGBGreen, hslGreen.toRGB())

        val hslBlue = ColorModel.HSL(240.0, 100.0, 50.0)
        val expectedRGBBlue = ColorModel.RGB(0, 0, 255)
        assertEquals(expectedRGBBlue, hslBlue.toRGB())
    }

    @Test
    fun testHSBToRGB() {
        val hsb = ColorModel.HSB(0.0, 100.0, 100.0)
        val expectedRGB = ColorModel.RGB(255, 0, 0)
        assertEquals(expectedRGB, hsb.toRGB())

        val hsbGreen = ColorModel.HSB(120.0, 100.0, 100.0)
        val expectedRGBGreen = ColorModel.RGB(0, 255, 0)
        assertEquals(expectedRGBGreen, hsbGreen.toRGB())

        val hsbBlue = ColorModel.HSB(240.0, 100.0, 100.0)
        val expectedRGBBlue = ColorModel.RGB(0, 0, 255)
        assertEquals(expectedRGBBlue, hsbBlue.toRGB())
    }

    @Test
    fun testCMYKToRGB() {
        val cmyk = ColorModel.CMYK(0, 100, 100, 0)
        val expectedRGB = ColorModel.RGB(255, 0, 0)
        assertEquals(expectedRGB, cmyk.toRGB())

        val cmykGreen = ColorModel.CMYK(100, 0, 100, 0)
        val expectedRGBGreen = ColorModel.RGB(0, 255, 0)
        assertEquals(expectedRGBGreen, cmykGreen.toRGB())

        val cmykBlue = ColorModel.CMYK(100, 100, 0, 0)
        val expectedRGBBlue = ColorModel.RGB(0, 0, 255)
        assertEquals(expectedRGBBlue, cmykBlue.toRGB())
    }
}