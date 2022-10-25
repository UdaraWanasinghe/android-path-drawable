package com.aureusapps.android.pathdrawable.painters

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import com.aureusapps.android.pathdrawable.DrawableData

class SingleColorPainter : DrawablePainter {

    private val segmentPaint: Paint = Paint()
    private val segmentPath = Path()

    override fun draw(canvas: Canvas, data: DrawableData, transform: Matrix) {
        val pathList = data.pathList
        for (index in pathList.indices) {
            segmentPaint.reset()
            segmentPaint.isAntiAlias = true
            segmentPaint.color = data.colorData[index]
            segmentPath.set(pathList[index])
            segmentPath.transform(transform)
            canvas.drawPath(segmentPath, segmentPaint)
        }
    }
}