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
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import library.*
import kotlin.math.sqrt

@Composable
fun WheelColorPicker(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    wheelThicknessPercentage: Float = .15f,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val wheelDrawScope = remember { CanvasDrawScope() }
    val squareDrawScope = remember { CanvasDrawScope() }

    var wheelIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }
    var squareIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    var wheelPixelMap by remember { mutableStateOf<PixelMap?>(null) }
    var squarePixelMap by remember { mutableStateOf<PixelMap?>(null) }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        BoxWithConstraints(
            modifier = Modifier.aspectRatio(1f).fillMaxSize().padding(indicatorRadius.dp),
            contentAlignment = Alignment.Center
        ) {
            val wheelDiameter = remember(maxWidth, maxHeight) {
                minOf(maxWidth.value, maxHeight.value)
            }

            val wheelThickness = remember(wheelDiameter, wheelThicknessPercentage) {
                wheelDiameter * wheelThicknessPercentage
            }

            val wheelBitmap by remember(wheelDiameter, wheelThickness) {
                derivedStateOf {
                    wheelDrawScope.createBitmap(size = Size(width = wheelDiameter, height = wheelDiameter)) {
                        drawWheel(thicknessPercentage = wheelThicknessPercentage)
                    }.onSuccess { bmp ->
                        wheelPixelMap = bmp.toPixelMap()
                    }.getOrNull()
                }
            }

            val wheelIndicatorOffset = remember(wheelIndicatorOffsetPercent, wheelDiameter) {
                if (wheelIndicatorOffsetPercent.isSpecified) {
                    Offset(
                        x = wheelIndicatorOffsetPercent.x * wheelDiameter,
                        y = wheelIndicatorOffsetPercent.y * wheelDiameter
                    )
                } else Offset.Unspecified
            }

            val wheelColor = remember(wheelPixelMap, wheelIndicatorOffset) {
                wheelPixelMap?.takeIf { wheelIndicatorOffset.isSpecified }?.let { pixelMap ->
                    colorForPosition(pixelMap, wheelIndicatorOffset)
                } ?: Color.Unspecified
            }

            val wheelDiameterInner = remember(wheelDiameter, wheelThickness) {
                wheelDiameter - 2 * wheelThickness
            }

            val squareSide = remember(wheelDiameterInner, indicatorRadius) {
                (wheelDiameterInner / sqrt(2f)) - indicatorRadius
            }

            val squareBitmap by remember(squareSide, wheelColor) {
                derivedStateOf {
                    squareDrawScope.createBitmap(size = Size(width = squareSide, height = squareSide)) {
                        drawRectangle(color = wheelColor)
                    }.onSuccess { bmp ->
                        squarePixelMap = bmp.toPixelMap()
                    }.getOrNull()
                }
            }

            val squareIndicatorOffset = remember(squareIndicatorOffsetPercent, squareSide) {
                if (squareIndicatorOffsetPercent.isSpecified) {
                    Offset(
                        x = squareIndicatorOffsetPercent.x * squareSide,
                        y = squareIndicatorOffsetPercent.y * squareSide
                    )
                } else Offset.Unspecified
            }

            val squareColor = remember(squarePixelMap, squareIndicatorOffset) {
                squarePixelMap?.takeIf { squareIndicatorOffset.isSpecified }?.let { pixelMap ->
                    colorForPosition(pixelMap, squareIndicatorOffset)
                } ?: Color.Unspecified
            }

            LaunchedEffect(squareColor) {
                currentPickColor(squareColor)
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
                    Image(bitmap = bmp,
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
                        })

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