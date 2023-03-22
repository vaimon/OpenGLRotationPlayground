package ru.mmcs.openglrotationplayground

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import ru.mmcs.openglnextplayground.GLRenderer
import ru.mmcs.openglnextplayground.OpenGLView
import ru.mmcs.openglnextplayground.RotationCenter

class MainActivity : AppCompatActivity() {
    private lateinit var glView: OpenGLView
    private lateinit var btnObject: Button
    private lateinit var btnScene: Button
    private lateinit var btnCube: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        glView = findViewById(R.id.glView)

        btnCube = findViewById(R.id.btnCube)
        btnScene = findViewById(R.id.btnScene)
        btnObject = findViewById(R.id.btnObject)
        setupListeners()
        onModeChanged()
    }

    private fun setupListeners() {
        btnObject.setOnClickListener {
            GLRenderer.rotationCenter = RotationCenter.Object
            onModeChanged()
        }
        btnCube.setOnClickListener {
            GLRenderer.rotationCenter = RotationCenter.Cubes
            onModeChanged()
        }
        btnScene.setOnClickListener {
            GLRenderer.rotationCenter = RotationCenter.World
            onModeChanged()
        }
    }

    fun onModeChanged(){
        btnCube.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(resources, if (GLRenderer.rotationCenter == RotationCenter.Cubes) R.color.purple_200 else R.color.purple_500, null)))
        btnScene.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(resources, if (GLRenderer.rotationCenter == RotationCenter.World) R.color.purple_200 else R.color.purple_500, null)))
        btnObject.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(resources, if (GLRenderer.rotationCenter == RotationCenter.Object) R.color.purple_200 else R.color.purple_500, null)))
    }
}