package library

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal fun CanvasDrawScope.createBitmap(size: Size, content: DrawScope.() -> Unit) = runCatching {
    ImageBitmap(size.width.toInt(), size.height.toInt()).apply {
        draw(
            density = Density(1f), layoutDirection = LayoutDirection.Ltr, canvas = Canvas(this), size = size
        ) {
            content()
        }
    }
}