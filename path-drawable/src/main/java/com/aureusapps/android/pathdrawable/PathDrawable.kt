package com.aureusapps.android.pathdrawable

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

class PathDrawable(
    context: Context,
    @DrawableRes drawable: Int
) : Drawable() {

    private val data = DrawableParser.parseVectorDrawable(context, drawable)
    private val transform = Matrix()
    private val fillPaint = Paint()
    private val strokePaint = Paint().apply { style = Paint.Style.STROKE }
    private val drawPath = Path()
    private val segmentFillColors = mutableListOf<Int?>()
    private val segmentStrokeColors = mutableListOf<Int?>()

    override fun draw(canvas: Canvas) {
        val pathCount = data.pathData.size
        for (index in 0 until pathCount) {
            // update path
            drawPath.reset()
            drawPath.set(data.pathData[index])
            drawPath.transform(transform)

            val fillColor = segmentFillColors.getOrNull(index) ?: data.getFillColor(index)
            if (fillColor != null) {
                // update fill paint
                fillPaint.reset()
                fillPaint.isAntiAlias = true
                fillPaint.color = fillColor
                // draw fill
                canvas.drawPath(drawPath, fillPaint)
            }

            val strokeColor = segmentStrokeColors.getOrNull(index) ?: data.getStrokeColor(index)
            val strokeWidth = data.strokeWidths[index]
            if (strokeColor != null && strokeWidth != null && strokeWidth > 0) {
                // update stroke paint
                strokePaint.reset()
                strokePaint.isAntiAlias = true
                strokePaint.color = strokeColor
                strokePaint.style = Paint.Style.STROKE
                strokePaint.strokeWidth = strokeWidth
                // draw stroke
                canvas.drawPath(drawPath, strokePaint)
            }
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        val scaleX = bounds.width().toFloat() / data.viewportWidth
        val scaleY = bounds.height().toFloat() / data.viewportHeight
        transform.reset()
        transform.postScale(scaleX, scaleY)
        transform.postTranslate(bounds.left.toFloat(), bounds.top.toFloat())
    }

    override fun getIntrinsicWidth(): Int {
        return data.width.toInt()
    }

    override fun getIntrinsicHeight(): Int {
        return data.height.toInt()
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    @Deprecated("Deprecated in Java", ReplaceWith("Nothing to replace"))
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    @Suppress("unused")
    fun setSegmentFillColor(index: Int, color: Int?) {
        while (segmentFillColors.lastIndex < index) {
            segmentFillColors.add(null)
        }
        segmentFillColors[index] = color
        invalidateSelf()
    }

    @Suppress("unused")
    fun setSegmentStrokeColor(index: Int, color: Int?) {
        while (segmentStrokeColors.lastIndex < index) {
            segmentStrokeColors.add(null)
        }
        segmentStrokeColors[index] = color
        invalidateSelf()
    }

}