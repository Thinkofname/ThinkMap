library mapViewer;

import "dart:html";
import "dart:web_gl";
import "dart:async";
import 'dart:typed_data';
import "dart:math";
//import "dart:collection";
import "dart:convert";
//import "package:archive/archive.dart";
import "package:vector_math/vector_math.dart";

part "chunk.dart";
part "world.dart";
part "shaders.dart";
part "blocks.dart";
part "otherblocks.dart";

RenderingContext gl;
CanvasElement canvas;

World world = new World();

Program mainProgram;
UniformLocation pMatrixLocation;
UniformLocation uMatrixLocation;
UniformLocation offsetLocation;
UniformLocation blockTextureLocation;
UniformLocation frameLocation;
UniformLocation disAlphaLocation;
int positionLocation;
int colourLocation;
int textureIdLocation;
int textirePosLocation;

List<ImageElement> blockTexturesRaw = new List();
List<Texture> blockTextures = new List();
Map<String, TextureInfo> blockTextureInfo = new Map();

main() {
    var img = new ImageElement();
    blockTexturesRaw.add(img);
    HttpRequest req = new HttpRequest();

    Future.wait([req.onLoad.first, img.onLoad.first]).then((e){
       Map<String, Map<String, int>> js = new JsonDecoder(null).convert(req.responseText);
       js.forEach((k, v) {
           blockTextureInfo[k] = new TextureInfo(v["start"], v["end"]);
       });
        start();
    });

    req.open("GET", "imgs/blocks.json", async: true);
    req.send();
    img.src = "imgs/blocks_0.png";
}

Texture loadTexture(RenderingContext gl, ImageElement imageElement) {
    Texture tex = gl.createTexture();
    gl.bindTexture(TEXTURE_2D, tex);
    gl.pixelStorei(UNPACK_FLIP_Y_WEBGL, 0);
    gl.pixelStorei(UNPACK_PREMULTIPLY_ALPHA_WEBGL, 0);
    gl.texImage2DImage(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, imageElement);
    gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST);
    gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST);
    gl.bindTexture(TEXTURE_2D, null);
    return tex;
}

start() {
    Block._allBlocks; // Get around a dart issue

    canvas = document.getElementById("main");
    gl = canvas.getContext3d(alpha: false, premultipliedAlpha: false, antialias: false);
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    window.onResize.listen((e) {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        pMatrix = makePerspectiveMatrix(75, canvas.width / canvas.height, 0.1, 500);
        pMatrix.copyIntoArray(pMatrixList);
    });
    pMatrix = makePerspectiveMatrix(75, canvas.width / canvas.height, 0.1, 500);
    pMatrix.copyIntoArray(pMatrixList);

    for (ImageElement img in blockTexturesRaw) {
        blockTextures.add(loadTexture(gl, img));
    }

    var chunkVertexShader = createShader(gl, chunkVertexShaderSource, VERTEX_SHADER);
    var chunkFragmentShader = createShader(gl, chunkFragmentShaderSource, FRAGMENT_SHADER);
    mainProgram = createProgram(gl, chunkVertexShader, chunkFragmentShader);
    pMatrixLocation = gl.getUniformLocation(mainProgram, "pMatrix");
    uMatrixLocation = gl.getUniformLocation(mainProgram, "uMatrix");
    offsetLocation = gl.getUniformLocation(mainProgram, "offset");
    frameLocation = gl.getUniformLocation(mainProgram, "frame");
    blockTextureLocation = gl.getUniformLocation(mainProgram, "texture");
    disAlphaLocation = gl.getUniformLocation(mainProgram, "disAlpha");
    positionLocation = gl.getAttribLocation(mainProgram, "position");
    colourLocation = gl.getAttribLocation(mainProgram, "colour");
    textureIdLocation = gl.getAttribLocation(mainProgram, "textureId");
    textirePosLocation = gl.getAttribLocation(mainProgram, "texturePos");
    gl.enableVertexAttribArray(positionLocation);
    gl.enableVertexAttribArray(colourLocation);
    gl.enableVertexAttribArray(textureIdLocation);
    gl.enableVertexAttribArray(textirePosLocation);

    gl.enable(DEPTH_TEST);
    gl.enable(CULL_FACE);
    gl.cullFace(BACK);
    gl.frontFace(CCW);

    gl.enable(BLEND);
    gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

    int viewDist = 10;
    for (int x = -viewDist; x < viewDist; x++) {
        for (int z = -viewDist; z < viewDist; z++) {
            world.addChunk(new Chunk(x, z));
        }
    }

    draw(0);

    window.onMouseMove.listen((e) {
        camera.rotY = (e.client.x - (window.innerWidth ~/ 2)) / 200.0;
        camera.rotX = ((e.client.y - (window.innerHeight ~/ 2)) / 200.0);
    });
    window.onKeyDown.where((e) => e.keyCode == KeyCode.W).listen((e) {
        movingForward = true;
        window.onKeyUp.firstWhere((e) => e.keyCode == KeyCode.W).then((e) {
            movingForward = false;
        });
    });
    window.onKeyDown.where((e) => e.keyCode == KeyCode.S).listen((e) {
        movingBackwards = true;
        window.onKeyUp.firstWhere((e) => e.keyCode == KeyCode.S).then((e) {
            movingBackwards = false;
        });
    });
}

Matrix4 pMatrix;
Float32List pMatrixList = new Float32List(4 * 4);
Matrix4 uMatrix = new Matrix4.identity();
Float32List uMatrixList = new Float32List(4 * 4);

bool movingForward = false;
bool movingBackwards = false;
Camera camera = new Camera()..y = 60.0;

draw(num highResTime) {
    gl.viewport(0, 0, canvas.width, canvas.height);
    double skyPosition = getScale();
    gl.clearColor(getScaledNumber(122.0 / 255.0, 0.0, skyPosition), getScaledNumber(165.0 / 255.0, 0.0, skyPosition), getScaledNumber(247.0 / 255.0, 0.0, skyPosition), 1);
    gl.colorMask(true, true, true, false);
    gl.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

    gl.useProgram(mainProgram);
    gl.uniformMatrix4fv(pMatrixLocation, false, pMatrixList);

    gl.activeTexture(TEXTURE0);
    gl.bindTexture(TEXTURE_2D, blockTextures[0]);
    gl.uniform1i(blockTextureLocation, 0);
    gl.uniform1f(frameLocation, world.currentTime);

    if (movingForward) {
        camera.x -= sin(camera.rotY) * cos(camera.rotX);
        camera.z -= cos(camera.rotY) * cos(camera.rotX);
        camera.y -= sin(camera.rotX);
    } else if (movingBackwards) {
        camera.x += sin(camera.rotY) * cos(camera.rotX);
        camera.z += cos(camera.rotY) * cos(camera.rotX);
        camera.y += sin(camera.rotX);
    }

    uMatrix.setIdentity();

    uMatrix.scale(1.0, -1.0, 1.0);
    uMatrix.rotateX(camera.rotX);
    uMatrix.rotateY(camera.rotY);
    uMatrix.translate(-camera.x ,-camera.y, -camera.z);
    uMatrix.copyIntoArray(uMatrixList);
    gl.uniformMatrix4fv(uMatrixLocation, false, uMatrixList);

    world.render(gl);

    gl.clearColor(1, 1, 1, 1);
    gl.colorMask(false, false, false, true);
    gl.clear(COLOR_BUFFER_BIT);

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

class Camera {
    double x = 0.0;
    double y = 0.0;
    double z = 0.0;

    double rotX = 0.0;
    double rotY = 0.0;
}

// Because dart is broken
int leftShift(int v, int x) => v < 0 ? -((-v)<<x) : v << x;