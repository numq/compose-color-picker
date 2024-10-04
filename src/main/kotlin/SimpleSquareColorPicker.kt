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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

private fun createBitmap(
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
            brush = Brush.horizontalGradient(
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
        drawRect(brush = Brush.verticalGradient(colors = listOf(Color.White, Color.Transparent, Color.Black)))
    }
}

@Composable
fun SimpleSquareColorPicker(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    density: Density = LocalDensity.current,
    pickColor: (Color) -> Unit,
) {
    val renderScope = rememberCoroutineScope { Dispatchers.Default }

    var renderJob by remember { mutableStateOf<Job?>(null) }

    val currentPickColor by rememberUpdatedState(pickColor)

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
                    createBitmap(size, density, drawScope).let { bmp ->
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
                    modifier = Modifier.size(size.width.dp, size.height.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
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

                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        if (indicatorOffset.isSpecified) {
                            drawCircle(
                                color = if (color.luminance() > .5f) Color.Black else Color.White,
                                radius = indicatorRadius,
                                center = indicatorOffset,
                                style = Stroke(width = 1f)
                            )
                        }
                    }
                }
            }
        }
    }
}