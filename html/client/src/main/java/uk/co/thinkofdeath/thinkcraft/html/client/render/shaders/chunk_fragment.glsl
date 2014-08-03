precision mediump float;

const int MAX_TEXTURES = 5;

uniform sampler2D textures[MAX_TEXTURES];
uniform sampler2D textureDetails;

varying vec4 vColour;
varying vec2 vTextureOffset;
varying float vLighting;
varying float texture;
varying float vTextureID;

const float invTextureSize = 1.0 / 512.0;

void main(void) {
    vec4 details = texture2D(textureDetails, vec2(
        mod(vTextureID, 128.0) / 128.0,
        floor(vTextureID / 128.0) / 128.0
    ));

    vec2 texturePos = vec2(
        (details.r * 255.0) * invTextureSize * 2.0,
        (details.g * 255.0) * invTextureSize * 2.0
    );
    float textureSize = (details.b * 255.0) * invTextureSize;

    vec2 pos = texturePos + fract(vTextureOffset) * textureSize;
    vec4 colour = vec4(0.0, 0.0, 0.0, 1.0);
    // I hate GLSL
    int tid = int(details.a * 255.0);
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