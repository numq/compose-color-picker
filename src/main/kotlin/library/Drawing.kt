package library

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

fun DrawScope.drawRectangle(color: Color) {
    drawRect(brush = Brush.verticalGradient(colors = listOf(Color.White, color, Color.Black)))
}

fun DrawScope.drawRectangleRGB() {
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow, Color.Red
            )
        )
    )
    drawRect(brush = Brush.verticalGradient(colors = listOf(Color.White, Color.Transparent, Color.Black)))
}

fun DrawScope.drawCircle() {
    val diameter = minOf(size.width, size.height)
    val radius = diameter / 2f
    val center = Offset(radius, radius)

    drawCircle(color = Color.Black, radius = radius, center = center)

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
        ), radius = radius, center = center
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, Color.Transparent), radius = radius, center = center
        )
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black), radius = radius * .95f, center = center
        )
    )
}

fun DrawScope.drawWheel(thicknessPercentage: Float) {
    val diameter = minOf(size.width, size.height)
    val thickness = diameter * thicknessPercentage
    drawCircle(
        brush = Brush.sweepGradient(
            colors = listOf(
                Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow, Color.Red
            )
        ),
        radius = (diameter - thickness) / 2f,
        style = Stroke(width = thickness),
    )
}

fun DrawScope.drawSlider() {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow, Color.Red
            )
        )
    )
}

fun DrawScope.drawSliderArrow(width: Float, offset: Offset) {
    val startX = offset.x
    val startY = offset.y - width / 2

    val path = Path().apply {
        moveTo(startX, startY)
        lineTo(startX + width, startY + width / 2)
        lineTo(startX, startY + width)
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

fun DrawScope.drawIndicator(radius: Float, offset: Offset, strokeWidth: Float = 2f) {
    drawCircle(
        color = Color.White, radius = radius, center = offset, style = Stroke(width = strokeWidth / 2)
    )
    drawCircle(
        color = Color.Black, radius = radius - 1f, center = offset, style = Stroke(width = strokeWidth / 2)
    )
}