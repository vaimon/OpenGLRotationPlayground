package ru.mmcs.openglrotationplayground.objects

import android.opengl.GLES30
import android.util.Log
import ru.mmcs.openglnextplayground.GLRenderer
import ru.mmcs.openglrotationplayground.utils.Point
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

open class Object3D(
    objFile: InputStream,
    vertexShaderFile: InputStream,
    fragmentShaderFile: InputStream,
    var center: Point,
    val color: FloatArray = floatArrayOf(0.7f, 1.0f, 0.0f, 1.0f)
) {
    protected val vertices: FloatArray  // vert + norm + tex
    protected val vertexShader: String
    protected val fragmentShader: String

    protected var glProgramId: Int = -1
    private var VAO: IntArray = intArrayOf(0)
    private var VBO: IntArray = intArrayOf(0)

    init {
        val packedVertices = mutableListOf<Float>()
        objFile.bufferedReader().use {
            val vertices = mutableListOf<Float>()
            val textureVertices = mutableListOf<Float>()
            val normales = mutableListOf<Float>()
            for (line in it.readLines()) {
                val details = line.split(" ")
                when (details.first()) {
                    "v" -> {
                        val coords = details.drop(1).map { s -> s.toFloat() }.toMutableList()
                        coords[0] += center.x
                        coords[1] += center.y
                        coords[2] += center.z
                        vertices.addAll(coords)
                    }
                    "vt" -> {
                        textureVertices.addAll(details.drop(1).map { s -> s.toFloat() })
                    }
                    "vn" -> {
                        normales.addAll(details.drop(1).map { s -> s.toFloat() })
                    }
                    "f" -> {
                        for (vertex in details.drop(1)) {
                            val triplet = vertex.split("/")
                            val vertexIndex = (triplet[0].toInt() - 1) * 3
                            packedVertices.addAll(
                                vertices.subList(
                                    vertexIndex,
                                    vertexIndex + 3
                                )
                            )
                            val normaleIndex = (triplet[2].toInt() - 1) * 3
                            packedVertices.addAll(
                                normales.subList(
                                    normaleIndex,
                                    normaleIndex + 3
                                )
                            )
                            val textureIndex = (triplet[1].toInt() - 1) * 2
                            packedVertices.addAll(
                                textureVertices.subList(
                                    textureIndex,
                                    textureIndex + 2
                                )
                            )
                        }
                    }
                    "o" -> {}
                    else -> throw IOException(".obj file is badly formatted. Unknown tag [${details.first()}]")
                }
            }
//            Log.d("GL_DEBUG", "v: " + packedVertices.joinToString(" "))
        }
        this.vertices = packedVertices.toFloatArray()
        fragmentShader = fragmentShaderFile
            .bufferedReader().use {
                it.readText()
            }
        vertexShader = vertexShaderFile
            .bufferedReader().use {
                it.readText()
            }

        compileShaders()
        initBuffers()
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }

    fun compileShaders() {
        val vertexShaderId = GLRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderId = GLRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader)

        glProgramId = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShaderId)
            GLES30.glAttachShader(it, fragmentShaderId)
            GLES30.glLinkProgram(it)
        }

        GLRenderer.checkGLError("shader compilation")
    }

    private var vPositionHandle : Int = 0
    private var vTextureHandle : Int = 0
    private var vNormaleHandle : Int = 0
    private var vColorHandle : Int = 0

    private var uMVPMatrixHandle : Int = 0

    fun initBuffers(){
        GLES30.glGenVertexArrays(1, VAO, 0)
        GLES30.glGenBuffers(1, VBO, 0)

        GLES30.glBindVertexArray(VAO[0])


        vPositionHandle = GLES30.glGetAttribLocation(glProgramId, "vertexPosition").also {
            GLES30.glEnableVertexAttribArray(it)
        }
        vNormaleHandle = GLES30.glGetAttribLocation(glProgramId, "vertexNormale").also {
            GLES30.glEnableVertexAttribArray(it)
        }
        vTextureHandle = GLES30.glGetAttribLocation(glProgramId, "vertexTextureCoords").also {
            GLES30.glEnableVertexAttribArray(it)
        }

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertices.size * Float.SIZE_BYTES,
            ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            },
            GLES30.GL_STATIC_DRAW
        )

        GLES30.glVertexAttribPointer(
                vPositionHandle,
                3,
                GLES30.GL_FLOAT,
                false,
                8 * Float.SIZE_BYTES,
                0
            )

        GLES30.glVertexAttribPointer(
            vNormaleHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            8 * Float.SIZE_BYTES,
            3 * Float.SIZE_BYTES
        )

        GLES30.glVertexAttribPointer(
            vTextureHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            8 * Float.SIZE_BYTES,
            6 * Float.SIZE_BYTES
        )

        GLES30.glBindVertexArray(0)
        GLES30.glDisableVertexAttribArray(vPositionHandle)
        GLES30.glDisableVertexAttribArray(vNormaleHandle)
        GLES30.glDisableVertexAttribArray(vTextureHandle)
        GLRenderer.checkGLError("init buffers")
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(glProgramId)

        uMVPMatrixHandle = GLES30.glGetUniformLocation(glProgramId, "uMVPMatrix")
        GLES30.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0)

        attachAdditionalHandles()

        GLES30.glBindVertexArray(VAO[0])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertices.size)
        GLES30.glBindVertexArray(0)

        GLES30.glUseProgram(0)
        GLRenderer.checkGLError("draw")
    }

    protected open fun attachAdditionalHandles(){
        vColorHandle = GLES30.glGetUniformLocation(glProgramId, "vertexColor")
        GLES30.glUniform4fv(vColorHandle, 1, color, 0)
    }
}