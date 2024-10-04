package library

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun CanvasDrawScope.createBitmap(size: Size, content: DrawScope.() -> Unit) = runCatching {
    ImageBitmap(size.width.toInt(), size.height.toInt()).apply {
        draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(this),
            size = size
        ) {
            content()
        }
    }
}

internal suspend fun CanvasDrawScope.createBitmapAsync(
    size: Size,
    coroutineContext: CoroutineContext = Dispatchers.Default + Job(),
    content: DrawScope.() -> Unit,
) = runCatching {
    withContext(coroutineContext) {
        ImageBitmap(size.width.toInt(), size.height.toInt()).apply {
            draw(
                density = Density(1f),
                layoutDirection = LayoutDirection.Ltr,
                canvas = Canvas(this),
                size = size
            ) {
                content()
            }
        }
    }
}

internal fun calculateCirclePercentage(
    offset: Offset,
    diameter: Float,
    indicatorRadius: Float,
): Offset {
    val radius = (diameter / 2f) - indicatorRadius

    val centerX = diameter / 2f
    val centerY = diameter / 2f

    val dx = offset.x - centerX
    val dy = offset.y - centerY
    val distance = sqrt(dx * dx + dy * dy)

    return when {
        distance > radius -> {
            val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
            Offset(
                x = centerX + radius * cos(angle),
                y = centerY + radius * sin(angle)
            )
        }

        else -> offset
    }.run {
        Offset(
            x = (x / diameter).coerceIn(0f, 1f),
            y = (y / diameter).coerceIn(0f, 1f)
        )
    }
}

internal fun calculateWheelPercentage(
    offset: Offset,
    diameter: Float,
    thickness: Float,
    indicatorRadius: Float,
) = runCatching {
    val outerRadius = (diameter / 2f) - indicatorRadius
    val innerRadius = (diameter / 2f) - thickness + indicatorRadius

    val centerX = diameter / 2f
    val centerY = diameter / 2f

    val dx = offset.x - centerX
    val dy = offset.y - centerY
    val distance = sqrt(dx * dx + dy * dy)

    when {
        distance < innerRadius -> {
            val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
            Offset(
                x = centerX + innerRadius * cos(angle), y = centerY + innerRadius * sin(angle)
            )
        }

        distance > outerRadius -> {
            val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
            Offset(
                x = centerX + outerRadius * cos(angle), y = centerY + outerRadius * sin(angle)
            )
        }

        else -> offset
    }
}

internal fun colorForPosition(pixelMap: PixelMap, position: Offset) = position.runCatching {
    pixelMap[x.toInt().coerceIn(0, pixelMap.width - 1), y.toInt().coerceIn(0, pixelMap.height - 1)]
}.getOrNull() ?: Color.Unspecified