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
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import library.*

@Composable
fun SimpleCircularColorPicker(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val drawScope = remember { CanvasDrawScope() }

    var indicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    var pixelMap by remember { mutableStateOf<PixelMap?>(null) }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        BoxWithConstraints(
            modifier = Modifier.aspectRatio(1f).fillMaxSize().padding(indicatorRadius.dp),
            contentAlignment = Alignment.Center
        ) {
            val circleDiameter = remember(maxWidth) { maxWidth.value }

            val bitmap by remember(circleDiameter) {
                derivedStateOf {
                    drawScope.createBitmap(size = Size(circleDiameter, circleDiameter)) {
                        drawCircle()
                    }.onSuccess { bmp ->
                        pixelMap = bmp.toPixelMap()
                    }.getOrNull()
                }
            }

            val indicatorOffset = remember(circleDiameter, indicatorOffsetPercent) {
                if (indicatorOffsetPercent.isSpecified) {
                    Offset(
                        x = (indicatorOffsetPercent.x * circleDiameter).coerceIn(0f, circleDiameter),
                        y = (indicatorOffsetPercent.y * circleDiameter).coerceIn(0f, circleDiameter)
                    )
                } else Offset.Unspecified
            }

            val color by remember(pixelMap, indicatorOffset) {
                derivedStateOf {
                    pixelMap?.takeIf { indicatorOffset.isSpecified }?.let { map ->
                        colorForPosition(map, indicatorOffset)
                    } ?: Color.Unspecified
                }
            }

            LaunchedEffect(color) {
                currentPickColor(color)
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