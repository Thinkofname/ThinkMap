precision mediump float;

uniform sampler2D texture;

varying vec4 vColour;
varying vec2 vTexturePos;
varying vec2 vTextureOffset;
varying float vLighting;

void main(void) {
    vec4 colour = texture2D(texture, vTexturePos + fract(vTextureOffset) * 0.03125) * vColour;
    colour.rgb *= vLighting;
    #ifndef alpha
        if (colour.a < 0.5) discard;
    #endif
    gl_FragColor = colour;
}