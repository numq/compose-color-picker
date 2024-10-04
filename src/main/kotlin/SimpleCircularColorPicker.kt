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
    diameter: Float,
    density: Density,
    drawScope: CanvasDrawScope,
) = ImageBitmap(diameter.toInt(), diameter.toInt()).apply {
    drawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(this),
        size = Size(diameter, diameter)
    ) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Red,
                    Color.Magenta,
                    Color.Blue,
                    Color.Cyan,
                    Color.Green,
                    Color.Yellow,
                    Color.Red,
                )
            )
        )
        drawCircle(brush = Brush.radialGradient(colors = listOf(Color.White, Color.Transparent)))
        drawCircle(brush = Brush.radialGradient(colors = listOf(Color.Transparent, Color.Black)))
    }
}

@Composable
fun SimpleCircularColorPicker(
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
        BoxWithConstraints(
            modifier = Modifier.aspectRatio(1f).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val circleDiameter = remember(maxWidth) { maxWidth.value }

            val circleRadius = remember(circleDiameter) { circleDiameter / 2f }

            LaunchedEffect(circleDiameter) {
                renderJob?.cancelAndJoin()
                renderJob = renderScope.launch {
                    createBitmap(circleDiameter, density, drawScope).let { bmp ->
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
                    Image(bitmap = bmp,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val relativeOffset = offset - Offset(circleRadius, circleRadius)

                                val distance = relativeOffset.getDistance()
                                if (distance <= circleRadius) {
                                    val percentX = (relativeOffset.x / circleRadius) / 2f + 0.5f
                                    val percentY = (relativeOffset.y / circleRadius) / 2f + 0.5f

                                    indicatorOffsetPercent = Offset(percentX, percentY)
                                    indicatorOffset = offset
                                }
                            }
                        }
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    change.consume()

                                    val relativeOffset = change.position - Offset(circleRadius, circleRadius)

                                    val distance = relativeOffset.getDistance()
                                    if (distance <= circleRadius) {
                                        val percentX = (relativeOffset.x / circleRadius) / 2f + 0.5f
                                        val percentY = (relativeOffset.y / circleRadius) / 2f + 0.5f

                                        indicatorOffsetPercent = Offset(percentX, percentY)
                                        indicatorOffset = change.position
                                    } else {
                                        val direction = relativeOffset / distance
                                        indicatorOffset = Offset(circleRadius, circleRadius) + direction * circleRadius

                                        val clampedRelativeOffset = indicatorOffset - Offset(circleRadius, circleRadius)
                                        val percentX = (clampedRelativeOffset.x / circleRadius) / 2f + 0.5f
                                        val percentY = (clampedRelativeOffset.y / circleRadius) / 2f + 0.5f

                                        indicatorOffsetPercent = Offset(percentX, percentY)
                                    }
                                }
                            })

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