package com.aureusapps.android.pathdrawable.painters

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import com.aureusapps.android.pathdrawable.DrawableData

@Suppress("MemberVisibilityCanBePrivate")

class SegmentColorPainter(
    private val defaultColor: Int? = null,
    private val segmentColors: ArrayList<Int?> = ArrayList()
) : DrawablePainter {

    private val segmentPaint = Paint()
    private val segmentPath = Path()

    override fun draw(canvas: Canvas, data: DrawableData, transform: Matrix) {
        data.pathList.forEachIndexed { index, path ->
            // update path
            segmentPath.reset()
            segmentPath.set(path)
            segmentPath.transform(transform)
            // update paint
            segmentPaint.reset()
            segmentPaint.isAntiAlias = true
            segmentPaint.color = defaultColor ?: data.colorData[index]
            // draw path
            if (index < segmentColors.size) {
                val color = segmentColors[index]
                if (color != null) {
                    segmentPaint.color = color
                }
            }
            canvas.drawPath(segmentPath, segmentPaint)
        }
    }

    fun updateSegmentColor(index: Int, color: Int) {
        while (segmentColors.size <= index) {
            segmentColors.add(null)
        }
        segmentColors[index] = color
    }

}