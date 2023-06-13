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
    vec3 lightNormale = normalize(lightPosition - vPosition);
    float lightAngle = max(dot(normalize(vNormale), lightNormale), 0.0);
    vec4 diffuse = materialDiffuse * lightDiffuse * lightAngle;
    fragColor = diffuse + vec4(vTextureCoordinate * 0.00000001, 0.0, 0.0);
}