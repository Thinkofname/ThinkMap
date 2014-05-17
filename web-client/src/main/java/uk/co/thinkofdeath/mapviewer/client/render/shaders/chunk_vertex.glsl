precision mediump float;

attribute vec3 position;
attribute vec4 colour;
attribute vec2 texturePos;
attribute vec4 textureDetails;
attribute float textureFrames;
attribute vec2 lighting;

uniform mat4 pMatrix;
uniform mat4 uMatrix;
uniform vec2 offset;
uniform float scale;
uniform float frame;

varying vec4 vColour;
varying vec2 vTexturePos;
varying vec2 vTextureOffset;
varying float vLighting;
varying float vTextureSize;
varying float texture;

const float invPosScale = 1.0 / 256.0;
const float invIdScale = 1.0 / 32.0;
const float invTextureSize = 1.0 / 1024.0;

void main(void) {
    vec3 pos = position;
    gl_Position = pMatrix * uMatrix * vec4((pos * invPosScale) - 0.5 + vec3(offset.x * 16.0, 0.0,
     offset.y * 16.0), 1.0);
    vColour = colour;

    vTextureSize = textureDetails[2] * invTextureSize;
    float currentFrame = mod(frame, textureFrames);
    float totalPosition = currentFrame * textureDetails.z;
    float posX = textureDetails[0] + mod(totalPosition, textureDetails[3]);
    float posY = textureDetails[1] + floor(totalPosition / textureDetails[3]) * textureDetails[2];
    texture = floor(posY / 1024.0);
    posY = posY - texture * 1024.0;
    vTexturePos = vec2(posX, posY) * invTextureSize;
    vTextureOffset = texturePos * invPosScale;

    float light = max(lighting.x, lighting.y * scale);
    float val = pow(0.9, 16.0 - light) * 2.0;
    vLighting = clamp(pow(val, 1.5) * 0.5, 0.0, 1.0);
}