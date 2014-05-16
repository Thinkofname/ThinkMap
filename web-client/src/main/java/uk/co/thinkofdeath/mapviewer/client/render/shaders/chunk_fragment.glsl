precision mediump float;

uniform sampler2D textures[5];

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
    if (tid == 0) {
        colour = texture2D(textures[0], pos);
    } else if (tid == 1) {
        colour = texture2D(textures[1], pos);
    } else if (tid == 2) {
        colour = texture2D(textures[2], pos);
    } else if (tid == 3) {
        colour = texture2D(textures[3], pos);
    } else if (tid == 1) {
        colour = texture2D(textures[4], pos);
    }
    colour *= vColour;
    colour.rgb *= vLighting;
    #ifndef alpha
        if (colour.a < 0.5) discard;
    #endif
    gl_FragColor = colour;
}