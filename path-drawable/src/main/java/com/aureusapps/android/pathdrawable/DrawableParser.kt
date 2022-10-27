package com.aureusapps.android.pathdrawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.util.Xml
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
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

    private val dimensionMap = mapOf(
        "px" to TypedValue.COMPLEX_UNIT_PX,
        "dip" to TypedValue.COMPLEX_UNIT_DIP,
        "dp" to TypedValue.COMPLEX_UNIT_DIP,
        "sp" to TypedValue.COMPLEX_UNIT_SP,
        "pt" to TypedValue.COMPLEX_UNIT_PT,
        "in" to TypedValue.COMPLEX_UNIT_IN,
        "mm" to TypedValue.COMPLEX_UNIT_MM
    )

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
            var event = xml.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event != XmlPullParser.START_TAG) {
                    event = xml.next()
                    continue
                }
                when (xml.name) {
                    "vector" -> {
                        val attr = Xml.asAttributeSet(xml)
                        width = attr.getDimenAttr(context, ATTR_WIDTH)
                        height = attr.getDimenAttr(context, ATTR_HEIGHT)
                        viewportWidth = attr.getFloatAttr(context, ATTR_VIEWPORT_WIDTH)
                        viewportHeight = attr.getFloatAttr(context, ATTR_VIEWPORT_HEIGHT)
                    }
                    "path" -> {
                        val attr = Xml.asAttributeSet(xml)
                        // path data
                        val p = attr.getAttributeValue(attr.getAttrPos(ATTR_PATH_DATA)) ?: throw Exception("path data is null")
                        pathData.add(PathParser.createPathFromPathData(p))
                        // fill alpha
                        val fillAlpha = attr.getFloatAttr(context, ATTR_FILL_ALPHA)
                        if (fillAlpha != null) {
                            val a = (fillAlpha * 255).roundToInt()
                            fillAlphas.add(a)
                        } else {
                            fillAlphas.add(null)
                        }
                        // fill color
                        val fillColor = attr.getColorAttr(context, ATTR_FILL_COLOR)
                        if (fillColor != null) {
                            fillColors.add(fillColor)
                        } else {
                            fillColors.add(null)
                        }
                        // stroke alpha
                        val strokeAlpha = attr.getFloatAttr(context, ATTR_STROKE_ALPHA)
                        if (strokeAlpha != null) {
                            val a = (strokeAlpha * 255).roundToInt()
                            strokeAlphas.add(a)
                        } else {
                            strokeAlphas.add(null)
                        }
                        // stroke color
                        val strokeColor = attr.getColorAttr(context, ATTR_STROKE_COLOR)
                        if (strokeColor != null) {
                            strokeColors.add(strokeColor)
                        } else {
                            strokeColors.add(null)
                        }
                        // stroke width
                        val strokeWidth = attr.getDimenAttr(context, ATTR_STROKE_WIDTH)
                        if (strokeWidth != null) {
                            strokeWidths.add(strokeWidth)
                        } else {
                            strokeWidths.add(null)
                        }
                    }
                }
                event = xml.next()
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

    private fun AttributeSet.getColorAttr(
        context: Context,
        attrName: String,
        defaultColor: Int = Color.BLACK
    ): Int? {
        return try {
            val attrIndex = getAttrPos(attrName)
            if (attrIndex < 0) {
                null
            } else {
                val attrValue = getAttributeValue(attrIndex)
                return when {
                    attrValue.contains("@") -> {
                        val colorResId = attrValue.replace("@", "").toInt()
                        ContextCompat.getColor(context, colorResId)
                    }
                    attrValue.contains("?") -> {
                        val colorAttrId = attrValue.replace("?", "").toInt()
                        val outValue = TypedValue()
                        if (context.theme.resolveAttribute(colorAttrId, outValue, true)) {
                            outValue.data
                        } else {
                            defaultColor
                        }
                    }
                    else -> {
                        getAttributeUnsignedIntValue(attrIndex, defaultColor)
                    }
                }
            }
        } catch (e: Exception) {
            return defaultColor
        }
    }

    private fun AttributeSet.getFloatAttr(context: Context, attrName: String): Float? {
        val attrIndex = getAttrPos(attrName)
        return if (attrIndex < 0) {
            null
        } else {
            val attrValue = getAttributeValue(attrIndex)
            return when {
                attrValue.startsWith("@") -> {
                    val floatResId = attrValue.replace("@", "").toInt()
                    context.resources.getDimension(floatResId)
                }
                attrValue.startsWith("?") -> {
                    val floatAttrId = attrValue.replace("?", "").toInt()
                    val outValue = TypedValue()
                    if (context.theme.resolveAttribute(floatAttrId, outValue, true)) {
                        outValue.float
                    } else {
                        null
                    }
                }
                else -> {
                    getAttributeFloatValue(attrIndex, 0f)
                }
            }
        }
    }

    private fun AttributeSet.getDimenAttr(context: Context, attrName: String): Float? {
        val attrIndex = getAttrPos(attrName)
        if (attrIndex < 0) {
            return null
        }
        val attrValue = getAttributeValue(attrIndex)
        return when {
            attrValue.startsWith("@") -> {
                val dimenResId = attrValue.replace("@", "").toInt()
                context.resources.getDimension(dimenResId)
            }
            attrValue.startsWith("?") -> {
                val dimenAttrId = attrValue.replace("?", "").toInt()
                val outValue = TypedValue()
                if (context.theme.resolveAttribute(dimenAttrId, outValue, true)) {
                    outValue.getDimension(context.resources.displayMetrics)
                } else {
                    null
                }
            }
            else -> {
                val dimenUnit = dimensionMap.entries.firstOrNull { attrValue.endsWith(it.key) }
                if (dimenUnit == null) {
                    return attrValue.toFloat()
                } else {
                    return TypedValue.applyDimension(
                        dimenUnit.value,
                        attrValue.replace(dimenUnit.key, "").toFloat(),
                        context.resources.displayMetrics
                    )
                }
            }
        }
    }

    private fun AttributeSet.getAttrPos(attrName: String): Int {
        return (0 until attributeCount)
            .firstOrNull { i -> getAttributeName(i) == attrName } ?: -1
    }
}