<h1 align="center">Compose Color Picker</h1>

<div align="center" style="display: grid; justify-content: center;">

|                                                                  🌟                                                                   |                  Support this project                   |               
|:-------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------:|
|  <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/bitcoin.png" alt="Bitcoin (BTC)" width="32"/>  | <code>bc1qs6qq0fkqqhp4whwq8u8zc5egprakvqxewr5pmx</code> | 
| <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/ethereum.png" alt="Ethereum (ETH)" width="32"/> | <code>0x3147bEE3179Df0f6a0852044BFe3C59086072e12</code> |
|  <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/tether.png" alt="USDT (TRC-20)" width="32"/>   |     <code>TKznmR65yhPt5qmYCML4tNSWFeeUkgYSEV</code>     |

</div>

<br>

<p align="center">A Color Picker library for Jetpack Compose</p>

<br>

<p align="center"><img src="media/demo.gif" alt="demo"/></p>

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
