package ru.mmcs.openglnextplayground

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

enum class RotationCenter { ComplexObject, World, SingleObject}
enum class LightingMode(val tag: String) { Lambert("lambert"), Phong("phong"), TexturePhong("texture") }
enum class ShadingMode(val tag: String) { Goureaux("goureaux"), Phong("phong") }

class OpenGLView(context: Context, attributes: AttributeSet) : GLSurfaceView(context, attributes) {
    private val renderer: GLRenderer
    private val TOUCH_SCALE_FACTOR: Float = 1f / 100f
    private var previousX: Float = 0f

    init{
        setEGLContextClientVersion(3)
        renderer = GLRenderer(context)
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x: Float = event!!.x

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = x - previousX

                renderer.addAngle(dx * TOUCH_SCALE_FACTOR)
                requestRender()
            }
        }

        previousX = x
        return true
    }

    fun updateLightingModel(){
        renderer.updateLightingModel()
        requestRender()
    }
}