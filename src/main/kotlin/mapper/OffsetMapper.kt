package mapper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import kotlin.math.*

object OffsetMapper {
    internal fun mapTriangleOffset(offset: Offset, vertices: Array<Offset>) = runCatching {
        val (a, b, c) = vertices

        fun distance(p1: Offset, p2: Offset) = sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))

        fun isPointInTriangle(pt: Offset, v1: Offset, v2: Offset, v3: Offset): Boolean {
            val area = 0.5 * (-v2.y * v3.x + v1.y * (-v2.x + v3.x) + v1.x * (v2.y - v3.y) + v2.x * v3.y)
            val s = 1 / (2 * area) * (v1.y * v3.x - v1.x * v3.y + (v3.y - v1.y) * pt.x + (v1.x - v3.x) * pt.y)
            val t = 1 / (2 * area) * (v1.x * v2.y - v1.y * v2.x + (v1.y - v2.y) * pt.x + (v2.x - v1.x) * pt.y)
            return s >= 0 && t >= 0 && (s + t) <= 1
        }

        if (isPointInTriangle(offset, a, b, c)) {
            return@runCatching offset
        }

        fun closestPointOnSegment(p: Offset, v: Offset, w: Offset): Offset {
            val l2 = distance(v, w).pow(2)
            if (l2 == 0f) return v
            val t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2
            return if (t < 0) v else if (t > 1) w else Offset(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y))
        }

        listOf(
            closestPointOnSegment(offset, a, b),
            closestPointOnSegment(offset, b, c),
            closestPointOnSegment(offset, c, a)
        ).minByOrNull { distance(offset, it) } ?: offset
    }

    internal fun mapCircleOffset(
        offset: Offset,
        radius: Float,
        center: Offset,
    ) = runCatching {
        val dx = offset.x - center.x
        val dy = offset.y - center.x
        val distance = sqrt(dx * dx + dy * dy)

        when {
            distance > radius -> {
                val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                Offset(x = center.x + radius * cos(angle), y = center.y + radius * sin(angle))
            }

            else -> offset
        }
    }

    internal fun mapWheelOffset(
        offset: Offset,
        size: Size,
        thicknessPercentage: Float,
        indicatorRadius: Float,
    ) = runCatching {
        val diameter = min(size.width, size.height)
        val radius = diameter / 2f
        val outerRadius = radius - indicatorRadius
        val innerRadius = radius - (diameter * thicknessPercentage) + indicatorRadius

        val center = size.center
        val dx = offset.x - center.x
        val dy = offset.y - center.y
        val distance = sqrt(dx * dx + dy * dy)

        when {
            distance < innerRadius -> innerRadius

            distance > outerRadius -> outerRadius

            else -> distance
        }.let { constrainedRadius ->
            val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()

            Offset(
                x = center.x + constrainedRadius * cos(angle),
                y = center.y + constrainedRadius * sin(angle)
            )
        }
    }
}