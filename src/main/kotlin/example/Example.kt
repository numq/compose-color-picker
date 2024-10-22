package example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import library.picker.CircularHSVColorPicker
import library.picker.RectangularColorPicker
import library.picker.WheelRectangleHSVColorPicker
import library.picker.WheelTriangleHSLColorPicker

@OptIn(ExperimentalStdlibApi::class)
fun main() = singleWindowApplication(title = "Color Picker") {
    val (backgroundColor, setBackgroundColor) = remember { mutableStateOf(Color.White) }

    val indicationColor = remember(backgroundColor) {
        if (backgroundColor.luminance() < .5f) Color.White else Color.Black
    }

    val (selectedColorPicker, setSelectedColorPicker) = remember {
        mutableStateOf<Pair<String, @Composable (Color) -> Unit>?>(null)
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().width(IntrinsicSize.Max).padding(4.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp, alignment = Alignment.CenterVertically)
        ) {
            val colorPickers by remember(backgroundColor) {
                derivedStateOf {
                    listOf<Pair<String, @Composable (Color) -> Unit>>(
                        "Circular HSV" to @Composable { color ->
                            CircularHSVColorPicker(
                                modifier = Modifier.weight(1f),
                                color = color,
                                onColorChange = setBackgroundColor
                            )
                        },
                        "Rectangular" to @Composable { color ->
                            RectangularColorPicker(
                                modifier = Modifier.weight(1f),
                                color = color,
                                onColorChange = setBackgroundColor
                            )
                        },
                        "Wheel rectangle" to @Composable { color ->
                            WheelRectangleHSVColorPicker(
                                modifier = Modifier.weight(1f),
                                isRotating = false,
                                color = color,
                                onColorChange = setBackgroundColor
                            )
                        },
                        "Wheel rectangle rotating" to @Composable { color ->
                            WheelRectangleHSVColorPicker(
                                modifier = Modifier.weight(1f),
                                isRotating = true,
                                color = color,
                                onColorChange = setBackgroundColor
                            )
                        },
                        "Wheel triangle" to @Composable { color ->
                            WheelTriangleHSLColorPicker(
                                modifier = Modifier.weight(1f),
                                isRotating = false,
                                color = color,
                                onColorChange = setBackgroundColor
                            )
                        },
                        "Wheel triangle rotating" to @Composable { color ->
                            WheelTriangleHSLColorPicker(
                                modifier = Modifier.weight(1f),
                                isRotating = true,
                                color = color,
                                onColorChange = setBackgroundColor
                            )
                        }
                    )
                }
            }

            colorPickers.forEach { colorPicker ->
                Card(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxSize().clickable {
                        setSelectedColorPicker(colorPicker)
                    }.padding(4.dp), contentAlignment = Alignment.CenterStart) {
                        Text(colorPicker.first)
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            selectedColorPicker?.let { (name, content) ->
                Scaffold(topBar = {
                    TopAppBar(title = {
                        Text(name)
                    })
                }) { paddingValues ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).background(color = backgroundColor)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = backgroundColor.toArgb().toHexString(),
                                color = indicationColor
                            )
                        }
                        content(backgroundColor)
                        RGBControls(
                            modifier = Modifier.weight(1f),
                            tint = indicationColor,
                            color = backgroundColor,
                            onColorChange = setBackgroundColor
                        )
                        HSVControls(
                            modifier = Modifier.weight(1f),
                            tint = indicationColor,
                            color = backgroundColor,
                            onColorChange = setBackgroundColor
                        )
                    }
                }
            }
        }
    }
}