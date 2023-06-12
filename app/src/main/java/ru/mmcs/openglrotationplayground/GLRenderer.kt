package ru.mmcs.openglnextplayground

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import ru.mmcs.openglrotationplayground.objects.*
import ru.mmcs.openglrotationplayground.utils.Point
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val sceneShapes = mutableListOf<RotatingObject3D>()

    private val vpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)


    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        sceneShapes.add(Cube3D(context, Point(1f,-0.5f,0f), floatArrayOf(0.97f,0.84f,0.12f,1f)))
        sceneShapes.add(Torus3D(context, Point(1f,1f,0f), floatArrayOf(0.97f,0.24f,0.12f,1f)))
        sceneShapes.add(Cube3D(context, Point(0.5f,-0.5f,0f), floatArrayOf(0.64f,0.64f,0.69f,1f)))
        sceneShapes.add(Cube3D(context, Point(1.5f,-0.5f,0f), floatArrayOf(0.78f,0.43f,0f,1f)))
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 3f, 20f)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 2f, 8f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        for(shape in sceneShapes){
            shape.draw(vpMatrix)
        }
    }

    fun addAngle(angle: Float){
        for(shape in sceneShapes){
            when(rotationCenter){
                RotationCenter.Object -> shape.objectAngle += angle
                RotationCenter.World -> shape.worldAngle += angle
                RotationCenter.Cubes -> shape.thisAngle += angle
            }
        }
    }

    companion object {
        var rotationCenter = RotationCenter.World
        var shadingMode = ShadingMode.Phong
        var lightingMode = LightingMode.Phong
        var attenuation = 0.0f
        var lightStrength = 0.0f

        fun loadShader(type: Int, shaderCode: String): Int {
            val id = GLES30.glCreateShader(type).also { shader ->
                GLES30.glShaderSource(shader, shaderCode)
                GLES30.glCompileShader(shader)
            }

            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(id, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                Log.e("GL_DEBUG", "Shader compilation error: " + GLES30.glGetShaderInfoLog(id))
//                Log.e("Shader Source : ", GLES30.glGetShaderSource(shader))
            }

            return id
        }

        fun checkGLError(tag: String = ""){
            val err = GLES30.glGetError()
            if(err != GLES30.GL_NO_ERROR){
                Log.e("GL_DEBUG","Error in opengl: $err [$tag]")
            }
        }
    }
}