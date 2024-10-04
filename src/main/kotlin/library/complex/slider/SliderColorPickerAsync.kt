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
fun SliderColorPickerAsync(
    modifier: Modifier,
    sliderArrowWidth: Float = 8f,
    sliderWidth: Float = 32f,
    indicatorRadius: Float = 4f,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val renderScope = rememberCoroutineScope()

    var sliderRenderJob by remember { mutableStateOf<Job?>(null) }
    var squareRenderJob by remember { mutableStateOf<Job?>(null) }

    val sliderDrawScope = remember { CanvasDrawScope() }
    val squareDrawScope = remember { CanvasDrawScope() }

    var sliderIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }
    var squareIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    var sliderIndicatorOffset by remember { mutableStateOf(Offset.Unspecified) }
    var squareIndicatorOffset by remember { mutableStateOf(Offset.Unspecified) }

    var sliderColor by remember { mutableStateOf(Color.Unspecified) }
    var squareColor by remember { mutableStateOf(Color.Unspecified) }

    LaunchedEffect(squareColor) {
        currentPickColor(squareColor)
    }

    var sliderBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var squareBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var sliderPixelMap by remember { mutableStateOf<PixelMap?>(null) }
    var squarePixelMap by remember { mutableStateOf<PixelMap?>(null) }

    var squareSide by remember { mutableStateOf<Float?>(null) }

    squareSide?.let { side ->
        if (sliderIndicatorOffsetPercent.isSpecified) {
            LaunchedEffect(sliderIndicatorOffsetPercent) {
                sliderIndicatorOffset = Offset(
                    x = sliderIndicatorOffsetPercent.x * side,
                    y = sliderIndicatorOffsetPercent.y * side
                )
            }
        }

        if (squareIndicatorOffsetPercent.isSpecified) {
            LaunchedEffect(squareIndicatorOffsetPercent) {
                squareIndicatorOffset = Offset(
                    x = squareIndicatorOffsetPercent.x * side,
                    y = squareIndicatorOffsetPercent.y * side
                )
            }
        }
    }

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

                LaunchedEffect(squareSize, sliderColor) {
                    squareRenderJob?.cancelAndJoin()
                    squareRenderJob = renderScope.launch {
                        squareDrawScope.createBitmapAsync(size = squareSize) {
                            drawRectangle(color = sliderColor)
                        }.onSuccess { bmp ->
                            squareBitmap = bmp
                            squarePixelMap = bmp.toPixelMap()
                        }
                    }
                }

                squarePixelMap?.let { pixelMap ->
                    LaunchedEffect(squareIndicatorOffset, sliderColor) {
                        if (squareIndicatorOffset.isSpecified) {
                            squareColor = colorForPosition(pixelMap, squareIndicatorOffset)
                        }
                    }
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
                Row(
                    modifier = Modifier.height(height.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.width(sliderArrowWidth.dp).fillMaxHeight()) {
                        if (sliderIndicatorOffset.isSpecified) {
                            drawSliderArrow(width = sliderArrowWidth, offset = sliderIndicatorOffset)
                        }
                    }
                    BoxWithConstraints(modifier = Modifier.width(sliderWidth.dp).fillMaxHeight()) {
                        val sliderSize = remember(maxWidth, maxHeight) {
                            Size(maxWidth.value, maxHeight.value)
                        }

                        LaunchedEffect(sliderSize) {
                            sliderRenderJob?.cancelAndJoin()
                            sliderRenderJob = renderScope.launch {
                                sliderDrawScope.createBitmapAsync(size = sliderSize) {
                                    drawSlider()
                                }.onSuccess { bmp ->
                                    sliderBitmap = bmp
                                    sliderPixelMap = bmp.toPixelMap()
                                }
                            }
                        }

                        sliderPixelMap?.let { pixelMap ->
                            LaunchedEffect(sliderIndicatorOffset) {
                                if (sliderIndicatorOffset.isSpecified) {
                                    sliderColor = colorForPosition(pixelMap, sliderIndicatorOffset)
                                }
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