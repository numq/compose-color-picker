package offset

import androidx.compose.ui.geometry.Offset
import kotlin.math.*

object OffsetMapper {
    private fun distance(p1: Offset, p2: Offset) = sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))

    private fun isPointInTriangle(pt: Offset, v1: Offset, v2: Offset, v3: Offset): Boolean {
        val area = 0.5 * (-v2.y * v3.x + v1.y * (-v2.x + v3.x) + v1.x * (v2.y - v3.y) + v2.x * v3.y)
        val s = 1 / (2 * area) * (v1.y * v3.x - v1.x * v3.y + (v3.y - v1.y) * pt.x + (v1.x - v3.x) * pt.y)
        val t = 1 / (2 * area) * (v1.x * v2.y - v1.y * v2.x + (v1.y - v2.y) * pt.x + (v2.x - v1.x) * pt.y)
        return s >= 0 && t >= 0 && (s + t) <= 1
    }

    private fun closestPointOnSegment(p: Offset, v: Offset, w: Offset): Offset {
        val l2 = distance(v, w).pow(2)
        if (l2 == 0f) return v
        val t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2
        return if (t < 0) v else if (t > 1) w else Offset(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y))
    }

    fun mapTriangularOffset(offset: Offset, vertices: Array<Offset>) = runCatching {
        val (a, b, c) = vertices

        if (isPointInTriangle(offset, a, b, c)) {
            return@runCatching offset
        }

        listOf(
            closestPointOnSegment(offset, a, b),
            closestPointOnSegment(offset, b, c),
            closestPointOnSegment(offset, c, a)
        ).minByOrNull { distance(offset, it) } ?: offset
    }.getOrNull() ?: offset

    fun mapCircularOffset(
        outerRadius: Float,
        innerRadius: Float,
        offset: Offset,
        center: Offset,
    ) = runCatching {
        val dx = offset.x - center.x
        val dy = offset.y - center.y
        val distance = sqrt(dx.pow(2) + dy.pow(2))
        val angle = atan2(dy, dx)

        val constrainedRadius = when {
            distance < innerRadius -> innerRadius

            distance > outerRadius -> outerRadius

            else -> distance
        }

        Offset(
            x = center.x + constrainedRadius * cos(angle),
            y = center.y + constrainedRadius * sin(angle)
        )
    }.getOrNull() ?: offset
}