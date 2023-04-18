#version 300 es
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

    gl_Position = uMVPMatrix * vec4(position, 1.0);
}