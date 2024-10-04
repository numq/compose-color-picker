package library.simple.square

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import library.colorForPosition
import library.createBitmap
import library.drawIndicator
import library.drawRectangleRGB

@Composable
fun SimpleSquareColorPicker(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val drawScope = remember { CanvasDrawScope() }

    var indicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    var pixelMap by remember { mutableStateOf<PixelMap?>(null) }

    BoxWithConstraints(modifier = modifier.padding(indicatorRadius.dp), contentAlignment = Alignment.Center) {
        BoxWithConstraints(modifier = Modifier.aspectRatio(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
            val size = remember(maxWidth, maxHeight) {
                Size(maxWidth.value, maxHeight.value)
            }

            val bitmap by remember(size) {
                derivedStateOf {
                    drawScope.createBitmap(size = size) {
                        drawRectangleRGB()
                    }.onSuccess { bmp ->
                        pixelMap = bmp.toPixelMap()
                    }.getOrNull()
                }
            }

            val indicatorOffset = remember(size, indicatorOffsetPercent) {
                if (indicatorOffsetPercent.isSpecified) {
                    Offset(
                        x = (indicatorOffsetPercent.x * size.width).coerceIn(0f, size.width - 1f),
                        y = (indicatorOffsetPercent.y * size.height).coerceIn(0f, size.height - 1f)
                    )
                } else Offset.Unspecified
            }

            val color = remember(pixelMap, indicatorOffset) {
                pixelMap?.takeIf { indicatorOffset.isSpecified }?.let { map ->
                    colorForPosition(map, indicatorOffset)
                } ?: Color.Unspecified
            }

            LaunchedEffect(color) {
                currentPickColor(color)
            }

            bitmap?.let { bmp ->
                Box(modifier = Modifier.size(size.width.dp, size.height.dp), contentAlignment = Alignment.Center) {
                    Image(
                        bitmap = bmp,
                        contentDescription = null,
                        modifier = Modifier.size(bmp.width.dp, bmp.height.dp).pointerInput(Unit) {
                            detectTapGestures(onTap = { offset ->
                                indicatorOffsetPercent = Offset(
                                    x = (offset.x / size.width).coerceIn(0f, 1f),
                                    y = (offset.y / size.height).coerceIn(0f, 1f)
                                )
                            })
                        }.pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()

                                indicatorOffsetPercent = Offset(
                                    x = (change.position.x / size.width).coerceIn(0f, 1f),
                                    y = (change.position.y / size.height).coerceIn(0f, 1f)
                                )
                            }
                        }
                    )

                    androidx.compose.foundation.Canvas(modifier = Modifier.size(bmp.width.dp, bmp.height.dp)) {
                        if (indicatorOffset.isSpecified) {
                            drawIndicator(indicatorRadius, indicatorOffset)
                        }
                    }
                }
            }
        }
    }
}