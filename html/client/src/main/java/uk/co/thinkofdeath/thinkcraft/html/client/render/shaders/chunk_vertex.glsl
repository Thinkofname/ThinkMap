precision mediump float;

attribute vec3 position;
attribute vec4 colour;
attribute vec2 texturePos;
attribute vec3 textureDetails;
attribute vec2 lighting;

uniform mat4 pMatrix;
uniform mat4 uMatrix;
uniform vec2 offset;
uniform float scale;

varying vec4 vColour;
varying vec2 vTexturePos;
varying vec2 vTextureOffset;
varying float vLighting;
varying float vTextureSize;
varying float texture;

const float invPosScale = 1.0 / 256.0;
const float invIdScale = 1.0 / 32.0;
const float invTextureSize = 1.0 / 512.0;

void main(void) {
    vec3 pos = position;
    gl_Position = pMatrix * uMatrix * vec4((pos * invPosScale) - 0.5 + vec3(offset.x * 16.0, 0.0,
     offset.y * 16.0), 1.0);
    vColour = colour;

    vTextureSize = textureDetails[2] * invTextureSize;
    float posX = textureDetails[0];
    float posY = textureDetails[1];
    texture = floor(posY * invTextureSize);
    posY = posY - texture * 1024.0;
    vTexturePos = vec2(posX, posY) * invTextureSize;
    vTextureOffset = texturePos * invPosScale;

    float light = max(lighting.x, lighting.y * scale);
    float val = pow(0.9, 16.0 - light) * 2.0;
    vLighting = clamp(pow(val, 1.5) * 0.5, 0.0, 1.0);
}