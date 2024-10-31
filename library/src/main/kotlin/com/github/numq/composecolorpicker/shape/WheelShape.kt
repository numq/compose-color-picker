package com.github.numq.composecolorpicker.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min

internal class WheelShape(
    private val outerDiameter: Float,
    private val innerDiameter: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ) = Outline.Generic(Path().apply {
        val outerRadius = min(size.width, size.height) / 2f

        val innerRadius = (innerDiameter / outerDiameter) * outerRadius

        val center = Offset(size.width / 2f, size.height / 2f)

        val outerOval = Path().apply {
            addOval(Rect(center - Offset(outerRadius, outerRadius), Size(outerRadius * 2f, outerRadius * 2f)))
        }
        val innerOval = Path().apply {
            addOval(Rect(center - Offset(innerRadius, innerRadius), Size(innerRadius * 2f, innerRadius * 2f)))
        }

        op(outerOval, innerOval, PathOperation.Difference)

        close()
    })
}