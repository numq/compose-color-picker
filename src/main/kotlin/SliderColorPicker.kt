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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

private fun createSquareBitmap(
    side: Float,
    baseColor: Color,
    density: Density,
    drawScope: CanvasDrawScope,
) = ImageBitmap(side.toInt(), side.toInt()).apply {
    drawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(this),
        size = Size(side, side)
    ) {
        drawRect(brush = Brush.horizontalGradient(colors = listOf(Color.White, baseColor)))
        drawRect(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black)))
    }
}

private fun createSliderBitmap(
    size: Size,
    density: Density,
    drawScope: CanvasDrawScope,
) = ImageBitmap(size.width.toInt(), size.height.toInt()).apply {
    drawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(this),
        size = size
    ) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Red,
                    Color.Magenta,
                    Color.Blue,
                    Color.Cyan,
                    Color.Green,
                    Color.Yellow,
                    Color.Red
                )
            )
        )
    }
}

@Composable
fun SliderColorPicker(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    density: Density = LocalDensity.current,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val renderScope = rememberCoroutineScope { Dispatchers.Default }

    var sliderRenderJob by remember { mutableStateOf<Job?>(null) }
    var squareRenderJob by remember { mutableStateOf<Job?>(null) }

    val sliderDrawScope = remember { CanvasDrawScope() }
    val squareDrawScope = remember { CanvasDrawScope() }

    var sliderIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }
    var squareIndicatorOffsetPercent by remember { mutableStateOf(Offset.Unspecified) }

    LaunchedEffect(sliderIndicatorOffsetPercent) {
        println(sliderIndicatorOffsetPercent)
    }

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

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.fillMaxSize().padding(indicatorRadius.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithConstraints(
                modifier = Modifier.weight(1f).aspectRatio(1f).onGloballyPositioned { layoutCoordinates ->
                    squareSide = layoutCoordinates.size.width.dp.value
                },
                contentAlignment = Alignment.Center
            ) {
                squareSide?.let { side ->
                    LaunchedEffect(squareSide, sliderColor) {
                        squareRenderJob?.cancelAndJoin()
                        squareRenderJob = renderScope.launch {
                            createSquareBitmap(side, sliderColor, density, squareDrawScope).let { bitmap ->
                                squareBitmap = bitmap
                                squarePixelMap = bitmap.toPixelMap()
                            }
                        }
                    }

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

                squarePixelMap?.let { pixelMap ->
                    LaunchedEffect(sliderColor, squareIndicatorOffset) {
                        if (squareIndicatorOffset.isSpecified) {
                            squareColor = colorForPosition(pixelMap, squareIndicatorOffset)
                        }
                    }
                }

                squareBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
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

                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        if (squareIndicatorOffset.isSpecified) {
                            drawCircle(
                                color = if (squareColor.luminance() > .5f) Color.Black else Color.White,
                                radius = indicatorRadius,
                                center = squareIndicatorOffset,
                                style = Stroke(width = 1f)
                            )
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
                    androidx.compose.foundation.Canvas(modifier = Modifier.width(8.dp).fillMaxHeight()) {
                        val side = size.width

                        if (sliderIndicatorOffset.isSpecified) {
                            val startX = sliderIndicatorOffset.x
                            val startY = sliderIndicatorOffset.y - side / 2

                            val path = Path().apply {
                                moveTo(startX, startY)
                                lineTo(startX + side, startY + side / 2)
                                lineTo(startX, startY + side)
                                close()
                            }

                            drawPath(
                                path = path,
                                color = Color.White
                            )

                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.cornerPathEffect(2.dp.toPx())
                                )
                            )
                        }
                    }
                    BoxWithConstraints(modifier = Modifier.width(32.dp).fillMaxHeight()) {
                        val sliderSize = remember(maxWidth, maxHeight) {
                            Size(maxWidth.value, maxHeight.value)
                        }

                        LaunchedEffect(sliderSize) {
                            sliderRenderJob?.cancelAndJoin()
                            sliderRenderJob = renderScope.launch {
                                createSliderBitmap(sliderSize, density, sliderDrawScope).let { bitmap ->
                                    sliderBitmap = bitmap
                                    sliderPixelMap = bitmap.toPixelMap()
                                }

                                if (sliderIndicatorOffsetPercent.isSpecified) {
                                    sliderIndicatorOffset = Offset(
                                        x = sliderIndicatorOffsetPercent.x * sliderSize.width,
                                        y = sliderIndicatorOffsetPercent.y * sliderSize.height
                                    )
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

                        sliderBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
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