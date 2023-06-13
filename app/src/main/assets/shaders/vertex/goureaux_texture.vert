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
uniform float materialShininess;

uniform vec4 lightAmbient;
uniform vec4 lightDiffuse;
uniform vec4 lightSpecular;
uniform vec3 lightAttenuation;

vec3 rotateVector(vec3 vector, vec3 angles) {
    float cosThetaX = cos(angles.x);
    float sinThetaX = sin(angles.x);
    float cosThetaY = cos(angles.y);
    float sinThetaY = sin(angles.y);
    float cosThetaZ = cos(angles.z);
    float sinThetaZ = sin(angles.z);

    // Apply rotation matrices in the correct order
    vec3 rotatedVector;
    rotatedVector.x = vector.x * (cosThetaZ * cosThetaY) + vector.y * (cosThetaZ * sinThetaY * sinThetaX - sinThetaZ * cosThetaX) + vector.z * (cosThetaZ * sinThetaY * cosThetaX + sinThetaZ * sinThetaX);
    rotatedVector.y = vector.x * (sinThetaZ * cosThetaY) + vector.y * (sinThetaZ * sinThetaY * sinThetaX + cosThetaZ * cosThetaX) + vector.z * (sinThetaZ * sinThetaY * cosThetaX - cosThetaZ * sinThetaX);
    rotatedVector.z = -vector.x * sinThetaY + vector.y * cosThetaY * sinThetaX + vector.z * cosThetaY * cosThetaX;

    return rotatedVector;
}

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
    vTextureCoordinate = vertexTextureCoords;

    vec4 ambient = materialAmbient * lightAmbient;

    vec3 lightNormale = normalize(lightPosition - vertexPosition);
    float lightAngle = max(dot(normalize(rotatedNormale), lightNormale), 0.0);
    vec4 diffuse = materialDiffuse * lightDiffuse * lightAngle;

    vec3 viewDirection = normalize(eyePosition - vertexPosition);
    float specularAngle = pow(max(dot(reflect(-lightNormale, rotatedNormale), viewDirection), 0.0), 2.0);
    vec4 specular = materialSpecular * lightSpecular * specularAngle;

    float distance = distance(lightPosition, vertexPosition);
    float attenuation = 1.0 / (lightAttenuation.x + lightAttenuation.y * distance + lightAttenuation.z * pow(distance, 2.0));

    vColor = ambient + attenuation * (diffuse + specular);
}