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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

private fun createWheelBitmap(
    diameter: Float,
    thickness: Float,
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
                    Color.Red
                )
            ),
            radius = (diameter - thickness) / 2f,
            style = Stroke(width = thickness),
        )
    }
}

@Composable
fun WheelColorPicker(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    density: Density = LocalDensity.current,
    pickColor: (Color) -> Unit,
) {
    val currentPickColor by rememberUpdatedState(pickColor)

    val renderScope = rememberCoroutineScope { Dispatchers.Default }

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
        LaunchedEffect(wheelColor, squareIndicatorOffset) {
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

            val wheelThickness = remember(wheelDiameter) {
                wheelDiameter * .15f
            }

            LaunchedEffect(wheelDiameter, wheelThickness) {
                wheelRenderJob?.cancelAndJoin()
                wheelRenderJob = renderScope.launch {
                    createWheelBitmap(wheelDiameter, wheelThickness, density, wheelDrawScope).let { bitmap ->
                        wheelBitmap = bitmap
                        wheelPixelMap = bitmap.toPixelMap()
                    }
                }
            }

            LaunchedEffect(wheelIndicatorOffsetPercent) {
                if (wheelIndicatorOffsetPercent.isSpecified) {
                    wheelIndicatorOffset = Offset(
                        x = wheelIndicatorOffsetPercent.x * wheelDiameter,
                        y = wheelIndicatorOffsetPercent.y * wheelDiameter
                    )
                }
            }

            val wheelDiameterInner = remember(wheelDiameter, wheelThickness) {
                wheelDiameter - 2 * wheelThickness
            }

            val squareSide = remember(wheelDiameterInner) {
                wheelDiameterInner / sqrt(2f)
            }

            LaunchedEffect(squareSide, wheelColor) {
                squareRenderJob?.cancelAndJoin()
                squareRenderJob = renderScope.launch {
                    createSquareBitmap(squareSide, wheelColor, density, squareDrawScope).let { bitmap ->
                        squareBitmap = bitmap
                        squarePixelMap = bitmap.toPixelMap()
                    }
                }
            }

            LaunchedEffect(squareIndicatorOffsetPercent) {
                if (squareIndicatorOffsetPercent.isSpecified) {
                    squareIndicatorOffset = Offset(
                        x = squareIndicatorOffsetPercent.x * squareSide,
                        y = squareIndicatorOffsetPercent.y * squareSide
                    )
                }
            }

            wheelBitmap?.let { bitmap ->
                Box(
                    modifier = Modifier.size(wheelDiameter.dp, wheelDiameter.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().pointerInput(wheelDiameter) {
                            detectTapGestures(onTap = { offset ->
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f

                                val dx = offset.x - centerX
                                val dy = offset.y - centerY
                                val distance = sqrt(dx * dx + dy * dy)

                                if (distance <= wheelDiameter / 2f) {
                                    Offset(dx + centerX, dy + centerY).run {
                                        wheelIndicatorOffsetPercent = Offset(
                                            x = (x / wheelDiameter).coerceIn(0f, 1f),
                                            y = (y / wheelDiameter).coerceIn(0f, 1f)
                                        )
                                    }
                                }
                            })
                        }.pointerInput(wheelDiameter, wheelThickness) {
                            detectDragGestures { change, _ ->
                                change.consume()

                                val centerX = size.width / 2f
                                val centerY = size.height / 2f

                                val dx = change.position.x - centerX
                                val dy = change.position.y - centerY
                                val distance = sqrt(dx * dx + dy * dy)

                                val outerRadius = wheelDiameter / 2f
                                val innerRadius = outerRadius - wheelThickness

                                when {
                                    distance in innerRadius..outerRadius -> change.position

                                    distance < innerRadius -> {
                                        val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                                        Offset(centerX + innerRadius * cos(angle), centerY + innerRadius * sin(angle))
                                    }

                                    else -> {
                                        val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                                        Offset(centerX + outerRadius * cos(angle), centerY + outerRadius * sin(angle))
                                    }
                                }.run {
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
                            drawCircle(
                                color = if (wheelColor.luminance() > .5f) Color.Black else Color.White,
                                radius = indicatorRadius,
                                center = wheelIndicatorOffset,
                                style = Stroke(width = 1f)
                            )
                        }
                    }
                }
            }

            squareBitmap?.let { bitmap ->
                Box(
                    modifier = Modifier.size(squareSide.dp, squareSide.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().pointerInput(squareSide) {
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
        }
    }
}