package library.complex.slider

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
fun SliderColorPicker(
    modifier: Modifier,
    sliderArrowWidth: Float = 8f,
    sliderWidth: Float = 32f,
    indicatorRadius: Float = 4f,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val sliderDrawScope = remember { CanvasDrawScope() }
    val squareDrawScope = remember { CanvasDrawScope() }

    var sliderIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }
    var squareIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    var sliderPixelMap by remember { mutableStateOf<PixelMap?>(null) }
    var squarePixelMap by remember { mutableStateOf<PixelMap?>(null) }

    var squareSide by remember { mutableStateOf<Float?>(null) }

    var baseColor by remember { mutableStateOf(Color.Unspecified) }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.fillMaxSize().padding(indicatorRadius.dp),
            horizontalArrangement = Arrangement.spacedBy(space = indicatorRadius.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithConstraints(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val squareSize = remember(maxWidth, maxHeight) {
                    minOf(maxWidth, maxHeight).value.let { side ->
                        Size(side, side)
                    }
                }

                SideEffect {
                    squareSide = squareSize.width
                }

                val squareBitmap by remember(squareSize, baseColor) {
                    derivedStateOf {
                        squareDrawScope.createBitmap(size = squareSize) {
                            drawRectangle(color = baseColor)
                        }.onSuccess { bmp ->
                            squarePixelMap = bmp.toPixelMap()
                        }.getOrNull()
                    }
                }

                val squareIndicatorOffset = remember(squareSize, squareIndicatorOffsetPercent) {
                    if (squareIndicatorOffsetPercent.isSpecified) {
                        Offset(
                            x = squareIndicatorOffsetPercent.x * squareSize.width,
                            y = squareIndicatorOffsetPercent.y * squareSize.height
                        )
                    } else Offset.Unspecified
                }

                val squareColor = remember(squareIndicatorOffset, baseColor) {
                    squarePixelMap?.takeIf { squareIndicatorOffset.isSpecified }?.let { pixelMap ->
                        colorForPosition(pixelMap, squareIndicatorOffset)
                    } ?: Color.Unspecified
                }

                LaunchedEffect(squareColor) {
                    currentPickColor(squareColor)
                }

                squareBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp,
                        contentDescription = null,
                        modifier = Modifier.size(bmp.width.dp, bmp.height.dp).pointerInput(Unit) {
                            detectTapGestures(onTap = { offset ->
                                squareIndicatorOffsetPercent = Offset(
                                    x = (offset.x / size.width).coerceIn(0f, 1f),
                                    y = (offset.y / size.height).coerceIn(0f, 1f)
                                )
                            })
                        }.pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()

                                squareIndicatorOffsetPercent = Offset(
                                    x = (change.position.x / size.width).coerceIn(0f, 1f),
                                    y = (change.position.y / size.height).coerceIn(0f, 1f)
                                )
                            }
                        }
                    )

                    androidx.compose.foundation.Canvas(modifier = Modifier.size(bmp.width.dp, bmp.height.dp)) {
                        if (squareIndicatorOffset.isSpecified) {
                            drawIndicator(indicatorRadius, squareIndicatorOffset)
                        }
                    }
                }
            }
            squareSide?.let { height ->
                BoxWithConstraints(
                    modifier = Modifier.height(height.dp).width((sliderArrowWidth + sliderWidth).dp),
                    contentAlignment = Alignment.Center
                ) {
                    val sliderIndicatorOffset = remember(height, sliderIndicatorOffsetPercent) {
                        if (sliderIndicatorOffsetPercent.isSpecified) {
                            Offset(
                                x = 0f,
                                y = sliderIndicatorOffsetPercent.y * height
                            )
                        } else {
                            Offset.Unspecified
                        }
                    }

                    val sliderColor = remember(sliderIndicatorOffset) {
                        sliderPixelMap?.takeIf { sliderIndicatorOffset.isSpecified }?.let { pixelMap ->
                            colorForPosition(pixelMap, sliderIndicatorOffset)
                        } ?: Color.Unspecified
                    }

                    LaunchedEffect(sliderColor) {
                        baseColor = sliderColor
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier.width(sliderArrowWidth.dp).fillMaxHeight()
                        ) {
                            if (sliderIndicatorOffset.isSpecified) {
                                drawSliderArrow(width = sliderArrowWidth, offset = sliderIndicatorOffset)
                            }
                        }
                        BoxWithConstraints(modifier = Modifier.width(sliderWidth.dp).fillMaxHeight()) {
                            val sliderSize = remember(maxWidth, maxHeight) {
                                Size(maxWidth.value, maxHeight.value)
                            }

                            val sliderBitmap by remember(sliderSize) {
                                derivedStateOf {
                                    sliderDrawScope.createBitmap(size = sliderSize) {
                                        drawSlider()
                                    }.onSuccess { bmp ->
                                        sliderPixelMap = bmp.toPixelMap()
                                    }.getOrNull()
                                }
                            }

                            sliderBitmap?.let { bmp ->
                                Image(
                                    bitmap = bmp,
                                    contentDescription = null,
                                    modifier = Modifier.size(bmp.width.dp, bmp.height.dp).pointerInput(Unit) {
                                        detectTapGestures(onTap = { offset ->
                                            sliderIndicatorOffsetPercent =
                                                Offset(x = 0f, y = (offset.y / size.height).coerceIn(0f, 1f))
                                        })
                                    }.pointerInput(Unit) {
                                        detectDragGestures { change, _ ->
                                            change.consume()

                                            sliderIndicatorOffsetPercent =
                                                Offset(x = 0f, y = (change.position.y / size.height).coerceIn(0f, 1f))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}