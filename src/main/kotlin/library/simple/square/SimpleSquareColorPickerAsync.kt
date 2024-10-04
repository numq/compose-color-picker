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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import library.colorForPosition
import library.createBitmapAsync
import library.drawIndicator
import library.drawRectangleRGB

@Composable
fun SimpleSquareColorPickerAsync(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val renderScope = rememberCoroutineScope()

    var renderJob by remember { mutableStateOf<Job?>(null) }

    val drawScope = remember { CanvasDrawScope() }

    var indicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    var indicatorOffset by remember { mutableStateOf(Offset.Unspecified) }

    var color by remember { mutableStateOf(Color.Unspecified) }

    LaunchedEffect(color) {
        currentPickColor(color)
    }

    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var pixelMap by remember { mutableStateOf<PixelMap?>(null) }

    pixelMap?.let { map ->
        LaunchedEffect(indicatorOffset) {
            if (indicatorOffset.isSpecified) {
                color = colorForPosition(map, indicatorOffset)
            }
        }
    }

    BoxWithConstraints(modifier = modifier.padding(indicatorRadius.dp), contentAlignment = Alignment.Center) {
        BoxWithConstraints(modifier = Modifier.aspectRatio(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
            val size = remember(maxWidth, maxHeight) {
                Size(maxWidth.value, maxHeight.value)
            }

            LaunchedEffect(size) {
                renderJob?.cancelAndJoin()
                renderJob = renderScope.launch {
                    drawScope.createBitmapAsync(size = size) {
                        drawRectangleRGB()
                    }.onSuccess { bmp ->
                        bitmap = bmp
                        pixelMap = bmp.toPixelMap()
                    }
                }
            }

            LaunchedEffect(size, indicatorOffsetPercent) {
                if (indicatorOffsetPercent.isSpecified) {
                    indicatorOffset = Offset(
                        x = (indicatorOffsetPercent.x * size.width).coerceIn(0f, size.width - 1f),
                        y = (indicatorOffsetPercent.y * size.height).coerceIn(0f, size.height - 1f)
                    )
                }
            }

            bitmap?.let { bmp ->
                Box(
                    modifier = Modifier.size(size.width.dp, size.height.dp), contentAlignment = Alignment.Center
                ) {
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