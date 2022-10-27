package com.aureusapps.android.pathdrawable.example

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.aureusapps.android.pathdrawable.PathDrawable

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var drawable: PathDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawable = PathDrawable(this, R.drawable.ic_android_black_24dp)
        imageView = findViewById(R.id.image_view)
        imageView.background = drawable
    }

}