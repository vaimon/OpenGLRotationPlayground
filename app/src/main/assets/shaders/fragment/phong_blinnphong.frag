#version 300 es
precision mediump float;
in vec2 vTextureCoordinate;
in vec3 vNormale;
in vec3 vPosition;

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

out vec4 fragColor;
void main() {
    vec4 ambient = materialAmbient * lightAmbient;

    vec3 lightNormale = normalize(lightPosition - vPosition);
    float lightAngle = max(dot(normalize(vNormale), lightNormale), 0.0);
    vec4 diffuse = materialDiffuse * lightDiffuse * lightAngle;

    vec3 viewDirection = normalize(eyePosition - vPosition);
    vec3 h = normalize(lightNormale + viewDirection);
    float specularAngle = pow(max(dot(vNormale, h), 0.0), materialShininess);
    vec4 specular = materialSpecular * lightSpecular * specularAngle;

    float distance = length(lightPosition - vPosition);
    float attenuation = 1.0 / (lightAttenuation.x + lightAttenuation.y * distance + lightAttenuation.z * pow(distance, 2.0));

    vec4 vColor = ambient + attenuation * (diffuse + specular);
    fragColor = vColor + vec4(vTextureCoordinate * 0.000001, 0.0, 0.0);
}