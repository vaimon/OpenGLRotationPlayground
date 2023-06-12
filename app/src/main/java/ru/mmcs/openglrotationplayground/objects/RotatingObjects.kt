package ru.mmcs.openglrotationplayground.objects

import android.content.Context
import android.opengl.GLES30
import ru.mmcs.openglrotationplayground.utils.Material
import ru.mmcs.openglrotationplayground.utils.Point

open class RotatingObject3D(
    context: Context,
    center: Point,
    color: Material = Material(),
    objFilePath: String
) : Object3D(
    objFile = context.assets.open(objFilePath),
    vertexShaderFile = context.assets.open("shaders/vertex/phong_phong.vert"),
    fragmentShaderFile = context.assets.open("shaders/fragment/phong_phong.frag"),
    center,
    color
) {

    var worldAngle = 0.0f
    var objectAngle = 0.0f
    var thisAngle = 0.0f

    var worldCenter = Point(0.0f, 0.0f, 0.0f)
    var complexObjectCenter = Point(1f, 0f, 0f)

    private var worldAngleHandle: Int = 0
    private var thisAngleHandle: Int = 0
    private var objectAngleHandle: Int = 0
    private var objectCenterHandle: Int = 0
    private var thisCenterHandle: Int = 0

    override fun attachAdditionalHandles() {
        super.attachAdditionalHandles()

        worldAngleHandle = GLES30.glGetUniformLocation(glProgramId, "world_angle")
        GLES30.glUniform1f(worldAngleHandle, worldAngle)

        thisAngleHandle = GLES30.glGetUniformLocation(glProgramId, "this_angle")
        GLES30.glUniform1f(thisAngleHandle, thisAngle)

        objectAngleHandle = GLES30.glGetUniformLocation(glProgramId, "object_angle")
        GLES30.glUniform1f(objectAngleHandle, objectAngle)

        objectCenterHandle = GLES30.glGetUniformLocation(glProgramId, "object_center")
        GLES30.glUniform3fv(objectCenterHandle, 1, complexObjectCenter.toFloatArray(), 0)

        thisCenterHandle = GLES30.glGetUniformLocation(glProgramId, "this_center")
        GLES30.glUniform3fv(thisCenterHandle, 1, center.toFloatArray(), 0)
    }
}

class Cube3D(
    context: Context,
    center: Point,
    color: Material = Material(),
): RotatingObject3D(
    context,
    center,
    color,
    "objects/cube.obj"
)

class Sphere3D(
    context: Context,
    center: Point,
    color: Material = Material(),
): RotatingObject3D(
    context,
    center,
    color,
    "objects/sphere.obj"
)

class Teapot3D(
    context: Context,
    center: Point,
    color: Material = Material(),
): RotatingObject3D(
    context,
    center,
    color,
    "objects/teapot.obj"
)

class Torus3D(
    context: Context,
    center: Point,
    color: Material = Material(),
): RotatingObject3D(
    context,
    center,
    color,
    "objects/torus.obj"
)