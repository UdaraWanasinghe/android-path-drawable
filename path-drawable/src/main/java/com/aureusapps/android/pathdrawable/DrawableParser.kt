package com.aureusapps.android.pathdrawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Path
import androidx.annotation.DrawableRes
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
import kotlin.math.roundToInt

@SuppressLint("ResourceType")
internal object DrawableParser {

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
                        context.obtainStyledAttributes(
                            xml,
                            intArrayOf(
                                android.R.attr.width,
                                android.R.attr.height,
                                android.R.attr.viewportWidth,
                                android.R.attr.viewportHeight
                            )
                        ).apply {
                            width = getDimension(0, 0f)
                            height = getDimension(1, 0f)
                            viewportWidth = getFloat(2, 0f)
                            viewportHeight = getFloat(3, 0f)
                            recycle()
                        }

                    }
                    "path" -> {
                        context.obtainStyledAttributes(
                            xml,
                            intArrayOf(
                                android.R.attr.pathData,
                                android.R.attr.fillAlpha,
                                android.R.attr.fillColor,
                                android.R.attr.strokeAlpha,
                                android.R.attr.strokeColor,
                                android.R.attr.strokeWidth
                            )
                        ).apply {
                            // path data
                            val p = getString(0) ?: throw Exception("Invalid path drawable.")
                            pathData.add(PathParser.createPathFromPathData(p))
                            // fill alpha
                            if (hasValue(1)) {
                                val a = (getFloat(1, 1f) * 255).roundToInt()
                                fillAlphas.add(a)
                            } else {
                                fillAlphas.add(null)
                            }
                            // fill color
                            if (hasValue(2)) {
                                val c = getColor(2, Color.BLACK)
                                fillColors.add(c)
                            } else {
                                fillColors.add(null)
                            }
                            // stroke alpha
                            if (hasValue(3)) {
                                val a = (getFloat(3, 1f) * 255).roundToInt()
                                strokeAlphas.add(a)
                            } else {
                                strokeAlphas.add(null)
                            }
                            // stroke color
                            if (hasValue(4)) {
                                val c = getColor(4, Color.BLACK)
                                strokeColors.add(c)
                            } else {
                                strokeColors.add(null)
                            }
                            // stroke width
                            if (hasValue(5)) {
                                val w = getDimension(5, 0f)
                                strokeWidths.add(w)
                            } else {
                                strokeWidths.add(null)
                            }
                            recycle()
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

}