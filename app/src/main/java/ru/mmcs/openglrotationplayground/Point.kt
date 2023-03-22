package ru.mmcs.openglrotationplayground

import Matrix
import kotlin.math.cos
import kotlin.math.sin

data class Point (val x: Float, val y: Float, val z: Float){
//    fun rotateAroundYAxis(angle: Float) : Point{
//        val affineMatrix = Matrix(3,3, listOf(
//            cos(angle), 0f, sin(angle),
//            0f, 1f, 0f,
//            -sin(angle), 0f, cos(angle)))
//        val positionVector = Matrix(1, 3, listOf(x, y, z)) * affineMatrix
//        return Point(positionVector[0,0], positionVector[0, 1], positionVector[0,2])
//    }
//
//    fun rotateAroundYLine(angle: Float, lineCenter: Point) : Point{
//        val affineMatrix = Matrix(3,3, listOf(
//            cos(angle), 0f, sin(angle),
//            0f, 1f, 0f,
//            -sin(angle), 0f, cos(angle)))
//        val positionVector = Matrix(1, 3, listOf(x - lineCenter.x,  - lineCenter.y, z - lineCenter.z)) * affineMatrix
//        return Point(positionVector[0,0] + lineCenter.x, positionVector[0, 1] + lineCenter.y, positionVector[0,2] + lineCenter.z)
//    }

    fun toFloatArray() : FloatArray{
        return floatArrayOf(x, y, z)
    }

    override fun toString(): String {
        return "(x=$x, y=$y, z=$z)"
    }


}