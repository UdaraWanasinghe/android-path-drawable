package com.aureusapps.android.pathdrawable.example

import android.content.Context
import android.util.AttributeSet
import com.aureusapps.android.graphics.pathdrawable.painters.DrawablePainter
import com.aureusapps.android.graphics.pathdrawable.painters.SegmentColorPainter
import com.aureusapps.android.pathdrawable.PathDrawable
import com.aureusapps.android.pathdrawable.R

class ColorPickerPropertyButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : PropertyButton(context, attrs) {

    private val iconDrawable: PathDrawable
    private var iconColorArray: ArrayList<Int?>
    private val drawablePainter: DrawablePainter

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPropertyButton).apply {
            val iconDrawableRef =
                getResourceId(R.styleable.ColorPickerPropertyButton_iconDrawable, 0)
            val iconColorArrayRef =
                getResourceId(R.styleable.ColorPickerPropertyButton_iconColorArray, 0)
            iconColorArray =
                if (iconColorArrayRef != 0) ArrayList(
                    context.resources.getIntArray(
                        iconColorArrayRef
                    ).toList()
                ) else ArrayList()
            iconDrawable = PathDrawable(context, iconDrawableRef)
            drawablePainter = SegmentColorPainter()
            iconDrawable.drawablePainter = drawablePainter
            icon = iconDrawable
            recycle()
        }
    }

    /**
     * [index] segment index
     * [color] segment color
     */
    fun updateSegmentColor(index: Int, color: Int) {
        if (drawablePainter is SegmentColorPainter) {
            drawablePainter.updateSegmentColor(index, color)
        }
        invalidate()
    }

}