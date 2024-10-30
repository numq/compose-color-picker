<a href="https://www.buymeacoffee.com/numq"><img src="https://img.buymeacoffee.com/button-api/?text=Buy me a one way ticket&emoji=✈️&slug=numq&button_colour=5F7FFF&font_colour=ffffff&font_family=Inter&outline_colour=000000&coffee_colour=FFDD00" /></a>

# Compose Color Picker

A Color Picker library for Jetpack Compose

![Demonstration](media/demo.gif)

## Interactive or reactive

> Changes in the argument are reflected on the indicator

- [CircularColorPicker](library/src/main/kotlin/picker/circular/CircularColorPicker.kt)
- [RectangularColorPicker](library/src/main/kotlin/picker/rectangular/RectangularColorPicker.kt)
- [RectangleColorPickerSV](library/src/main/kotlin/picker/wheel/rectangle/RectangleColorPickerSV.kt)
- [WheelRectangleColorPicker](library/src/main/kotlin/picker/wheel/rectangle/WheelRectangleColorPickerHSV.kt)
- [TriangleColorPickerSV](library/src/main/kotlin/picker/wheel/triangle/TriangleColorPickerSV.kt)
- [WheelTriangleColorPickerHSV](library/src/main/kotlin/picker/wheel/triangle/WheelTriangleColorPickerHSV.kt)

## Lazy

> Initial color is passed as an argument

- [CircularColorPickerLazy](library/src/main/kotlin/picker/circular/CircularColorPickerLazy.kt)
- [RectangularColorPickerLazy](library/src/main/kotlin/picker/rectangular/RectangularColorPickerLazy.kt)
- [RectangleColorPickerSVLazy](library/src/main/kotlin/picker/wheel/rectangle/RectangleColorPickerSVLazy.kt)
- [WheelRectangleColorPickerLazy](library/src/main/kotlin/picker/wheel/rectangle/WheelRectangleColorPickerHSVLazy.kt)
- [TriangleColorPickerSVLazy](library/src/main/kotlin/picker/wheel/triangle/TriangleColorPickerSVLazy.kt)
- [WheelTriangleColorPickerHSVLazy](library/src/main/kotlin/picker/wheel/triangle/WheelTriangleColorPickerHSVLazy.kt)

## Installation

```
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.numq:compose-color-picker:1.0.2")
}
```