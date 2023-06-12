#version 300 es
precision mediump float;
in vec2 vTextureCoordinate;
in vec4 vColor;

out vec4 fragColor;
void main() {
    fragColor = vColor + vec4(vTextureCoordinate * 0.1, 0.0, 0.0);
}