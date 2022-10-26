package com.aureusapps.android.pathdrawable

import android.graphics.Path

internal data class DrawableData(
    val width: Float,
    val height: Float,
    val viewportWidth: Float,
    val viewportHeight: Float,
    val pathData: List<Path>,
    val fillAlphas: List<Int?>,
    val fillColors: List<Int?>,
    val strokeAlphas: List<Int?>,
    val strokeColors: List<Int?>,
    val strokeWidths: List<Float?>
) {

    fun getFillColor(index: Int, default: Int? = null): Int? {
        val color = fillColors[index] ?: default ?: return null
        val alpha = fillAlphas[index]
        return if (alpha != null) {
            (color and 0x00FFFFFF) or (alpha shl 24)
        } else {
            color
        }
    }

    fun getStrokeColor(index: Int, default: Int? = null): Int? {
        val color = strokeColors[index] ?: default ?: return null
        val alpha = strokeAlphas[index]
        return if (alpha != null) {
            (color and 0x00FFFFFF) or (alpha shl 24)
        } else {
            color
        }
    }

}