package shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal class TriangleShape(private val vertices: Array<Offset>) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ) = Outline.Generic(Path().apply {
        moveTo(vertices[0].x, vertices[0].y)
        lineTo(vertices[1].x, vertices[1].y)
        lineTo(vertices[2].x, vertices[2].y)
        close()
    })
}