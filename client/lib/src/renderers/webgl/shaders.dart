part of map_viewer;

const chunkVertexShaderSource =
    """
attribute vec3 position;
attribute vec3 colour;
attribute vec2 texturePos;
attribute vec2 textureId;
attribute vec2 lighting;

uniform mat4 pMatrix;
uniform mat4 uMatrix;
uniform vec2 offset;

varying vec3 vColour;
varying vec2 vTextureId;
varying vec2 vTexturePos;
varying vec2 vLighting;

void main(void) {
    vec3 pos = position;
    gl_Position = pMatrix * uMatrix * vec4((pos / 16.0) + vec3(offset.x * 16.0, 0.0, offset.y * 16.0), 1.0);
    vColour = colour;
    vTextureId = textureId;
    vTexturePos = texturePos / 16.0;
    if (vTexturePos.x == 0.0) {
        vTexturePos.x = 0.0001;
    } else if (vTexturePos.x == 1.0) {
        vTexturePos.x = 0.9999;
    }
    if (vTexturePos.y == 0.0) {
        vTexturePos.y = 0.0001;
    } else if (vTexturePos.y == 1.0) {
        vTexturePos.y = 0.9999;
    }
    vLighting = lighting;
}
""";

const chunkFragmentShaderSource =
    """
precision mediump float;

uniform sampler2D texture;
uniform float frame;
uniform int disAlpha;

varying vec3 vColour;
varying vec2 vTextureId;
varying vec2 vTexturePos;
varying vec2 vLighting;

void main(void) {
    float id = floor(vTextureId.x + 0.5);
    if (abs(floor((vTextureId.y - vTextureId.x) + 0.5) - 1.0) < 0.99) {
        id = id + floor(mod(frame, vTextureId.y - vTextureId.x) + 0.5);
    }
    vec2 pos = fract(vTexturePos) * 0.03125;
    pos.x += floor(mod(id, 32.0)) * 0.03125;
    pos.y += floor(id / 32.0) * 0.03125;
    gl_FragColor = texture2D(texture, pos) * vec4(vColour, 1.0);

    float scale = (frame - 6000.0) / 12000.0;
    if (scale > 1.0) {
        scale = 2.0 - scale;
    } else if (scale < 0.0) {
        scale = -scale;
    }
    scale = 1.0 - scale;

    float light = max(vLighting.x, vLighting.y * scale);
    float val = pow(0.9, 16.0 - light) * 2.0;
    gl_FragColor.rgb *= clamp(pow(val, 1.5) / 2.0, 0.0, 1.0);
    if (disAlpha == 1 && gl_FragColor.a < 0.5) discard;
}
""";

Shader createShader(RenderingContext gl, String source, int type) {
  var shader = gl.createShader(type);
  gl.shaderSource(shader, source);
  gl.compileShader(shader);

  if (!gl.getShaderParameter(shader, COMPILE_STATUS)) {
    throw gl.getShaderInfoLog(shader);
  }
  return shader;
}

Program createProgram(RenderingContext gl, Shader vertexShader, Shader
    fragmentShader) {
  var program = gl.createProgram();
  gl.attachShader(program, vertexShader);
  gl.attachShader(program, fragmentShader);
  gl.linkProgram(program);
  gl.useProgram(program);
  return program;
}
