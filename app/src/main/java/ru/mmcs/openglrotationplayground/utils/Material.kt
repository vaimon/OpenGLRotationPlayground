package ru.mmcs.openglrotationplayground.utils

data class Material(
    val color: FloatArray = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f),
    val diffuse: FloatArray = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f),
    val specular: FloatArray = floatArrayOf(0.6f, 0.6f, 0.6f, 1.0f),
    val ambient: FloatArray = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f),
    val shininess: Float = 10f,
    val texturePath: String = "textures/fruitTexture.png",
    val materialPath: String = "textures/wood.jpg"
) {
}