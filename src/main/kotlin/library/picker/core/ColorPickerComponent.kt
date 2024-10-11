package library.picker.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import library.colorForPosition
import library.createBitmap

@Composable
fun ColorPickerComponent(
    modifier: Modifier,
    indicatorThickness: Float,
    indicatorRadius: Float,
    onColorChange: ((Color) -> Unit)? = null,
    mapIndicatorOffset: (PointerInputScope.(Offset) -> Offset?)? = null,
    indicatorContent: DrawScope.(Offset) -> Unit = { indicatorOffset ->
        drawCircle(
            color = Color.Black,
            radius = indicatorRadius / 2f,
            center = indicatorOffset,
            style = Stroke(width = indicatorThickness)
        )
        drawCircle(
            color = Color.White,
            radius = indicatorRadius,
            center = indicatorOffset,
            style = Stroke(width = indicatorThickness)
        )
    },
    content: DrawScope.() -> Unit,
) {
    val changeColor by rememberUpdatedState(onColorChange)

    val mapOffset by rememberUpdatedState(mapIndicatorOffset)

    val drawScope = remember { CanvasDrawScope() }

    var indicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    val (pixelMap, setPixelMap) = remember { mutableStateOf<PixelMap?>(null) }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val paletteSize = remember(maxWidth, maxHeight) {
            Size(maxWidth.value, maxHeight.value)
        }

        val indicatorOffset by remember(paletteSize, indicatorOffsetPercent) {
            derivedStateOf {
                if (indicatorOffsetPercent.isSpecified) {
                    Offset(
                        x = (indicatorOffsetPercent.x * paletteSize.width).coerceIn(0f, paletteSize.width),
                        y = (indicatorOffsetPercent.y * paletteSize.height).coerceIn(0f, paletteSize.height)
                    )
                } else Offset.Unspecified
            }
        }

        val color by remember(pixelMap, indicatorOffset) {
            derivedStateOf {
                if (indicatorOffset.isSpecified) {
                    pixelMap?.let { pixelMap ->
                        colorForPosition(pixelMap = pixelMap, position = Offset(indicatorOffset.x, indicatorOffset.y))
                    } ?: Color.Unspecified
                } else Color.Unspecified
            }
        }

        LaunchedEffect(color) {
            changeColor?.invoke(color)
        }

        val bitmap by remember(paletteSize, content, color) {
            derivedStateOf {
                paletteSize.takeIf(Size::isSpecified)?.let { size ->
                    drawScope.createBitmap(size = size, content = content).onSuccess { bmp ->
                        setPixelMap(bmp.toPixelMap())
                    }.getOrNull()
                }
            }
        }

        bitmap?.run {
            Image(bitmap = this, contentDescription = null, modifier = Modifier.fillMaxSize().pointerInput(mapOffset) {
                detectTapGestures(onTap = { offset ->
                    indicatorOffsetPercent = (mapOffset?.invoke(this, offset) ?: offset).let { (x, y) ->
                        Offset(
                            x = (x / size.width).coerceIn(0f, 1f),
                            y = (y / size.height).coerceIn(0f, 1f),
                        )
                    }
                })
            }.pointerInput(mapOffset) {
                detectDragGestures { change, _ ->
                    change.consume()
                    change.position.let { offset ->
                        indicatorOffsetPercent = (mapOffset?.invoke(this, offset) ?: offset).let { (x, y) ->
                            Offset(
                                x = (x / size.width).coerceIn(0f, 1f),
                                y = (y / size.height).coerceIn(0f, 1f),
                            )
                        }
                    }
                }
            })

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (indicatorOffset.isSpecified) {
                    indicatorContent(indicatorOffset)
                }
            }
        }
    }
}