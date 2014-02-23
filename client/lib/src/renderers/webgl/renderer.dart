part of map_viewer;

class WebGLRenderer extends Renderer {

  // Rendering
  RenderingContext gl;

  // Shaders
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
  int texturePosLocation;
  int lightingLocation;

  // Matrices
  Matrix4 pMatrix;
  Float32List pMatrixList = new Float32List(4 * 4);
  Matrix4 uMatrix = new Matrix4.identity();
  Float32List uMatrixList = new Float32List(4 * 4);

  List<Texture> blockTextures = new List();

  // Controls
  bool movingForward = false;
  bool movingBackwards = false;
  Camera camera = new Camera()
      ..y = 6.0 * 16.0
      ..rotX = PI / 3
      ..rotY = PI / 4;
  double vSpeed = MIN_VSPEED;
  static const double MIN_VSPEED = -0.2;
  bool onGround = false;
  int offGroundFor = 0;
  int cx = 0;
  int cz = 0;
  bool firstPerson = false;

  Stopwatch frameTimer = new Stopwatch()..start();

  WebGLRenderer(CanvasElement canvas) {
    // Flags are set for performance
    gl = canvas.getContext3d(alpha: false, premultipliedAlpha: false, antialias:
        false);

    if (gl == null) {
      throw "WebGL not supported";
    }

    resize(0, 0);

    // Convert images to textures
    for (ImageElement img in blockTexturesRaw) {
      blockTextures.add(loadTexture(gl, img));
    }

    var chunkVertexShader = createShader(gl, chunkVertexShaderSource,
        VERTEX_SHADER);
    var chunkFragmentShader = createShader(gl, chunkFragmentShaderSource,
        FRAGMENT_SHADER);
    mainProgram = createProgram(gl, chunkVertexShader, chunkFragmentShader);

    // Setup uniforms and attributes
    pMatrixLocation = gl.getUniformLocation(mainProgram, "pMatrix");
    uMatrixLocation = gl.getUniformLocation(mainProgram, "uMatrix");
    offsetLocation = gl.getUniformLocation(mainProgram, "offset");
    frameLocation = gl.getUniformLocation(mainProgram, "frame");
    blockTextureLocation = gl.getUniformLocation(mainProgram, "texture");
    disAlphaLocation = gl.getUniformLocation(mainProgram, "disAlpha");
    positionLocation = gl.getAttribLocation(mainProgram, "position");
    colourLocation = gl.getAttribLocation(mainProgram, "colour");
    textureIdLocation = gl.getAttribLocation(mainProgram, "textureId");
    texturePosLocation = gl.getAttribLocation(mainProgram, "texturePos");
    lightingLocation = gl.getAttribLocation(mainProgram, "lighting");
    gl.enableVertexAttribArray(positionLocation);
    gl.enableVertexAttribArray(colourLocation);
    gl.enableVertexAttribArray(textureIdLocation);
    gl.enableVertexAttribArray(texturePosLocation);
    gl.enableVertexAttribArray(lightingLocation);

    gl.enable(DEPTH_TEST);
    gl.enable(CULL_FACE);
    gl.cullFace(BACK);
    gl.frontFace(CW);

    gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

    // Model Editor TODO: Remove
    document.body.onKeyDown.listen((e) {
      switch(e.keyCode) {
        // Move
        case KeyCode.P: // Up (+Y)
          testModel.faces.last.moveY(1/16, e.shiftKey);
          break;
        case KeyCode.O: // Down (-Y)
          testModel.faces.last.moveY(-1/16, e.shiftKey);
          break;
        case KeyCode.OPEN_SQUARE_BRACKET: // Left (+x)
          testModel.faces.last.moveX(1/16, e.shiftKey);
          break;
        case KeyCode.CLOSE_SQUARE_BRACKET: // Right (-x)
          testModel.faces.last.moveX(-1/16, e.shiftKey);
          break;
        case KeyCode.APOSTROPHE: // Front (+z)
          testModel.faces.last.moveZ(1/16, e.shiftKey);
          break;
        case KeyCode.SINGLE_QUOTE: // Back (-z)
          testModel.faces.last.moveZ(-1/16, e.shiftKey);
          break;
        // Size
        case KeyCode.U:
          testModel.faces.last.sizeY(1/16, e.shiftKey);
          break;
        case KeyCode.I:
          testModel.faces.last.sizeY(-1/16, e.shiftKey);
          break;
        case KeyCode.J:
          testModel.faces.last.sizeX(1/16, e.shiftKey);
          break;
        case KeyCode.K:
          testModel.faces.last.sizeX(-1/16, e.shiftKey);
          break;
        case KeyCode.L:
          testModel.faces.last.sizeZ(1/16, e.shiftKey);
          break;
        case KeyCode.SEMICOLON:
          testModel.faces.last.sizeZ(-1/16, e.shiftKey);
          break;
        // Adding
        case KeyCode.ONE:
          testModel.faces.add(new ModelFace(BlockFace.TOP)
            ..texture=new JsObject.fromBrowserObject(window).callMethod("prompt"));
          break;
        case KeyCode.TWO:
          testModel.faces.add(new ModelFace(BlockFace.BOTTOM)
            ..texture=new JsObject.fromBrowserObject(window).callMethod("prompt"));
          break;
        case KeyCode.THREE:
          testModel.faces.add(new ModelFace(BlockFace.LEFT)
            ..texture=new JsObject.fromBrowserObject(window).callMethod("prompt"));
          break;
        case KeyCode.FOUR:
          testModel.faces.add(new ModelFace(BlockFace.RIGHT)
            ..texture=new JsObject.fromBrowserObject(window).callMethod("prompt"));
          break;
        case KeyCode.FIVE:
          testModel.faces.add(new ModelFace(BlockFace.FRONT)
            ..texture=new JsObject.fromBrowserObject(window).callMethod("prompt"));
          break;
        case KeyCode.SIX:
          testModel.faces.add(new ModelFace(BlockFace.BACK)
            ..texture=new JsObject.fromBrowserObject(window).callMethod("prompt"));
          break;
        case KeyCode.Z:
          print(JSON.encode(testModel));
          return;
        default:
          return;
      }
      print("Rebuild");
      world.getChunk(cx, cz).rebuild();
    });

    // 3D Controls
    document.body.onMouseDown.listen((e) {
      if (document.pointerLockElement != document.body && firstPerson)
          requestPointerLock(document.body);
    });
    document.body.onMouseMove.listen((e) {
      if (pointerLockElement() != document.body || !firstPerson) return;
      camera.rotY += movementX(e) / 300.0;
      camera.rotX += movementY(e) / 300.0;
    });
    document.body.onKeyDown.where((e) => e.keyCode == KeyCode.W).listen((e) {
      movingForward = true;
      window.onKeyUp.firstWhere((e) => e.keyCode == KeyCode.W).then((e) {
        movingForward = false;
      });
      if (pointerLockElement() != document.body && firstPerson)
          requestPointerLock(document.body);
    });
    document.body.onKeyDown.where((e) => e.keyCode == KeyCode.S).listen((e) {
      movingBackwards = true;
      window.onKeyUp.firstWhere((e) => e.keyCode == KeyCode.S).then((e) {
        movingBackwards = false;
      });
    });
    document.body.onKeyDown.where((e) => e.keyCode == KeyCode.SPACE).listen((e)
        {
      if (firstPerson && (onGround || offGroundFor <= 1)) vSpeed = 0.1;
    });
    // Iso controls
    bool down = false;
    int x = 0;
    int y = 0;
    document.body.onMouseDown.listen((e) {
      if (firstPerson) return;
      down = true;
      x = e.client.x;
      y = e.client.y;
      document.body.onMouseUp.first.then((e) {
        down = false;
      });
    });
    document.body.onMouseMove.where((e) => down).listen((e) {
      if (firstPerson) return;
      double dx = -(e.client.x - x) / 8;
      double dy = (e.client.y - y) / 8;
      camera.x += dx + dy;
      camera.z += dx * 0.5 - dy * 0.5;
      x = e.client.x;
      y = e.client.y;
    });
    document.body.onMouseWheel.listen((e) {
      e.preventDefault();
      if (firstPerson) return;
      JsObject jse = new JsObject.fromBrowserObject(e);
      // TODO: Fix once dart fixes this bug
      // zoom += e.wheelDeltaY;
      if (jse["deltaY"] != null) {
        camera.y -= -(jse["deltaY"] as num) < 0.0 ? -1.0 : 1.0;
      } else {
        camera.y -= (jse["wheelDeltaY"] as num) < 0.0 ? -1.0 : 1.0;
      }
    });
    // Misc
    document.body.onKeyDown.where((e) => e.keyCode == KeyCode.F).listen((e) {
      requestFullScreen(document.body);
    });
    document.body.onKeyDown.where((e) => e.keyCode == KeyCode.G).listen((e) {
      firstPerson = !firstPerson;
      if (!firstPerson) {
        camera
            ..rotX = PI / 3
            ..rotY = PI / 4;
      }
    });
    document.body.onKeyDown.where((e) => e.keyCode == KeyCode.Q).listen((e) {
      world.chunks.forEach((k, v) {
        v.rebuild();
      });
    });
  }

  static const int viewDistance = 4;

  final Frustum viewFrustum = new Frustum();

  @override
  void draw() {
    frameTimer.stop();
    double delta = min(frameTimer.elapsedMicroseconds / (1000000.0 / 60.0), 2.0);
    frameTimer.reset();
    frameTimer.start();

    gl.viewport(0, 0, canvas.width, canvas.height);
    double skyPosition = getScale();
    gl.clearColor(getScaledNumber(122.0 / 255.0, 0.0, skyPosition),
        getScaledNumber(165.0 / 255.0, 0.0, skyPosition), getScaledNumber(247.0 / 255.0,
        0.0, skyPosition), 1);
    gl.colorMask(true, true, true, false);
    gl.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

    gl.useProgram(mainProgram);
    gl.uniformMatrix4fv(pMatrixLocation, false, pMatrixList);

    gl.activeTexture(TEXTURE0);
    gl.bindTexture(TEXTURE_2D, blockTextures[0]);
    gl.uniform1i(blockTextureLocation, 0);
    gl.uniform1f(frameLocation, world.currentTime);

    if (firstPerson) {
      double lx = camera.x;
      double ly = camera.y;
      double lz = camera.z;

      camera.y += vSpeed * delta;
      vSpeed = max(MIN_VSPEED, vSpeed - 0.005 * delta);

      if (movingForward) {
        camera.x += 0.1 * sin(camera.rotY) * delta;
        camera.z -= 0.1 * cos(camera.rotY) * delta;
      } else if (movingBackwards) {
        camera.x -= 0.1 * sin(camera.rotY) * delta;
        camera.z += 0.1 * cos(camera.rotY) * delta;
      }
      checkCollision(lx, ly, lz);

      if (onGround) vSpeed = 0.0;
    }


    uMatrix.setIdentity();

    uMatrix.scale(-1.0, -1.0, 1.0);
    uMatrix.rotateX(-camera.rotX - PI);
    uMatrix.rotateY(-camera.rotY - PI);
    uMatrix.translate(-camera.x, -camera.y, -camera.z);
    uMatrix.copyIntoArray(uMatrixList);
    gl.uniformMatrix4fv(uMatrixLocation, false, uMatrixList);

    viewFrustum.setFromMatrix(pMatrix * uMatrix);

    (world as WebGLWorld).render(this);

    gl.clearColor(1, 1, 1, 1);
    gl.colorMask(false, false, false, true);
    gl.clear(COLOR_BUFFER_BIT);

    int nx = (camera.x.toInt() >> 4).toSigned(32);
    int nz = (camera.z.toInt() >> 4).toSigned(32);
    if (nx != cx || nz != cz) {
      for (Chunk chunk in new List.from(world.chunks.values)) {
        int x = chunk.x;
        int z = chunk.z;
        if (x < nx - viewDistance || x >= nx + viewDistance || z < nz -
            viewDistance || z >= nz + viewDistance) {
          world.removeChunk(x, z);
        }
      }

      for (int x = nx - viewDistance; x < nx + viewDistance; x++) {
        for (int z = nz - viewDistance; z < nz + viewDistance; z++) {
          if (world.getChunk(x, z) == null) connection.writeRequestChunk(x, z);
        }
      }
      cx = nx;
      cz = nz;
    }
  }

  /**
     * Creates a WebGL texture from an ImageElement
     */
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

  @override
  void resize(int width, int height) {
    pMatrix = makePerspectiveMatrix(radians(80.0), canvas.width / canvas.height,
        0.1, 500);
    pMatrix.copyIntoArray(pMatrixList);
  }

  @override
  void connected() {
    for (int x = -viewDistance; x < viewDistance; x++) {
      for (int z = -viewDistance; z < viewDistance; z++) {
        connection.writeRequestChunk(x, z);
      }
    }
  }

  void checkCollision(double lx, double ly, double lz) {
    Box box = new Box(lx, ly - 1.6, lz, 0.5, 1.75, 0.5);

    int cx = box.x.toInt();
    int cy = box.y.toInt();
    int cz = box.z.toInt();


    box.x = camera.x;
    cx = box.x.toInt();
    l1: for (int x = cx - 2; x < cx + 2; x++) {
      for (int z = cz - 2; z < cz + 2; z++) {
        for (int y = cy - 3; y < cy + 3; y++) {
          if (world.getBlock(x, y, z).collidesWith(box, x, y, z)) {
            camera.x = lx;
            box.x = lx;
            break l1;
          }
        }
      }
    }

    box.z = camera.z;
    cz = box.z.toInt();
    l2: for (int x = cx - 2; x < cx + 2; x++) {
      for (int z = cz - 2; z < cz + 2; z++) {
        for (int y = cy - 3; y < cy + 3; y++) {
          if (world.getBlock(x, y, z).collidesWith(box, x, y, z)) {
            camera.z = lz;
            box.z = lz;
            break l2;
          }
        }
      }
    }

    box.y = camera.y - 1.6;
    cy = box.y.toInt();
    onGround = false;
    bool hit = false;
    for (int x = cx - 2; x < cx + 2; x++) {
      for (int z = cz - 2; z < cz + 2; z++) {
        for (int y = cy - 3; y < cy + 3; y++) {
          if (world.getBlock(x, y, z).collidesWith(box, x, y, z)) {
            hit = true;
            if (y <= cy) {
              onGround = true;
            }
          }
        }
      }
    }

    if (hit) {
      camera.y = ly;
      box.y = ly - 1.6;
      if (vSpeed > 0.0) vSpeed = 0.0;
    }

    if (!onGround) {
      offGroundFor++;
    } else {
      offGroundFor = 0;
    }
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

  @override
  bool shouldLoad(int x, int z) {
    if (x < cx - viewDistance || x >= cx + viewDistance || z < cz - viewDistance
        || z >= cz + viewDistance) {
      return false;
    }
    return true;
  }

  @override
  void moveTo(int x, int y, int z) {
    camera.x = x.toDouble();
    camera.y = y.toDouble() + 75;
    camera.z = z.toDouble();
  }
}


class Camera {
  double x = 0.0;
  double y = 0.0;
  double z = 0.0;

  double rotX = 0.0;
  double rotY = 0.0;
}
