package com.aureusapps.android.pathdrawable

import android.graphics.Path
import androidx.core.graphics.PathParser

data class DrawableData(
    val width: Float,
    val height: Float,
    val viewportWidth: Float,
    val viewportHeight: Float,
    val pathData: List<Path>,
    val fillColors: List<Int>,
    val strokeColors: List<Int>,
    val strokeWidths: List<Float>
)