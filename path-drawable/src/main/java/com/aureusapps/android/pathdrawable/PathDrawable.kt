package com.aureusapps.android.pathdrawable

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.aureusapps.android.extensions.dp
import com.aureusapps.android.pathdrawable.painters.DrawablePainter
import com.aureusapps.android.pathdrawable.painters.SingleColorPainter

class PathDrawable(
    context: Context,
    @DrawableRes drawable: Int
) : Drawable() {

    private val drawableData = DrawableParser.parseVectorDrawable(context, drawable)
    private val transformMatrix = Matrix()

    var drawablePainter: DrawablePainter = SingleColorPainter()

    override fun draw(canvas: Canvas) {
        drawablePainter.draw(canvas, drawableData, transformMatrix)
    }

    override fun onBoundsChange(bounds: Rect) {
        val scaleX = bounds.width().toFloat() / drawableData.viewportWidth
        val scaleY = bounds.height().toFloat() / drawableData.viewportHeight
        transformMatrix.reset()
        transformMatrix.postScale(scaleX, scaleY)
        transformMatrix.postTranslate(bounds.left.toFloat(), bounds.top.toFloat())
    }

    override fun getIntrinsicWidth(): Int {
        return drawableData.width.toInt().dp
    }

    override fun getIntrinsicHeight(): Int {
        return drawableData.height.toInt().dp
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

}