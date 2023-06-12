#version 300 es
in vec3 vertexPosition;
in vec3 vertexNormale;
in vec2 vertexTextureCoords;

out vec2 vTextureCoordinate;
out vec4 vColor;

uniform mat4 uMVPMatrix;

uniform vec3 object_center;
uniform vec3 this_center;
uniform float world_angle;
uniform float object_angle;
uniform float this_angle;

uniform vec3 eyePosition;
uniform vec3 lightPosition;

uniform vec4 materialAmbient;
uniform vec4 materialDiffuse;
uniform vec4 materialSpecular;

uniform vec4 lightAmbient;
uniform vec4 lightDiffuse;
uniform vec4 lightSpecular;

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

    gl_Position = uMVPMatrix * vec4(position, 1.0);
    vTextureCoordinate = vertexTextureCoords;

    vec4 dump = materialAmbient * materialSpecular * lightAmbient * lightSpecular * 0.00000001;
    vec3 lightNormale = normalize(lightPosition - vertexPosition);
    float lightAngle = max(dot(normalize(vertexNormale), lightNormale), 0.0);
    vec4 diffuse = materialDiffuse * lightDiffuse * lightAngle;
    vColor = dump + diffuse;
}