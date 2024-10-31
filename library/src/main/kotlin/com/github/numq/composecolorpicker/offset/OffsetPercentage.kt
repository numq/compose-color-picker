package com.github.numq.composecolorpicker.offset

data class OffsetPercentage(val x: Float, val y: Float) {
    init {
        require(x in 0f..1f && y in 0f..1f) { "Percentage should be within 0f..1f" }
    }
}