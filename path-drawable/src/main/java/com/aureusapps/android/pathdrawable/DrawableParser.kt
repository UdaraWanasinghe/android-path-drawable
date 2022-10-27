package com.aureusapps.android.pathdrawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import android.util.Xml
import androidx.annotation.DrawableRes
import androidx.core.graphics.PathParser
import com.aureusapps.android.extensions.*
import kotlin.math.roundToInt

internal object DrawableParser {

    private const val ATTR_WIDTH = "width"
    private const val ATTR_HEIGHT = "height"
    private const val ATTR_VIEWPORT_WIDTH = "viewportWidth"
    private const val ATTR_VIEWPORT_HEIGHT = "viewportHeight"
    private const val ATTR_PATH_DATA = "pathData"
    private const val ATTR_FILL_COLOR = "fillColor"
    private const val ATTR_FILL_ALPHA = "fillAlpha"
    private const val ATTR_STROKE_COLOR = "strokeColor"
    private const val ATTR_STROKE_ALPHA = "strokeAlpha"
    private const val ATTR_STROKE_WIDTH = "strokeWidth"

    @SuppressLint("ResourceType")
    fun parseVectorDrawable(
        context: Context,
        @DrawableRes drawableResId: Int
    ): DrawableData {
        var width: Float? = null
        var height: Float? = null
        var viewportWidth: Float? = null
        var viewportHeight: Float? = null
        val pathData = ArrayList<Path>()
        val fillColors = ArrayList<Int?>()
        val fillAlphas = ArrayList<Int?>()
        val strokeColors = ArrayList<Int?>()
        val strokeAlphas = ArrayList<Int?>()
        val strokeWidths = ArrayList<Float?>()

        context.resources.getXml(drawableResId).use { xml ->
            xml.forEachTag { parser ->
                when (parser.name) {
                    "vector" -> {
                        val attr = Xml.asAttributeSet(xml)
                        width = attr.getDimensionAttribute(context, ATTR_WIDTH)
                        height = attr.getDimensionAttribute(context, ATTR_HEIGHT)
                        viewportWidth = attr.getFloatAttribute(context, ATTR_VIEWPORT_WIDTH)
                        viewportHeight = attr.getFloatAttribute(context, ATTR_VIEWPORT_HEIGHT)
                    }
                    "path" -> {
                        val attr = Xml.asAttributeSet(xml)
                        // path data
                        val p = attr.getAttributeValue(attr.getAttributePosition(ATTR_PATH_DATA))
                            ?: throw Exception("path data is null")
                        pathData.add(PathParser.createPathFromPathData(p))
                        // fill alpha
                        val fillAlpha = attr.getFloatAttribute(context, ATTR_FILL_ALPHA)
                        if (fillAlpha != null) {
                            val a = (fillAlpha * 255).roundToInt()
                            fillAlphas.add(a)
                        } else {
                            fillAlphas.add(null)
                        }
                        // fill color
                        val fillColor = attr.getColorAttribute(context, ATTR_FILL_COLOR)
                        if (fillColor != null) {
                            fillColors.add(fillColor)
                        } else {
                            fillColors.add(null)
                        }
                        // stroke alpha
                        val strokeAlpha = attr.getFloatAttribute(context, ATTR_STROKE_ALPHA)
                        if (strokeAlpha != null) {
                            val a = (strokeAlpha * 255).roundToInt()
                            strokeAlphas.add(a)
                        } else {
                            strokeAlphas.add(null)
                        }
                        // stroke color
                        val strokeColor = attr.getColorAttribute(context, ATTR_STROKE_COLOR)
                        if (strokeColor != null) {
                            strokeColors.add(strokeColor)
                        } else {
                            strokeColors.add(null)
                        }
                        // stroke width
                        val strokeWidth = attr.getDimensionAttribute(context, ATTR_STROKE_WIDTH)
                        if (strokeWidth != null) {
                            strokeWidths.add(strokeWidth)
                        } else {
                            strokeWidths.add(null)
                        }
                    }
                }
            }
        }

        return DrawableData(
            width ?: throw Exception("Path width is not defined."),
            height ?: throw Exception("Path height is not defined."),
            viewportWidth ?: throw Exception("Path viewport width is not defined."),
            viewportHeight ?: throw Exception("Path viewport height is not defined."),
            pathData,
            fillAlphas,
            fillColors,
            strokeAlphas,
            strokeColors,
            strokeWidths
        )
    }

}