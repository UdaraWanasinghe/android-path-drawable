package com.aureusapps.android.pathdrawable.example

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton

open class PropertyButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialButton(context, attrs) {

    override fun setCheckable(checkable: Boolean) {}
}