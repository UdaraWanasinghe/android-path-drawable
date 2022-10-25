package com.aureusapps.android.pathdrawable.painters

import android.graphics.Canvas
import android.graphics.Matrix
import com.aureusapps.android.pathdrawable.DrawableData

interface DrawablePainter {
    fun draw(canvas: Canvas, data: DrawableData, transform: Matrix)
}