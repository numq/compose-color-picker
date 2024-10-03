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
    paletteSide: Float,
    baseColor: Color,
    density: Density,
    paletteDrawScope: CanvasDrawScope,
) = ImageBitmap(paletteSide.toInt(), paletteSide.toInt()).apply {
    paletteDrawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(this),
        size = Size(paletteSide, paletteSide)
    ) {
        drawRect(brush = Brush.horizontalGradient(colors = listOf(Color.White, baseColor)))
        drawRect(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black)))
    }
}

private fun createCircleBitmap(
    diameter: Float,
    thickness: Float,
    density: Density,
    circleDrawScope: CanvasDrawScope,
) = ImageBitmap(diameter.toInt(), diameter.toInt()).apply {
    circleDrawScope.draw(
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
fun CircularColorPicker(
    modifier: Modifier,
    indicatorRadius: Float = 4f,
    density: Density = LocalDensity.current,
    pickColor: (Color) -> Unit,
) {
    val renderScope = rememberCoroutineScope { Dispatchers.Default }

    val currentPickColor by rememberUpdatedState(pickColor)

    val circleDrawScope = remember { CanvasDrawScope() }
    val paletteDrawScope = remember { CanvasDrawScope() }

    var circleIndicatorOffset by remember { mutableStateOf(Offset.Unspecified) }
    var paletteIndicatorOffset by remember { mutableStateOf(Offset.Unspecified) }

    var circleColor by remember { mutableStateOf(Color.Unspecified) }
    var paletteColor by remember { mutableStateOf(Color.Unspecified) }

    var circleBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var paletteBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var circlePixelMap by remember { mutableStateOf<PixelMap?>(null) }

    circlePixelMap?.let { pixelMap ->
        LaunchedEffect(circleIndicatorOffset) {
            if (circleIndicatorOffset.isSpecified) {
                circleColor = colorForPosition(pixelMap, circleIndicatorOffset)
            }
        }
    }

    var palettePixelMap by remember { mutableStateOf<PixelMap?>(null) }

    palettePixelMap?.let { pixelMap ->
        LaunchedEffect(circleColor, paletteIndicatorOffset) {
            if (paletteIndicatorOffset.isSpecified) {
                paletteColor = colorForPosition(pixelMap, paletteIndicatorOffset)
            }
        }
    }

    var circleRenderJob by remember { mutableStateOf<Job?>(null) }
    var paletteRenderJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(paletteColor) {
        currentPickColor(paletteColor)
    }

    BoxWithConstraints(modifier = modifier.padding(indicatorRadius.dp), contentAlignment = Alignment.Center) {
        val circleDiameter = remember(maxWidth, maxHeight) {
            minOf(maxWidth.value, maxHeight.value)
        }

        val circleThickness = remember(circleDiameter) {
            circleDiameter * .15f
        }

        LaunchedEffect(circleDiameter, circleThickness) {
            circleRenderJob?.cancelAndJoin()
            circleRenderJob = renderScope.launch {
                createCircleBitmap(circleDiameter, circleThickness, density, circleDrawScope).let { bitmap ->
                    circleBitmap = bitmap
                    circlePixelMap = bitmap.toPixelMap()
                }
            }
        }

        val innerCircleDiameter = remember(circleDiameter, circleThickness) {
            circleDiameter - 2 * circleThickness
        }

        val paletteSide = remember(innerCircleDiameter) {
            innerCircleDiameter / sqrt(2f)
        }

        LaunchedEffect(paletteSide, circleColor) {
            paletteRenderJob?.cancelAndJoin()
            paletteRenderJob = renderScope.launch {
                createSquareBitmap(paletteSide, circleColor, density, paletteDrawScope).let { bitmap ->
                    paletteBitmap = bitmap
                    palettePixelMap = bitmap.toPixelMap()
                }
            }
        }

        circleBitmap?.let { bitmap ->
            Box(
                modifier = Modifier.size(circleDiameter.dp, circleDiameter.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                        detectTapGestures(onTap = { offset ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f

                            val dx = offset.x - centerX
                            val dy = offset.y - centerY
                            val distance = sqrt(dx * dx + dy * dy)

                            if (distance <= circleDiameter / 2f) {
                                circleIndicatorOffset = Offset(dx + centerX, dy + centerY)
                            }
                        })
                    }.pointerInput(circleDiameter, circleThickness) {
                        detectDragGestures { change, _ ->
                            change.consume()

                            val centerX = size.width / 2f
                            val centerY = size.height / 2f

                            val dx = change.position.x - centerX
                            val dy = change.position.y - centerY
                            val distance = sqrt(dx * dx + dy * dy)

                            val outerRadius = circleDiameter / 2f
                            val innerRadius = outerRadius - circleThickness

                            circleIndicatorOffset = when {
                                distance in innerRadius..outerRadius -> change.position

                                distance < innerRadius -> {
                                    val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                                    Offset(centerX + innerRadius * cos(angle), centerY + innerRadius * sin(angle))
                                }

                                else -> {
                                    val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                                    Offset(centerX + outerRadius * cos(angle), centerY + outerRadius * sin(angle))
                                }
                            }
                        }
                    }
                )

                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    if (circleIndicatorOffset.isSpecified) {
                        drawCircle(
                            color = if (circleColor.luminance() > .5f) Color.Black else Color.White,
                            radius = indicatorRadius,
                            center = circleIndicatorOffset,
                            style = Stroke(width = 1f)
                        )
                    }
                }
            }
        }

        paletteBitmap?.let { bitmap ->
            Box(
                modifier = Modifier.size(paletteSide.dp, paletteSide.dp),
                contentAlignment = Alignment.Center
            ) {
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
                    if (paletteIndicatorOffset.isSpecified) {
                        drawCircle(
                            color = if (paletteColor.luminance() > .5f) Color.Black else Color.White,
                            radius = indicatorRadius,
                            center = paletteIndicatorOffset,
                            style = Stroke(width = 1f)
                        )
                    }
                }
            }
        }
    }
}