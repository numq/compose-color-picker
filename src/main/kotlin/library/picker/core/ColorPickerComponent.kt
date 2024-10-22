package library.picker.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import library.offset.OffsetPercentage

@Composable
fun ColorPickerComponent(
    modifier: Modifier,
    mapIndicatorOffset: (PointerInputScope.(Offset) -> Offset?)? = null,
    indicatorOffsetPercentage: OffsetPercentage,
    onIndicatorOffsetPercentage: (indicatorOffsetPercentage: OffsetPercentage) -> Unit,
    indicatorContent: DrawScope.(indicatorOffset: Offset) -> Unit,
    content: DrawScope.() -> Unit,
) {
    val updatedMapIndicatorOffset by rememberUpdatedState(mapIndicatorOffset)

    val updatedOnIndicatorOffsetPercentage by rememberUpdatedState(onIndicatorOffsetPercentage)

    BoxWithConstraints(modifier = modifier) {
        val colorPickerSize = remember(maxWidth, maxHeight) {
            Size(width = maxWidth.value, height = maxHeight.value)
        }

        val indicatorOffset by remember(indicatorOffsetPercentage, colorPickerSize) {
            derivedStateOf {
                Offset(
                    x = (indicatorOffsetPercentage.x * colorPickerSize.width).coerceIn(0f, colorPickerSize.width),
                    y = (indicatorOffsetPercentage.y * colorPickerSize.height).coerceIn(0f, colorPickerSize.height)
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize().pointerInput(updatedMapIndicatorOffset) {
            detectTapGestures(onTap = { offset ->
                updatedOnIndicatorOffsetPercentage(
                    (updatedMapIndicatorOffset?.invoke(this, offset) ?: offset).let { (x, y) ->
                        OffsetPercentage(
                            x = (x / size.width).coerceIn(0f, 1f),
                            y = (y / size.height).coerceIn(0f, 1f),
                        )
                    }
                )
            })
        }.pointerInput(updatedMapIndicatorOffset) {
            detectDragGestures { change, _ ->
                change.consume()
                change.position.let { offset ->
                    updatedOnIndicatorOffsetPercentage(
                        (updatedMapIndicatorOffset?.invoke(this, offset) ?: offset).let { (x, y) ->
                            OffsetPercentage(
                                x = (x / size.width).coerceIn(0f, 1f),
                                y = (y / size.height).coerceIn(0f, 1f),
                            )
                        }
                    )
                }
            }
        }) {
            content()

            indicatorContent(indicatorOffset)
        }
    }
}