precision mediump float;

const int MAX_TEXTURES = 10;

uniform sampler2D textures[MAX_TEXTURES];

varying vec4 vColour;
varying vec2 vTexturePos;
varying vec2 vTextureOffset;
varying float vLighting;
varying float vTextureSize;
varying float texture;

void main(void) {
    vec4 colour = vec4(0.0);
    vec2 pos = vTexturePos + fract(vTextureOffset) * vTextureSize;
    // I hate GLSL
    int tid = int(texture);
    for (int i = 0; i < MAX_TEXTURES; i++) {
        if (tid == i) {
            colour = texture2D(textures[i], pos);
        }
    }
    colour *= vColour;
    colour.rgb *= vLighting;
    #ifndef alpha
        if (colour.a < 0.5) discard;
    #endif
    gl_FragColor = colour;
}