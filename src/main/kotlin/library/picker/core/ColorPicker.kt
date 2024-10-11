package library.picker.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import library.picker.CircularColorPicker
import library.picker.RectangularColorPicker
import library.picker.WheelRectangularColorPicker
import library.picker.WheelTriangularColorPicker

sealed interface ColorPicker {
    val name: String
    val content: @Composable () -> Unit

    companion object {
        const val DEFAULT_THICKNESS_PERCENTAGE = .2f
        const val DEFAULT_INDICATOR_THICKNESS = 1f
        const val DEFAULT_INDICATOR_RADIUS = 4f
    }

    data class Circular(
        val modifier: Modifier,
        val indicatorThickness: Float = DEFAULT_INDICATOR_THICKNESS,
        val indicatorRadius: Float = DEFAULT_INDICATOR_RADIUS,
        val onColorChange: (Color) -> Unit,
    ) : ColorPicker {
        override val name: String
            get() = "Circular"

        override val content: @Composable () -> Unit
            get() = {
                CircularColorPicker(
                    modifier = modifier,
                    indicatorThickness = indicatorThickness,
                    indicatorRadius = indicatorRadius,
                    onColorChange = onColorChange
                )
            }
    }

    data class Rectangular(
        val modifier: Modifier,
        val indicatorThickness: Float = DEFAULT_INDICATOR_THICKNESS,
        val indicatorRadius: Float = DEFAULT_INDICATOR_RADIUS,
        val onColorChange: (Color) -> Unit,
    ) : ColorPicker {
        override val name: String
            get() = "Rectangular"

        override val content: @Composable () -> Unit
            get() = {
                RectangularColorPicker(
                    modifier = modifier,
                    indicatorThickness = indicatorThickness,
                    indicatorRadius = indicatorRadius,
                    onColorChange = onColorChange
                )
            }
    }

    sealed interface Wheel : ColorPicker {
        data class Triangular(
            val modifier: Modifier,
            val indicatorThickness: Float = DEFAULT_INDICATOR_THICKNESS,
            val indicatorRadius: Float = DEFAULT_INDICATOR_RADIUS,
            val wheelThicknessPercentage: Float = DEFAULT_THICKNESS_PERCENTAGE,
            val isRotating: Boolean = false,
            val onColorChange: (Color) -> Unit,
        ) : Wheel {
            override val name: String
                get() = "Wheel triangular"

            override val content: @Composable () -> Unit
                get() = {
                    WheelTriangularColorPicker(
                        modifier = modifier,
                        indicatorThickness = indicatorThickness,
                        indicatorRadius = indicatorRadius,
                        wheelThicknessPercentage = wheelThicknessPercentage,
                        isRotating = isRotating,
                        onColorChange = onColorChange
                    )
                }
        }

        data class Rectangular(
            val modifier: Modifier,
            val indicatorThickness: Float = DEFAULT_INDICATOR_THICKNESS,
            val wheelThicknessPercentage: Float = DEFAULT_THICKNESS_PERCENTAGE,
            val indicatorRadius: Float = DEFAULT_INDICATOR_RADIUS,
            val isRotating: Boolean,
            val onColorChange: (Color) -> Unit,
        ) : Wheel {
            override val name: String
                get() = "Wheel rectangular"

            override val content: @Composable () -> Unit
                get() = {
                    WheelRectangularColorPicker(
                        modifier = modifier,
                        indicatorThickness = indicatorThickness,
                        indicatorRadius = indicatorRadius,
                        wheelThicknessPercentage = wheelThicknessPercentage,
                        isRotating = isRotating,
                        onColorChange = onColorChange
                    )
                }
        }
    }
}