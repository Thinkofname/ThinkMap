precision mediump float;

attribute vec3 position;
attribute vec4 colour;

uniform mat4 pMatrix;
uniform mat4 uMatrix;

varying vec4 vColour;

void main(void) {
    gl_Position = pMatrix * uMatrix * vec4(position, 1.0);
    vColour = colour;
}