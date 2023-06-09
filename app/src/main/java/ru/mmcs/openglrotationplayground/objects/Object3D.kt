package ru.mmcs.openglrotationplayground.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import ru.mmcs.openglnextplayground.GLRenderer
import ru.mmcs.openglrotationplayground.App
import ru.mmcs.openglrotationplayground.utils.Material
import ru.mmcs.openglrotationplayground.utils.Point
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

open class Object3D(
    objFile: InputStream,
    vertexShaderFile: InputStream,
    fragmentShaderFile: InputStream,
    var center: Point,
    val material: Material = Material()
) {
    protected lateinit var vertices: FloatArray  // vert + norm + tex
    protected lateinit var vertexShader: String
    protected lateinit var fragmentShader: String

    protected var glProgramId: Int = -1
    private var VAO: IntArray = intArrayOf(0)
    private var VBO: IntArray = intArrayOf(0)
    private var textures = intArrayOf(0)

    private lateinit var textureBitmap: Bitmap
    private lateinit var materialBitmap: Bitmap

    init {
        loadBitmaps(App.appContext)
        parseObjFile(objFile)
        readShaders(vertexShaderFile, fragmentShaderFile)
        compileShaders()
        initBuffers()
        initTextures()
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }

    private fun loadBitmaps(context: Context?){
        context!!.assets.open(material.materialPath).use{
            materialBitmap = BitmapFactory.decodeStream(it)
        }
        context.assets.open(material.texturePath).use{
            textureBitmap = BitmapFactory.decodeStream(it)
        }
    }

    fun reloadShaders(vertexShaderFile: InputStream, fragmentShaderFile: InputStream){
        readShaders(vertexShaderFile, fragmentShaderFile)
        compileShaders()
        initBuffers()
        initTextures()
    }

    private fun readShaders(vertexShaderFile: InputStream, fragmentShaderFile: InputStream){
        fragmentShader = fragmentShaderFile
            .bufferedReader().use {
                it.readText()
            }
        vertexShader = vertexShaderFile
            .bufferedReader().use {
                it.readText()
            }
    }

    private fun parseObjFile(objFile: InputStream){
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
                    "" -> {}
                    else -> throw IOException(".obj file is badly formatted. Unknown tag [${details.first()}]")
                }
            }
//            Log.d("GL_DEBUG", "v: " + packedVertices.joinToString(" "))
        }
        this.vertices = packedVertices.toFloatArray()
    }

    private fun compileShaders() {
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

    private var uEyePositionHandle : Int = 0
    private var uLightPositionHandle : Int = 0


    private var uMaterialAmbientHandle : Int = 0
    private var uMaterialColorHandle : Int = 0
    private var uMaterialDiffuseHandle : Int = 0
    private var uMaterialSpecularHandle : Int = 0
    private var uLightAmbientHandle : Int = 0
    private var uLightDiffuseHandle : Int = 0
    private var uLightSpecularHandle : Int = 0
    private var uLightAttenuationHandle : Int = 0
    private var uMaterialShininessHandle : Int = 0
    private var uTextureContributionHandle : Int = 0
    private var uMaterialContributionHandle : Int = 0
    private var uMaterialHandle : Int = 0
    private var uTextureHandle : Int = 0

    private fun initBuffers(){
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
            2,
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

    private fun initTextures(){
        textures = intArrayOf(0, 0)
        GLES30.glGenTextures(2, textures, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0])
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, materialBitmap, 0)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST)
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[1])
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, textureBitmap, 0)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST)
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)

//        val buffer = ByteBuffer.allocate(materialBitmap.rowBytes * materialBitmap.height)
//        materialBitmap.copyPixelsToBuffer(buffer)
//        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 128, 128, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer)


        GLRenderer.checkGLError("Textures")
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(glProgramId)

        uMVPMatrixHandle = GLES30.glGetUniformLocation(glProgramId, "uMVPMatrix")
        GLES30.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[1])

        attachAdditionalHandles()

        GLES30.glBindVertexArray(VAO[0])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertices.size)
        GLES30.glBindVertexArray(0)

        GLES30.glUseProgram(0)
        GLRenderer.checkGLError("draw")
    }

    protected open fun attachAdditionalHandles(){
//        vColorHandle = GLES30.glGetUniformLocation(glProgramId, "vertexColor")
//        GLES30.glUniform4fv(vColorHandle, 1, color, 0)
        uEyePositionHandle = GLES30.glGetUniformLocation(glProgramId, "eyePosition")
        GLES30.glUniform3fv(uEyePositionHandle, 1, GLRenderer.eyePosition, 0)
        uLightPositionHandle = GLES30.glGetUniformLocation(glProgramId, "lightPosition")
        GLES30.glUniform3fv(uLightPositionHandle, 1, GLRenderer.lightPosition, 0)

        uLightSpecularHandle = GLES30.glGetUniformLocation(glProgramId, "lightSpecular")
        GLES30.glUniform4fv(uLightSpecularHandle, 1, GLRenderer.lightSpecular, 0)
        uLightDiffuseHandle = GLES30.glGetUniformLocation(glProgramId, "lightDiffuse")
        GLES30.glUniform4fv(uLightDiffuseHandle, 1, GLRenderer.lightDiffuse, 0)
        uLightAmbientHandle = GLES30.glGetUniformLocation(glProgramId, "lightAmbient")
        GLES30.glUniform4fv(uLightAmbientHandle, 1, GLRenderer.lightAmbient, 0)

        uMaterialColorHandle = GLES30.glGetUniformLocation(glProgramId, "materialColor")
        GLES30.glUniform4fv(uMaterialColorHandle, 1, material.color, 0)
        uMaterialSpecularHandle = GLES30.glGetUniformLocation(glProgramId, "materialSpecular")
        GLES30.glUniform4fv(uMaterialSpecularHandle, 1, material.specular, 0)
        uMaterialDiffuseHandle = GLES30.glGetUniformLocation(glProgramId, "materialDiffuse")
        GLES30.glUniform4fv(uMaterialDiffuseHandle, 1, material.diffuse, 0)
        uMaterialAmbientHandle = GLES30.glGetUniformLocation(glProgramId, "materialAmbient")
        GLES30.glUniform4fv(uMaterialAmbientHandle, 1, material.ambient, 0)
        uMaterialShininessHandle = GLES30.glGetUniformLocation(glProgramId, "materialShininess")
        GLES30.glUniform1f(uMaterialShininessHandle, material.shininess)

        uLightAttenuationHandle = GLES30.glGetUniformLocation(glProgramId, "lightAttenuation")
        GLES30.glUniform3fv(uLightAttenuationHandle, 1, GLRenderer.lightAttenuation, 0)

        uMaterialContributionHandle = GLES30.glGetUniformLocation(glProgramId, "materialContribution")
        GLES30.glUniform1f(uMaterialContributionHandle, GLRenderer.materialContribution)
        uTextureContributionHandle = GLES30.glGetUniformLocation(glProgramId, "textureContribution")
        GLES30.glUniform1f(uTextureContributionHandle, GLRenderer.textureContribution)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[1]);

        uMaterialHandle = GLES30.glGetUniformLocation(glProgramId, "materialTexture")
        GLES30.glUniform1i(uMaterialHandle, 0)
        uTextureHandle = GLES30.glGetUniformLocation(glProgramId, "textureTexture")
        GLES30.glUniform1i(uTextureHandle, 1)

//        Log.d("GL_DEBUG","Uniforms: $uEyePositionHandle $uLightPositionHandle $uLightSpecularHandle " +
//                "$uLightDiffuseHandle $uLightAmbientHandle $uMaterialSpecularHandle $uMaterialDiffuseHandle " +
//                "$uMaterialAmbientHandle $uMaterialShininessHandle $uLightAmbientHandle $uTextureContributionHandle" +
//                "$uMaterialContributionHandle $uMaterialHandle")
    }
}