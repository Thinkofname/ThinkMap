part of mapViewer;

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
        ..y = 6 * 16
        ..rotX = PI / 3
        ..rotY = PI / 4;
    double vSpeed = MIN_VSPEED;
    static const double MIN_VSPEED = -0.2;
    bool onGround = false;
    int offGroundFor = 0;
    int cx = 0;
    int cz = 0;
    bool firstPerson = false;

    WebGLRenderer(CanvasElement canvas) {
        // Flags are set for performance
        gl = canvas.getContext3d(alpha: false, premultipliedAlpha: false, antialias: false);

        if (gl == null) {
            throw "WebGL not supported";
        }

        resize(0, 0);

        // Convert images to textures
        for (ImageElement img in blockTexturesRaw) {
            blockTextures.add(loadTexture(gl, img));
        }

        var chunkVertexShader = createShader(gl, chunkVertexShaderSource, VERTEX_SHADER);
        var chunkFragmentShader = createShader(gl, chunkFragmentShaderSource, FRAGMENT_SHADER);
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
        gl.enableVertexAttribArray(positionLocation);
        gl.enableVertexAttribArray(colourLocation);
        gl.enableVertexAttribArray(textureIdLocation);
        gl.enableVertexAttribArray(texturePosLocation);

        gl.enable(DEPTH_TEST);
        gl.enable(CULL_FACE);
        gl.cullFace(BACK);
        gl.frontFace(CW);

        gl.enable(BLEND);
        gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

        // 3D Controls
        document.body.onMouseDown.listen((e) {if (document.pointerLockElement != canvas && firstPerson) canvas.requestPointerLock(); });
        document.body.onMouseMove.listen((e) {
            if (document.pointerLockElement != canvas || !firstPerson) return;
            camera.rotY += e.movement.x / 300.0;
            camera.rotX += e.movement.y / 300.0;
        });
        document.body.onKeyDown.where((e) => e.keyCode == KeyCode.W).listen((e) {
            movingForward = true;
            window.onKeyUp.firstWhere((e) => e.keyCode == KeyCode.W).then((e) {
                movingForward = false;
            });
            if (document.pointerLockElement != canvas && firstPerson) canvas.requestPointerLock();
        });
        document.body.onKeyDown.where((e) => e.keyCode == KeyCode.S).listen((e) {
            movingBackwards = true;
            window.onKeyUp.firstWhere((e) => e.keyCode == KeyCode.S).then((e) {
                movingBackwards = false;
            });
        });
        document.body.onKeyDown.where((e) => e.keyCode == KeyCode.SPACE).listen((e) {
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
            if (firstPerson) return;
            JsObject jse = new JsObject.fromBrowserObject(e);
            // TODO: Fix once dart fixes this bug
            // zoom += e.wheelDeltaY;
            if (jse["deltaY"] != null) {
                camera.y -= (jse["deltaY"] as int) < 0 ? -1.0 : 1.0;
            } else {
                camera.y -= (jse["wheelDeltaY"] as int) < 0 ? -1.0 : 1.0;
            }
            e.preventDefault();
        });
        // Misc
        document.body.onKeyDown.where((e) => e.keyCode == KeyCode.F).listen((e) {
            canvas.requestFullscreen();
        });
        document.body.onKeyDown.where((e) => e.keyCode == KeyCode.G).listen((e) {
            firstPerson = !firstPerson;
            if (!firstPerson) {
                camera..rotX = PI / 3
                    ..rotY = PI / 4;
            }
        });
    }

    static const int viewDistance = 6;

    @override
    draw() {
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

        if (firstPerson) {
            double lx = camera.x;
            double ly = camera.y;
            double lz = camera.z;

            if (world.getChunk(cx, cz) != null) {
                camera.y += vSpeed;
                vSpeed = max(MIN_VSPEED, vSpeed - 0.005);

                if (movingForward) {
                    camera.x += 0.1 * sin(camera.rotY);
                    camera.z -= 0.1 * cos(camera.rotY);
                } else if (movingBackwards) {
                    camera.x -= 0.1 * sin(camera.rotY);
                    camera.z += 0.1 * cos(camera.rotY);
                }
                checkCollision(lx, ly, lz);

                if (onGround) vSpeed = 0.0;
            }
        }


        uMatrix.setIdentity();

        uMatrix.scale(-1.0, -1.0, 1.0);
        uMatrix.rotateX(-camera.rotX - PI);
        uMatrix.rotateY(-camera.rotY - PI);
        uMatrix.translate(-camera.x ,-camera.y, -camera.z);
        uMatrix.copyIntoArray(uMatrixList);
        gl.uniformMatrix4fv(uMatrixLocation, false, uMatrixList);

        (world as WebGLWorld).render(this);

        gl.clearColor(1, 1, 1, 1);
        gl.colorMask(false, false, false, true);
        gl.clear(COLOR_BUFFER_BIT);

        int nx = camera.x ~/ 16;
        int nz = camera.z ~/ 16;
        if (nx != cx || nz != cz) {
            for (int x = nx-viewDistance; x < nx+viewDistance; x++) {
                for (int z = nz-viewDistance; z < nz+viewDistance; z++) {
                    if (world.getChunk(x, z) == null)
                        connection.writeRequestChunk(x, z);
                }
            }

            for (Chunk chunk in new List.from(world.chunks.values)) {
                int x = chunk.x;
                int z = chunk.z;
                if (x < nx-viewDistance || x >= nx+viewDistance
                || z < nz-viewDistance || z >= nz+viewDistance) {
                    world.removeChunk(x, z);
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
    resize(int width, int height) {
        pMatrix = makePerspectiveMatrix(radians(75.0), canvas.width / canvas.height, 0.1, 500);
        pMatrix.copyIntoArray(pMatrixList);
    }

    @override
    connected() {
        for (int x = -viewDistance; x < viewDistance; x++) {
            for (int z = -viewDistance; z < viewDistance; z++) {
                connection.writeRequestChunk(x, z);
            }
        }
    }

    checkCollision(double lx, double ly, double lz) {
        Box box = new Box(lx, ly - 1.6, lz, 0.5, 1.75, 0.5);

        int cx = box.x.toInt();
        int cy = box.y.toInt();
        int cz = box.z.toInt();


        box.x = camera.x;
        cx = box.x.toInt();
        l1:
        for (int x = cx - 2; x < cx + 2; x++) {
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
        l2:
        for (int x = cx - 2; x < cx + 2; x++) {
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
}

class WebGLWorld extends World {

    WebGLWorld() : super();

    Map<String, bool> _waitingForBuild = new Map();
    List<_BuildJob> _buildQueue = new List();
    _BuildJob currentBuild;
    BuildSnapshot currentSnapshot;
    Stopwatch stopwatch = new Stopwatch();

    requestBuild(Chunk chunk, int i) {
        String key = chunk.x.toString() + ":" + chunk.z.toString() + "@" + i.toString();
        if (_waitingForBuild.containsKey(key)) {
            return; // Already queued
        }
        _waitingForBuild[key] = true;
        _buildQueue.add(new _BuildJob(chunk, i));
    }

    static const int BUILD_LIMIT_MS = 8;
    int lastSort = 0;

    render(WebGLRenderer renderer) {
        stopwatch.reset();
        stopwatch.start();
        while (stopwatch.elapsedMilliseconds < BUILD_LIMIT_MS && toLoad.isNotEmpty) {
            addChunk(new WebGLChunk.fromBuffer(this, toLoad.removeLast(), 0));
        }
        bool run = stopwatch.elapsedMilliseconds < BUILD_LIMIT_MS;

        if (run && currentBuild != null) {
            var job = currentBuild;
            BuildSnapshot snapshot = job.chunk.buildSection(job.i, currentSnapshot, stopwatch);
            currentBuild = null;
            currentSnapshot = null;
            if (snapshot != null) {
                currentBuild = job;
                currentSnapshot = snapshot;
                run = false;
            }
        }
        if (_buildQueue.isNotEmpty && lastSort <= 0) {
            lastSort = 10;
            _buildQueue.sort(_queueCompare);
        }
        lastSort--;
        while (run && stopwatch.elapsedMilliseconds < BUILD_LIMIT_MS && _buildQueue.isNotEmpty) {
            var job = _buildQueue.removeLast();
            String key = job.chunk.x.toString() + ":" + job.chunk.z.toString() + "@" + job.i.toString();
            _waitingForBuild.remove(key);
            BuildSnapshot snapshot = job.chunk.buildSection(job.i, null, stopwatch);
            if (snapshot != null) {
                currentBuild = job;
                currentSnapshot = snapshot;
                break;
            }
        }
        stopwatch.stop();
        renderer.gl.uniform1i(renderer.disAlphaLocation, 1);
        chunks.forEach((k, v) {
            v.render(renderer, 0);
        });
        chunks.forEach((k, v) {
            v.render(renderer, 1);
        });
        renderer.gl.uniform1i(renderer.disAlphaLocation, 0);
        chunks.forEach((k, v) {
            v.render(renderer, 2);
        });
    }

    int _queueCompare(_BuildJob a, _BuildJob b) {
        Camera camera = (renderer as WebGLRenderer).camera;
        num adx = (a.chunk.x * 16) + 8 - camera.x;
        num ady = (a.i * 16) + 8 - camera.y;
        num adz = (a.chunk.z * 16) + 8 - camera.z;
        num distA = adx*adx + ady*ady + adz*adz;
        num bdx = (b.chunk.x * 16) + 8 - camera.x;
        num bdy = (b.i * 16) + 8 - camera.y;
        num bdz = (b.chunk.z * 16) + 8 - camera.z;
        num distB = bdx*bdx + bdy*bdy + bdz*bdz;

//        num aa = atan2(camera.z - (a.chunk.z * 16) + 8, camera.x - (a.chunk.x * 16) + 8);
//        num angleA = min((2 * PI) - (camera.rotY - aa).abs(), (camera.rotY - aa).abs());
//
//        num ba = atan2(camera.z - (b.chunk.z * 16) + 8, camera.x - (b.chunk.x * 16) + 8);
//        num angleB = min((2 * PI) - (camera.rotY - ba).abs(), (camera.rotY - ba).abs());
        return distB - distA;
    }
}

class WebGLChunk extends Chunk {

    WebGLChunk(int x, int z, World world) : super(x, z, world);

    WebGLChunk.fromBuffer(World world, ByteBuffer buffer, [int o = 0]) : super.fromBuffer(world, buffer, o);

    List<Uint8List> builtSections = new List(16);
    Buffer renderBuffer;
    int bufferSize = 0;
    int triangleCount = 0;

    List<Uint8List> builtSectionsTrans = new List(16);
    Buffer renderBufferTrans;
    int bufferSizeTrans = 0;
    int triangleCountTrans = 0;

    List<Uint8List> builtSectionsFloat = new List(16);
    Buffer renderBufferFloat;
    int bufferSizeFloat = 0;
    int triangleCountFloat = 0;

    render(WebGLRenderer renderer, int pass) {
        RenderingContext gl = renderer.gl;
        if (needsBuild) {
            needsBuild = false;
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null && section.needsBuild) {
                    section.needsBuild = false;
                    (world as WebGLWorld).requestBuild(this, i);
                }
            }
        }
        if (needsUpdate) {
            needsUpdate = false;
            Uint8List chunkData = new Uint8List(bufferSize);
            int offset = 0;
            Uint8List chunkDataTrans = new Uint8List(bufferSizeTrans);
            int offsetTrans = 0;
            Uint8List chunkDataFloat = new Uint8List(bufferSizeFloat);
            int offsetFloat = 0;
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null) {
                    if (builtSections[i] != null) {
                        chunkData.setAll(offset, builtSections[i]);
                        offset += builtSections[i].length;
                    }
                    if (builtSectionsTrans[i] != null) {
                        chunkDataTrans.setAll(offsetTrans, builtSectionsTrans[i]);
                        offsetTrans += builtSectionsTrans[i].length;
                    }
                    if (builtSectionsFloat[i] != null) {
                        chunkDataFloat.setAll(offsetFloat, builtSectionsFloat[i]);
                        offsetFloat += builtSectionsFloat[i].length;
                    }
                }
            }
            if (renderBuffer == null) {
                renderBuffer = gl.createBuffer();
            }
            gl.bindBuffer(ARRAY_BUFFER, renderBuffer);
            gl.bufferData(ARRAY_BUFFER, chunkData, STATIC_DRAW);

            if (renderBufferTrans == null) {
                renderBufferTrans = gl.createBuffer();
            }
            gl.bindBuffer(ARRAY_BUFFER, renderBufferTrans);
            gl.bufferData(ARRAY_BUFFER, chunkDataTrans, STATIC_DRAW);

            if (renderBufferFloat == null) {
                renderBufferFloat = gl.createBuffer();
            }
            gl.bindBuffer(ARRAY_BUFFER, renderBufferFloat);
            gl.bufferData(ARRAY_BUFFER, chunkDataFloat, STATIC_DRAW);

            triangleCount = chunkData.length ~/ 12;
            triangleCountTrans = chunkDataTrans.length ~/ 12;
            triangleCountFloat = chunkDataFloat.length ~/ 28;
        }
        if (pass == 0 && renderBuffer != null && triangleCount != 0) {
            gl.uniform2f(renderer.offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBuffer);
            gl.vertexAttribPointer(renderer.positionLocation, 3, UNSIGNED_BYTE, false, 12, 0);
            gl.vertexAttribPointer(renderer.colourLocation, 3, UNSIGNED_BYTE, true, 12, 3);
            gl.vertexAttribPointer(renderer.texturePosLocation, 2, UNSIGNED_BYTE, false, 12, 6);
            gl.vertexAttribPointer(renderer.textureIdLocation, 2, UNSIGNED_SHORT, false, 12, 8);
            gl.drawArrays(TRIANGLES, 0, triangleCount);
        } else if (pass == 1 && renderBufferFloat != null && triangleCountFloat != 0) {
            gl.uniform2f(renderer.offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBufferFloat);
            gl.vertexAttribPointer(renderer.positionLocation, 3, FLOAT, false, 28, 0);
            gl.vertexAttribPointer(renderer.colourLocation, 3, UNSIGNED_BYTE, true, 28, 12);
            gl.vertexAttribPointer(renderer.texturePosLocation, 2, FLOAT, false, 28, 16);
            gl.vertexAttribPointer(renderer.textureIdLocation, 2, UNSIGNED_SHORT, false, 28, 24);
            gl.drawArrays(TRIANGLES, 0, triangleCountFloat);
        } else if (pass == 2 && renderBufferTrans != null && triangleCountTrans != 0) {
            gl.uniform2f(renderer.offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBufferTrans);
            gl.vertexAttribPointer(renderer.positionLocation, 3, UNSIGNED_BYTE, false, 12, 0);
            gl.vertexAttribPointer(renderer.colourLocation, 3, UNSIGNED_BYTE, true, 12, 3);
            gl.vertexAttribPointer(renderer.texturePosLocation, 2, UNSIGNED_BYTE, false, 12, 6);
            gl.vertexAttribPointer(renderer.textureIdLocation, 2, UNSIGNED_SHORT, false, 12, 8);
            gl.drawArrays(TRIANGLES, 0, triangleCountTrans);
        }
    }

    BuildSnapshot buildSection(int i, BuildSnapshot snapshot, Stopwatch stopwatch) {
        if (snapshot == null) {
            snapshot = new BuildSnapshot();
        }
        BlockBuilder builder = snapshot.builder;
        BlockBuilder builderTrans = snapshot.builderTrans;
        for (int x = snapshot.x; x < 16; x++) {
            int sz = x == snapshot.x ? snapshot.z : 0;
            for (int z = sz; z < 16; z++) {
                int sy = 0;
                if (z == snapshot.z){
                    sy = snapshot.y;
                    snapshot.z = -1;
                }
                for (int y = sy; y < 16; y++) {
                    Block block = getBlock(x, (i << 4) + y, z);
                    if (block.renderable) {
                        block.renderFloat(block.transparent ? builderTrans : builder, snapshot.builderFloat, x, (i << 4) + y, z, this);
                    }
                    if (stopwatch.elapsedMilliseconds >= WebGLWorld.BUILD_LIMIT_MS) {
                        snapshot.x = x;
                        snapshot.y = y + 1;
                        snapshot.z = z;
                        return snapshot;
                    }
                }
            }
        }
        // Resize
        bufferSize -= builtSections[i] != null ? builtSections[i].length : 0;
        bufferSizeTrans -= builtSectionsTrans[i] != null ? builtSectionsTrans[i].length : 0;
        bufferSizeFloat -= builtSectionsFloat[i] != null ? builtSectionsFloat[i].length : 0;
        // Store
        builtSections[i] = builder.toTypedList();
        builtSectionsTrans[i] = builderTrans.toTypedList();
        builtSectionsFloat[i] = snapshot.builderFloat.toTypedList();
        // Resize
        bufferSize += builtSections[i].length;
        bufferSizeTrans += builtSectionsTrans[i].length;
        bufferSizeFloat += builtSectionsFloat[i].length;
        needsUpdate = true;
        return null;
    }

    @override
    unload(Renderer renderer) {
        RenderingContext gl = (renderer as WebGLRenderer).gl;
        if (renderBuffer != null) {
            gl.deleteBuffer(renderBuffer);
        }
        if (renderBufferTrans != null) {
            gl.deleteBuffer(renderBufferTrans);
        }
        if (renderBufferFloat != null) {
            gl.deleteBuffer(renderBufferFloat);
        }
    }
}


class Camera {
    double x = 0.0;
    double y = 0.0;
    double z = 0.0;

    double rotX = 0.0;
    double rotY = 0.0;
}

class BuildSnapshot {
    BlockBuilder builder = new BlockBuilder();
    BlockBuilder builderTrans = new BlockBuilder();
    FloatBlockBuilder builderFloat = new FloatBlockBuilder();
    int x = 0;
    int y = 0;
    int z = 0;
}