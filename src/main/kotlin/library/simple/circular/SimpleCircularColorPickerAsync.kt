package library.simple.circular

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
import library.*

@Composable
fun SimpleCircularColorPickerAsync(
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

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        BoxWithConstraints(
            modifier = Modifier.aspectRatio(1f).fillMaxSize().padding(indicatorRadius.dp),
            contentAlignment = Alignment.Center
        ) {
            val circleDiameter = remember(maxWidth) { maxWidth.value }

            LaunchedEffect(circleDiameter) {
                renderJob?.cancelAndJoin()
                renderJob = renderScope.launch {
                    drawScope.createBitmapAsync(size = Size(circleDiameter, circleDiameter)) {
                        drawCircle()
                    }.onSuccess { bmp ->
                        bitmap = bmp
                        pixelMap = bmp.toPixelMap()
                    }
                }
            }

            LaunchedEffect(circleDiameter, indicatorOffsetPercent) {
                if (indicatorOffsetPercent.isSpecified) {
                    indicatorOffset = Offset(
                        x = (indicatorOffsetPercent.x * circleDiameter).coerceIn(0f, circleDiameter),
                        y = (indicatorOffsetPercent.y * circleDiameter).coerceIn(0f, circleDiameter)
                    )
                }
            }

            bitmap?.let { bmp ->
                Box(
                    modifier = Modifier.size(circleDiameter.dp, circleDiameter.dp), contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp,
                        contentDescription = null,
                        modifier = Modifier.size(bmp.width.dp, bmp.height.dp)
                            .pointerInput(circleDiameter, indicatorRadius) {
                                detectTapGestures { offset ->
                                    indicatorOffsetPercent =
                                        calculateCirclePercentage(offset, circleDiameter, indicatorRadius)
                                }
                            }.pointerInput(circleDiameter, indicatorRadius) {
                            detectDragGestures { change, _ ->
                                change.consume()

                                indicatorOffsetPercent =
                                    calculateCirclePercentage(change.position, circleDiameter, indicatorRadius)
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