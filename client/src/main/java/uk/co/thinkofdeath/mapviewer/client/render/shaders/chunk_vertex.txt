precision mediump float;

attribute vec3 position;
attribute vec4 colour;
attribute vec2 texturePos;
attribute vec2 textureId;
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

const float invPosScale = 1.0 / 256.0;
const float invIdScale = 1.0 / 32.0;

void main(void) {
    vec3 pos = position;
    gl_Position = pMatrix * uMatrix * vec4((pos * invPosScale) - 0.5 + vec3(offset.x * 16.0, 0.0,
     offset.y * 16.0), 1.0);
    vColour = colour;

    vec2 tPos = texturePos * invPosScale;
    float id = floor(textureId.x + 0.5);
    id = id + floor(mod(frame, floor((textureId.y - textureId.x) + 0.5) + 1.0));
	vTexturePos = vec2(floor(mod(id, 32.0)) * 0.03125,
		floor(id * invIdScale) * 0.03125);
	vTextureOffset = tPos;

    float light = max(lighting.x, lighting.y * scale);
    float val = pow(0.9, 16.0 - light) * 2.0;
    vLighting = clamp(pow(val, 1.5) * 0.5, 0.0, 1.0);
}