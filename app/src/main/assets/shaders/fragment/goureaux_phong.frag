#version 300 es
precision mediump float;
in vec2 vTextureCoordinate;
in vec4 vColor;

uniform vec4 materialColor;

out vec4 fragColor;
void main() {
    fragColor = vColor * materialColor + vec4(vTextureCoordinate * 0.0000001, 0.0, 0.0);
}