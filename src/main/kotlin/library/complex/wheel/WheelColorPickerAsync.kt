package library.complex.wheel

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
import kotlin.math.sqrt

@Composable
fun WheelColorPickerAsync(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val renderScope = rememberCoroutineScope()

    var wheelRenderJob by remember { mutableStateOf<Job?>(null) }
    var squareRenderJob by remember { mutableStateOf<Job?>(null) }

    val wheelDrawScope = remember { CanvasDrawScope() }
    val squareDrawScope = remember { CanvasDrawScope() }

    var wheelIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }
    var squareIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    var wheelIndicatorOffset by remember { mutableStateOf(Offset.Unspecified) }
    var squareIndicatorOffset by remember { mutableStateOf(Offset.Unspecified) }

    var wheelColor by remember { mutableStateOf(Color.Unspecified) }
    var squareColor by remember { mutableStateOf(Color.Unspecified) }

    LaunchedEffect(squareColor) {
        currentPickColor(squareColor)
    }

    var wheelBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var squareBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var wheelPixelMap by remember { mutableStateOf<PixelMap?>(null) }

    wheelPixelMap?.let { pixelMap ->
        LaunchedEffect(wheelIndicatorOffset) {
            if (wheelIndicatorOffset.isSpecified) {
                wheelColor = colorForPosition(pixelMap, wheelIndicatorOffset)
            }
        }
    }

    var squarePixelMap by remember { mutableStateOf<PixelMap?>(null) }

    squarePixelMap?.let { pixelMap ->
        LaunchedEffect(squareIndicatorOffset, wheelColor) {
            if (squareIndicatorOffset.isSpecified) {
                squareColor = colorForPosition(pixelMap, squareIndicatorOffset)
            }
        }
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        BoxWithConstraints(
            modifier = Modifier.aspectRatio(1f).fillMaxSize().padding(indicatorRadius.dp),
            contentAlignment = Alignment.Center
        ) {
            val wheelDiameter = remember(maxWidth, maxHeight) {
                minOf(maxWidth.value, maxHeight.value)
            }

            val wheelThicknessPercentage = remember { .15f }

            val wheelThickness = remember(wheelDiameter, wheelThicknessPercentage) {
                wheelDiameter * wheelThicknessPercentage
            }

            LaunchedEffect(wheelDiameter, wheelThickness) {
                wheelRenderJob?.cancelAndJoin()
                wheelRenderJob = renderScope.launch {
                    wheelDrawScope.createBitmapAsync(size = Size(width = wheelDiameter, height = wheelDiameter)) {
                        drawWheel(thicknessPercentage = wheelThicknessPercentage)
                    }.onSuccess { bmp ->
                        wheelBitmap = bmp
                        wheelPixelMap = bmp.toPixelMap()
                    }
                }
            }

            if (wheelIndicatorOffsetPercent.isSpecified) {
                LaunchedEffect(wheelIndicatorOffsetPercent) {
                    wheelIndicatorOffset = Offset(
                        x = wheelIndicatorOffsetPercent.x * wheelDiameter,
                        y = wheelIndicatorOffsetPercent.y * wheelDiameter
                    )
                }
            }

            val wheelDiameterInner = remember(wheelDiameter, wheelThickness) {
                wheelDiameter - 2 * wheelThickness
            }

            val squareSide = remember(wheelDiameterInner, indicatorRadius) {
                (wheelDiameterInner / sqrt(2f)) - indicatorRadius
            }

            LaunchedEffect(squareSide, wheelColor) {
                squareRenderJob?.cancelAndJoin()
                squareRenderJob = renderScope.launch {
                    squareDrawScope.createBitmapAsync(size = Size(width = squareSide, height = squareSide)) {
                        drawRectangle(color = wheelColor)
                    }.onSuccess { bmp ->
                        squareBitmap = bmp
                        squarePixelMap = bmp.toPixelMap()
                    }
                }
            }

            if (squareIndicatorOffsetPercent.isSpecified) {
                LaunchedEffect(squareIndicatorOffsetPercent) {
                    squareIndicatorOffset = Offset(
                        x = squareIndicatorOffsetPercent.x * squareSide,
                        y = squareIndicatorOffsetPercent.y * squareSide
                    )
                }
            }

            wheelBitmap?.let { bmp ->
                Box(
                    modifier = Modifier.size(wheelDiameter.dp, wheelDiameter.dp), contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp,
                        contentDescription = null,
                        modifier = Modifier.size(bmp.width.dp, bmp.height.dp)
                            .pointerInput(wheelDiameter, wheelThickness, indicatorRadius) {
                                detectTapGestures(onTap = { offset ->
                                    calculateWheelPercentage(
                                        offset,
                                        wheelDiameter,
                                        wheelThickness,
                                        indicatorRadius
                                    ).onSuccess { (x, y) ->
                                        wheelIndicatorOffsetPercent = Offset(
                                            x = (x / wheelDiameter).coerceIn(0f, 1f),
                                            y = (y / wheelDiameter).coerceIn(0f, 1f)
                                        )
                                    }
                                })
                            }.pointerInput(wheelDiameter, wheelThickness, indicatorRadius) {
                                detectDragGestures { change, _ ->
                                    change.consume()

                                    calculateWheelPercentage(
                                        change.position,
                                        wheelDiameter,
                                        wheelThickness,
                                        indicatorRadius
                                    ).onSuccess { (x, y) ->
                                        wheelIndicatorOffsetPercent = Offset(
                                            x = (x / wheelDiameter).coerceIn(0f, 1f),
                                            y = (y / wheelDiameter).coerceIn(0f, 1f)
                                        )
                                    }
                                }
                            }
                    )

                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        if (wheelIndicatorOffset.isSpecified) {
                            drawIndicator(indicatorRadius, wheelIndicatorOffset)
                        }
                    }
                }
            }

            squareBitmap?.let { bmp ->
                Box(
                    modifier = Modifier.size(squareSide.dp, squareSide.dp), contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp,
                        contentDescription = null,
                        modifier = Modifier.size(bmp.width.dp, bmp.height.dp).pointerInput(squareSide) {
                            detectTapGestures(onTap = { offset ->
                                squareIndicatorOffsetPercent = Offset(
                                    x = (offset.x / squareSide).coerceIn(0f, 1f),
                                    y = (offset.y / squareSide).coerceIn(0f, 1f)
                                )
                            })
                        }.pointerInput(squareSide) {
                            detectDragGestures { change, _ ->
                                change.consume()

                                squareIndicatorOffsetPercent = Offset(
                                    x = (change.position.x / squareSide).coerceIn(0f, 1f),
                                    y = (change.position.y / squareSide).coerceIn(0f, 1f)
                                )
                            }
                        }
                    )

                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        if (squareIndicatorOffset.isSpecified) {
                            drawIndicator(indicatorRadius, squareIndicatorOffset)
                        }
                    }
                }
            }
        }
    }
}