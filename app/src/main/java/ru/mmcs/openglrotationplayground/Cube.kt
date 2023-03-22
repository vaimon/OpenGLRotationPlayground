package ru.mmcs.openglnextplayground

import android.opengl.GLES30
import android.util.Log
import ru.mmcs.openglrotationplayground.Point
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube(var center: Point, val radius: Float, val color: FloatArray = floatArrayOf(0.7f, 1.0f, 0.0f, 1.0f)) {
    private val vertexShader = """#version 300 es
        in vec3 vPosition;
        uniform mat4 uMVPMatrix;
        
        uniform vec3 object_center;
        uniform vec3 this_center;
        uniform float world_angle;
        uniform float object_angle;
        uniform float this_angle;
        
        void main() {        
            vec3 position = (vPosition - this_center) * mat3(
                cos(this_angle), 0, sin(this_angle),
                0, 1, 0,
                -sin(this_angle), 0, cos(this_angle)
            ) + this_center;
            
            position = (position - object_center) * mat3(
                cos(object_angle), 0, sin(object_angle),
                0, 1, 0,
                -sin(object_angle), 0, cos(object_angle)
            ) + object_center;
            
            position = position * mat3(
                cos(world_angle), 0, sin(world_angle),
                0, 1, 0,
                -sin(world_angle), 0, cos(world_angle)
            );
            
            gl_Position = uMVPMatrix * vec4(position, 1.0);
        }
    """.trimIndent()

    private val fragmentShader = """#version 300 es
        precision mediump float;
        uniform vec4 vColor;
        
        out vec4 fragColor;
        void main() {
            fragColor = vColor;
        }
    """.trimIndent()

    private var glProgramId: Int

    init {
        Log.d("GL_DEBUG", "Shader compilation started")
        val vertexShaderId = GLRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderId = GLRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader)

        glProgramId = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShaderId)
            GLES30.glAttachShader(it, fragmentShaderId)
            GLES30.glLinkProgram(it)
        }
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }

    val vertices = floatArrayOf(
        center.x-radius, center.y-radius, center.z+radius,  center.x-radius, center.y+radius, center.z+radius,  center.x+radius, center.y+radius, center.z+radius,
        center.x+radius, center.y+radius, center.z+radius,  center.x+radius, center.y-radius, center.z+radius,  center.x-radius, center.y-radius, center.z+radius,
        center.x-radius, center.y-radius, center.z-radius,  center.x+radius, center.y+radius, center.z-radius,  center.x-radius, center.y+radius, center.z-radius,
        center.x+radius, center.y+radius, center.z-radius,  center.x-radius, center.y-radius, center.z-radius,  center.x+radius, center.y-radius, center.z-radius,

        center.x-radius, center.y+radius, center.z-radius,  center.x-radius, center.y+radius, center.z+radius,  center.x+radius, center.y+radius, center.z+radius,
        center.x+radius, center.y+radius, center.z+radius,  center.x+radius, center.y+radius, center.z-radius,  center.x-radius, center.y+radius, center.z-radius,
        center.x-radius, center.y-radius, center.z-radius,  center.x+radius, center.y-radius, center.z+radius,  center.x-radius, center.y-radius, center.z+radius,
        center.x+radius, center.y-radius, center.z+radius,  center.x-radius, center.y-radius, center.z-radius,  center.x+radius, center.y-radius, center.z-radius,

        center.x+radius, center.y-radius, center.z-radius,  center.x+radius, center.y-radius, center.z+radius,  center.x+radius, center.y+radius, center.z+radius,
        center.x+radius, center.y+radius, center.z+radius,  center.x+radius, center.y+radius, center.z-radius,  center.x+radius, center.y-radius, center.z-radius,
        center.x-radius, center.y-radius, center.z-radius,  center.x-radius, center.y+radius, center.z+radius,  center.x-radius, center.y-radius, center.z+radius,
        center.x-radius, center.y+radius, center.z+radius,  center.x-radius, center.y-radius, center.z-radius,  center.x-radius, center.y+radius, center.z-radius,
    )

    var worldAngle = 0.0f
    var objectAngle = 0.0f
    var thisAngle = 0.0f

    var worldCenter = Point(0.0f, 0.0f, 0.0f)
    var objectCenter = Point(1f, 0f, 0f)

    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

    private var vPositionHandle: Int = 0
    private var vColorHandle: Int = 0
    private var uMVPMatrixHandle: Int = 0
    private var worldAngleHandle: Int = 0
    private var thisAngleHandle: Int = 0
    private var objectAngleHandle: Int = 0
    private var objectCenterHandle: Int = 0
    private var thisCenterHandle: Int = 0

    private val vertexCount: Int = vertices.size / 3
    private val vertexStride: Int = 12

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(glProgramId)

        vPositionHandle = GLES30.glGetAttribLocation(glProgramId, "vPosition").also {
            GLES30.glEnableVertexAttribArray(it)
            GLES30.glVertexAttribPointer(
                it,
                3,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        }

        vColorHandle = GLES30.glGetUniformLocation(glProgramId, "vColor")
        GLES30.glUniform4fv(vColorHandle, 1, color, 0)

        uMVPMatrixHandle = GLES30.glGetUniformLocation(glProgramId, "uMVPMatrix")
        GLES30.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0)

        worldAngleHandle = GLES30.glGetUniformLocation(glProgramId, "world_angle")
        GLES30.glUniform1f(worldAngleHandle, worldAngle)

        thisAngleHandle = GLES30.glGetUniformLocation(glProgramId, "this_angle")
        GLES30.glUniform1f(thisAngleHandle, thisAngle)

        objectAngleHandle = GLES30.glGetUniformLocation(glProgramId, "object_angle")
        GLES30.glUniform1f(objectAngleHandle, objectAngle)

        objectCenterHandle = GLES30.glGetUniformLocation(glProgramId, "object_center")
        GLES30.glUniform3fv(objectCenterHandle, 1, objectCenter.toFloatArray(), 0)

        thisCenterHandle = GLES30.glGetUniformLocation(glProgramId, "this_center")
        GLES30.glUniform3fv(thisCenterHandle, 1, center.toFloatArray(), 0)

        Log.d("OPENGL", "$center $objectCenter $worldCenter")

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
        GLES30.glDisableVertexAttribArray(vPositionHandle)
    }
}
