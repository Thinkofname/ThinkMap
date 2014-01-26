library mapViewer;

import "dart:html";
import "dart:web_gl";
import "dart:async";
import 'dart:typed_data';
import "dart:math";
import "package:vector_math/vector_math.dart";

part "chunk.dart";
part "world.dart";
part "shaders.dart";
part "blocks.dart";

RenderingContext gl;
CanvasElement canvas;

World world = new World();

Program mainProgram;
UniformLocation pMatrixLocation;
UniformLocation uMatrixLocation;
int positionLocation;
int colourLocation;

Chunk tempChunk = new Chunk(0, 0);

main() {
  canvas = document.getElementById("main");
  gl = canvas.getContext3d();
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
  window.onResize.listen((e) {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    pMatrix = makePerspectiveMatrix(75, canvas.width / canvas.height, 0.1, 100000);
    pMatrix.copyIntoArray(pMatrixList);
  });
  pMatrix = makePerspectiveMatrix(75, canvas.width / canvas.height, 0.1, 100000);
  pMatrix.copyIntoArray(pMatrixList);

  var chunkVertexShader = createShader(gl, chunkVertexShaderSource, VERTEX_SHADER);
  var chunkFragmentShader = createShader(gl, chunkFragmentShaderSource, FRAGMENT_SHADER);
  mainProgram = createProgram(gl, chunkVertexShader, chunkFragmentShader);
  pMatrixLocation = gl.getUniformLocation(mainProgram, "pMatrix");
  uMatrixLocation = gl.getUniformLocation(mainProgram, "uMatrix");
  positionLocation = gl.getAttribLocation(mainProgram, "position");
  colourLocation = gl.getAttribLocation(mainProgram, "colour");
  gl.enableVertexAttribArray(positionLocation);
  gl.enableVertexAttribArray(colourLocation);

  gl.enable(DEPTH_TEST);
  gl.enable(CULL_FACE);
  gl.cullFace(BACK);
  gl.frontFace(CCW);

  draw(0);

  window.onMouseMove.listen((e){
    pos = e.client.x - (window.innerWidth~/2);
    posY = e.client.y - (window.innerHeight~/2);
  });
}

Matrix4 pMatrix;
Float32List pMatrixList = new Float32List(4 * 4);

Matrix4 uMatrix = new Matrix4.identity();
Float32List uMatrixList = new Float32List(4 * 4);

int pos = 0;
int posY = 0;

draw(num highResTime) {
  gl.viewport(0, 0, canvas.width, canvas.height);
  double skyPosition = getScale();
  gl.clearColor(
      getScaledNumber(122.0/255.0, 0.0, skyPosition),
      getScaledNumber(165.0/255.0, 0.0, skyPosition),
      getScaledNumber(247.0/255.0, 0.0, skyPosition), 1);
  gl.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

  gl.useProgram(mainProgram);
  gl.uniformMatrix4fv(pMatrixLocation, false, pMatrixList);

  uMatrix.setIdentity();

  uMatrix.scale(1.0, -1.0, 1.0);
  uMatrix.rotateX(0.6);
  uMatrix.translate(0.0, -85.0 - (posY/30), -50.0);
  uMatrix.rotateY(pos/200);
  uMatrix.translate(-8.0, 0.0, -8.0);
//  uMatrix.translate(-8.0, -70.0, -8.0);
  uMatrix.copyIntoArray(uMatrixList);
  gl.uniformMatrix4fv(uMatrixLocation, false, uMatrixList);

  tempChunk.render(gl);

  window.requestAnimationFrame(draw);
}

double getScale() {
  double scale = (world.currentTime - 6000) / 12000;
  if (scale > 1.0) {
    scale = 2.0 - scale;
  } else if (scale < 0) {
    scale = -scale;
  }
  return scale;
}

double getScaledNumber(double x, double y, double scale) {
  return x + (y - x) * scale;
}