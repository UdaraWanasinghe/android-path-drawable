package com.aureusapps.android.pathdrawable

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser

internal object DrawableParser {

    private val nonDigits = Regex("[^0-9.]")

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
        val pathData = ArrayList<String>()
        val fillColors = ArrayList<Int>()
        val strokeColors = ArrayList<Int>()
        val strokeWidths = ArrayList<Float>()

        // This is very simple parser, it doesn't support <group> tag, nested tags and other stuff
        context.resources.getXml(drawableResId).use { xml ->
            var event = xml.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event != XmlPullParser.START_TAG) {
                    event = xml.next()
                    continue
                }
                when (xml.name) {
                    "vector" -> {
                        width = xml.getAttributeValue(getAttrPosition(xml, ATTR_WIDTH))
                            .replace(nonDigits, "")
                            .toFloatOrNull()
                        height = xml.getAttributeValue(getAttrPosition(xml, ATTR_HEIGHT))
                            .replace(nonDigits, "")
                            .toFloatOrNull()
                        viewportWidth = xml.getAttributeValue(getAttrPosition(xml, ATTR_VIEWPORT_WIDTH))
                            .toFloatOrNull()
                        viewportHeight = xml.getAttributeValue(getAttrPosition(xml, ATTR_VIEWPORT_HEIGHT))
                            .toFloatOrNull()
                    }
                    "path" -> {
                        val pathDataStr = xml.getAttributeValue(getAttrPosition(xml, ATTR_PATH_DATA))
                            ?: throwException("Invalid path data.")
                        pathData.add(pathDataStr)
                        val fillColor = getAttrColor(context, xml, ATTR_FILL_COLOR, ATTR_FILL_ALPHA)
                        fillColors.add(fillColor)
                        val strokeColor = getAttrColor(context, xml, ATTR_STROKE_COLOR, ATTR_STROKE_ALPHA)
                        strokeColors.add(strokeColor)
                    }
                }
                event = xml.next()
            }
        }

        val pathList = pathData.map { PathParser.createPathFromPathData(it) }.toList()

        return DrawableData(
            width ?: throwException("Path width is not defined."),
            height ?: throwException("Path height is not defined."),
            viewportWidth ?: throwException("Path viewport width is not defined."),
            viewportHeight ?: throwException("Path viewport height is not defined."),
            pathData,
            fillColors,
            pathList
        )
    }

    private fun getAttrPosition(xml: XmlPullParser, attrName: String): Int {
        return (0 until xml.attributeCount)
            .firstOrNull { i -> xml.getAttributeName(i) == attrName } ?: -1
    }

    private fun getAttrColor(
        context: Context,
        xml: XmlResourceParser,
        colorAttrName: String,
        alphaAttrName: String,
        defColor: Int = Color.BLACK
    ): Int? {
        return try {
            val attrIndex = getAttrPosition(xml, colorAttrName)
            if (attrIndex < 0) {
                null
            } else {
                val attrValue = xml.getAttributeValue(attrIndex)
                val color = when {
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
                            defColor
                        }
                    }
                    else -> {
                        xml.getAttributeUnsignedIntValue(attrIndex, defColor)
                    }
                }
                val alphaAttrIndex = getAttrPosition(xml, alphaAttrName)
                if (alphaAttrIndex < 0) {
                    color
                } else {
                    val alpha = (xml.getAttributeFloatValue(alphaAttrIndex, 1f) * 255).toInt()
                    (color and 0x00ffffff) or (alpha shl 24)
                }
            }
        } catch (e: Exception) {
            return defColor
        }
    }

    private fun getFloatAttr(resources: Resources, xml: XmlResourceParser, attrName: String, defValue: Float): Float {
        val attrIndex = getAttrPosition(xml, attrName)
        return if (attrIndex < 0) {
            defValue
        } else {
            xml.getAttributeFloatValue(attrIndex, defValue)
        }
    }

    private fun throwException(s: String): Nothing {
        throw Exception("Invalid path drawable. $s")
    }

}