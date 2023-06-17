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
uniform vec4 materialColor;

uniform vec4 lightAmbient;
uniform vec4 lightDiffuse;
uniform vec4 lightSpecular;
uniform vec3 lightAttenuation;

out vec4 fragColor;
void main() {
    vec3 lightNormale = normalize(lightPosition - vPosition);
    float diff = 0.2 + max(dot(vNormale, lightNormale), 0.0);
    vec4 diffuse = materialDiffuse * lightDiffuse;
    vec4 clr;
    if (diff < 0.4)
        clr = diffuse * diff;
    else if (diff < 0.6)
        clr = diffuse * 0.6;
    else if (diff < 0.8)
        clr = diffuse;
    else
        clr = diffuse * 1.3;
    fragColor = clr * materialColor + vec4(vTextureCoordinate * 0.00000001, 0.0, 0.0);
}