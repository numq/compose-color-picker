import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

private fun createPaletteBitmap(
    paletteSize: Size,
    baseColor: Color,
    density: Density,
    paletteDrawScope: CanvasDrawScope,
) = ImageBitmap(paletteSize.width.toInt(), paletteSize.height.toInt()).apply {
    paletteDrawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(this),
        size = paletteSize
    ) {
        drawRect(brush = Brush.horizontalGradient(colors = listOf(Color.White, baseColor)))
        drawRect(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black)))
    }
}

private fun createSliderBitmap(
    sliderSize: Size,
    density: Density,
    sliderDrawScope: CanvasDrawScope,
) = ImageBitmap(sliderSize.width.toInt(), sliderSize.height.toInt()).apply {
    sliderDrawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(this),
        size = sliderSize
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
fun SquareColorPicker(
    modifier: Modifier,
    paletteIndicatorRadius: Float = 4f,
    sliderIndicatorThickness: Float = 4f,
    density: Density = LocalDensity.current,
    pickColor: (Color) -> Unit,
) {
    val renderScope = rememberCoroutineScope { Dispatchers.Default }

    val currentPickColor by rememberUpdatedState(pickColor)

    val paletteDrawScope = remember { CanvasDrawScope() }
    val sliderDrawScope = remember { CanvasDrawScope() }

    var paletteIndicatorOffset by remember { mutableStateOf(Offset.Zero) }
    var sliderIndicatorOffset by remember { mutableStateOf(Offset.Zero) }

    var paletteColor by remember { mutableStateOf(Color.Unspecified) }
    var sliderColor by remember { mutableStateOf(Color.Unspecified) }

    var paletteBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var sliderBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var palettePixelMap by remember { mutableStateOf<PixelMap?>(null) }
    var sliderPixelMap by remember { mutableStateOf<PixelMap?>(null) }

    var paletteRenderJob by remember { mutableStateOf<Job?>(null) }
    var sliderRenderJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(paletteColor) {
        currentPickColor(paletteColor)
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.aspectRatio(1f).fillMaxSize().padding(paletteIndicatorRadius.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithConstraints(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val paletteSize = remember(maxWidth, maxHeight) {
                    Size(maxWidth.value, maxHeight.value)
                }

                LaunchedEffect(paletteSize, sliderColor) {
                    paletteRenderJob?.cancelAndJoin()
                    paletteRenderJob = renderScope.launch {
                        createPaletteBitmap(paletteSize, sliderColor, density, paletteDrawScope).let { bitmap ->
                            paletteBitmap = bitmap
                            palettePixelMap = bitmap.toPixelMap()
                        }
                    }
                }

                palettePixelMap?.let { pixelMap ->
                    LaunchedEffect(sliderColor, paletteIndicatorOffset) {
                        paletteColor = colorForPosition(pixelMap, paletteIndicatorOffset)
                    }
                }

                paletteBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                            detectTapGestures(onTap = { offset ->
                                paletteIndicatorOffset = offset.copy(x = offset.x, y = offset.y)
                            })
                        }.pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                paletteIndicatorOffset = change.position.copy(
                                    x = change.position.x.coerceIn(0f, size.width.toFloat() - 1f),
                                    y = change.position.y.coerceIn(0f, size.height.toFloat() - 1f),
                                )
                            }
                        }
                    )

                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = if (paletteColor.luminance() > .5f) Color.Black else Color.White,
                            radius = paletteIndicatorRadius,
                            center = paletteIndicatorOffset,
                            style = Stroke(width = 1f)
                        )
                    }
                }
            }
            BoxWithConstraints(
                modifier = Modifier.width(32.dp).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
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
                    }
                }

                sliderPixelMap?.let { pixelMap ->
                    LaunchedEffect(sliderIndicatorOffset) {
                        sliderColor = colorForPosition(pixelMap, sliderIndicatorOffset)
                    }
                }

                sliderBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                            detectTapGestures(onTap = { offset ->
                                sliderIndicatorOffset = sliderIndicatorOffset.copy(y = offset.y)
                            })
                        }.pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                sliderIndicatorOffset = sliderIndicatorOffset.copy(
                                    y = change.position.y.coerceIn(0f, size.height.toFloat() - 1f)
                                )
                            }
                        }
                    )

                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(
                            color = if (sliderColor.luminance() > .5f) Color.Black else Color.White,
                            start = sliderIndicatorOffset.copy(
                                x = 0f,
                                y = sliderIndicatorOffset.y
                            ),
                            end = sliderIndicatorOffset.copy(
                                x = maxWidth.value,
                                y = sliderIndicatorOffset.y
                            ),
                            strokeWidth = sliderIndicatorThickness
                        )
                    }
                }
            }
        }
    }
}