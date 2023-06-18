#version 300 es
in vec3 vertexPosition;
in vec3 vertexNormale;
in vec2 vertexTextureCoords;

out vec2 vTextureCoordinate;
out vec3 vNormale;
out vec3 vPosition;

uniform mat4 uMVPMatrix;

uniform vec3 object_center;
uniform vec3 this_center;
uniform float world_angle;
uniform float object_angle;
uniform float this_angle;

void main() {
    vec3 position = (vertexPosition - this_center) * mat3(
        cos(this_angle), 0, sin(this_angle),
        0, 1, 0,
        -sin(this_angle), 0, cos(this_angle)
    ) + this_center;

    position = vec3((position - object_center) * mat3(
        cos(object_angle), 0, sin(object_angle),
        0, 1, 0,
        -sin(object_angle), 0, cos(object_angle)
    )) + object_center;

    position = vec3(position * mat3(
        cos(world_angle), 0, sin(world_angle),
        0, 1, 0,
        -sin(world_angle), 0, cos(world_angle)
    ));

    mat3 normaleRotation = mat3(
        cos(this_angle), 0, sin(this_angle),
        0, 1, 0,
        -sin(this_angle), 0, cos(this_angle)
    ) * mat3(
        cos(world_angle), 0, sin(world_angle),
        0, 1, 0,
        -sin(world_angle), 0, cos(world_angle)
    ) * mat3(
        cos(object_angle), 0, sin(object_angle),
        0, 1, 0,
        -sin(object_angle), 0, cos(object_angle)
    );

    vec3 rotatedNormale = vec3(mat3(transpose((normaleRotation))) * vertexNormale);
    //    vec3 rotatedNormale = mat3(transpose(inverse(normaleRotation))) * vertexNormale;

    gl_Position = uMVPMatrix * vec4(position, 1.0);
    vTextureCoordinate = vec2(vertexTextureCoords.x, 1.0 - vertexTextureCoords.y);

    vNormale = rotatedNormale;
    vPosition = position;
}