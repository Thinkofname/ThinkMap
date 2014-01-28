part of mapViewer;

var chunkVertexShaderSource = """
attribute vec3 position;
attribute vec3 colour;

uniform mat4 pMatrix;
uniform mat4 uMatrix;
uniform vec2 offset;

varying vec3 vColour;

void main(void) {
  gl_Position = pMatrix * uMatrix * vec4(position + vec3(offset.x * 16.0, 0.0, offset.y * 16.0), 1.0);
  vColour = colour;
}
""";

var chunkFragmentShaderSource = """
precision mediump float;

varying vec3 vColour;

void main(void) {
  gl_FragColor = vec4(vColour, 1.0);
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

Program createProgram(RenderingContext gl, Shader vertexShader, Shader fragmentShader) {
    var program = gl.createProgram();
    gl.attachShader(program, vertexShader);
    gl.attachShader(program, fragmentShader);
    gl.linkProgram(program);
    gl.useProgram(program);
    return program;
}