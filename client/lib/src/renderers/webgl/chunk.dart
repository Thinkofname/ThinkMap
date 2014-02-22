part of map_viewer;

class WebGLChunk extends Chunk {

  List<Uint8List> builtSections = new List(16);
  List<Buffer> normalBuffers = new List(16);
  List<int> normalTriangleCount = new List(16);

  List<Uint8List> builtSectionsTrans = new List(16);
  List<Buffer> transBuffers = new List(16);
  List<int> transTriangleCount = new List(16);

  List<Uint8List> builtSectionsFloat = new List(16);
  List<Buffer> floatBuffers = new List(16);
  List<int> floatTriangleCount = new List(16);

  List<Aabb3> sectionAABBs;

  WebGLChunk();

  @override
  void init() {
    sectionAABBs = new List(16);
    for (int i = 0; i < 16; i++) {
      var sectionAABB = new Aabb3();
      sectionAABB.min.setValues(x * 16.0, i * 16.0, z * 16.0);
      sectionAABB.max.setValues(x * 16.0 + 16.0, i * 16.0 + 16.0, z * 16.0 + 16.0);
      sectionAABBs[i] = sectionAABB;
    }
  }

  void render(WebGLRenderer renderer, int pass) {
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
      _updateChunkBuffers(gl);
    }

    // TODO: Simplify (#23)
    if (pass == 0) {
      for (int i = 0; i < 16; i++) {
        var section = sections[i];
        if (section == null) continue;

        if (!renderer.viewFrustum.intersectsWithAabb3(sectionAABBs[i])) continue;

        if (normalBuffers[i] != null && normalTriangleCount[i] != 0) {
          gl.uniform2f(renderer.offsetLocation, x, z);
          gl.bindBuffer(ARRAY_BUFFER, normalBuffers[i]);
          gl.vertexAttribPointer(renderer.positionLocation, 3, UNSIGNED_BYTE,
          false, 14, 0);
          gl.vertexAttribPointer(renderer.colourLocation, 3, UNSIGNED_BYTE, true,
          14, 3);
          gl.vertexAttribPointer(renderer.texturePosLocation, 2, UNSIGNED_BYTE,
          false, 14, 6);
          gl.vertexAttribPointer(renderer.textureIdLocation, 2, UNSIGNED_SHORT,
          false, 14, 8);
          gl.vertexAttribPointer(renderer.lightingLocation, 2, BYTE, false, 14, 12
          );
          gl.drawArrays(TRIANGLES, 0, normalTriangleCount[i]);
        }
        if (floatBuffers[i] != null && floatTriangleCount[i] != 0) {
          gl.uniform2f(renderer.offsetLocation, x, z);
          gl.bindBuffer(ARRAY_BUFFER, floatBuffers[i]);
          gl.vertexAttribPointer(renderer.positionLocation, 3, FLOAT, false, 32, 0
          );
          gl.vertexAttribPointer(renderer.colourLocation, 3, UNSIGNED_BYTE, true,
          32, 12);
          gl.vertexAttribPointer(renderer.texturePosLocation, 2, FLOAT, false, 32,
          16);
          gl.vertexAttribPointer(renderer.textureIdLocation, 2, UNSIGNED_SHORT,
          false, 32, 24);
          gl.vertexAttribPointer(renderer.lightingLocation, 2, BYTE, false, 32, 28
          );
          gl.drawArrays(TRIANGLES, 0, floatTriangleCount[i]);
        }
      }
    } else if (pass == 1) {
      for (int i = 0; i < 16; i++) {
        var section = sections[i];
        if (section == null) continue;

        if (!renderer.viewFrustum.intersectsWithAabb3(sectionAABBs[i])) continue;

        if (transBuffers[i] != null && transTriangleCount[i] != 0) {
          gl.uniform2f(renderer.offsetLocation, x, z);
          gl.bindBuffer(ARRAY_BUFFER, transBuffers[i]);
          gl.vertexAttribPointer(renderer.positionLocation, 3, UNSIGNED_BYTE, false,
          14, 0);
          gl.vertexAttribPointer(renderer.colourLocation, 3, UNSIGNED_BYTE, true,
          14, 3);
          gl.vertexAttribPointer(renderer.texturePosLocation, 2, UNSIGNED_BYTE,
          false, 14, 6);
          gl.vertexAttribPointer(renderer.textureIdLocation, 2, UNSIGNED_SHORT,
          false, 14, 8);
          gl.vertexAttribPointer(renderer.lightingLocation, 2, BYTE, false, 14, 12);
          gl.drawArrays(TRIANGLES, 0, transTriangleCount[i]);
        }
      }
    }
  }

  void _updateChunkBuffers(RenderingContext gl) {
    for (int i = 0; i < 16; i++) {
      var section = sections[i];
      if (section != null) {
        if (builtSections[i] == null || !section.needsUpdate) continue; // Not built yet
        section.needsUpdate = false;

        if (normalBuffers[i] == null) {
          normalBuffers[i] = gl.createBuffer();
        }
        gl.bindBuffer(ARRAY_BUFFER, normalBuffers[i]);
        gl.bufferData(ARRAY_BUFFER, builtSections[i], STATIC_DRAW);

        if (transBuffers[i] == null) {
          transBuffers[i] = gl.createBuffer();
        }
        gl.bindBuffer(ARRAY_BUFFER, transBuffers[i]);
        gl.bufferData(ARRAY_BUFFER, builtSectionsTrans[i], STATIC_DRAW);

        if (floatBuffers[i] == null) {
          floatBuffers[i] = gl.createBuffer();
        }
        gl.bindBuffer(ARRAY_BUFFER, floatBuffers[i]);
        gl.bufferData(ARRAY_BUFFER, builtSectionsFloat[i], STATIC_DRAW);

        normalTriangleCount[i] = builtSections[i].length ~/ 14;
        transTriangleCount[i] = builtSectionsTrans[i].length ~/ 14;
        floatTriangleCount[i] = builtSectionsFloat[i].length ~/ 32;
      }
    }
  }

  @override
  WebGLSnapshot buildSection(int i, WebGLSnapshot snapshot, Stopwatch stopwatch)
  {
    if (snapshot == null) {
      snapshot = new WebGLSnapshot();
    }
    BlockBuilder builder = snapshot.builder;
    BlockBuilder builderTrans = snapshot.builderTrans;
    for (int x = snapshot.x; x < 16; x++) {
      snapshot.x = 0;
      for (int z = snapshot.z; z < 16; z++) {
        snapshot.z = 0;
        for (int y = snapshot.y; y < 16; y++) {
          snapshot.y = 0;
          Block block = getBlock(x, (i << 4) + y, z);
          if (block.renderable) {
            block.renderFloat(block.transparent ? builderTrans : builder,
            snapshot.builderFloat, x, (i << 4) + y, z, this);
          }
          if (stopwatch.elapsedMicroseconds >= World.BUILD_LIMIT_MS) {
            snapshot.x = x;
            snapshot.y = y + 1;
            snapshot.z = z;
            snapshot.builder = builder;
            snapshot.builderTrans = builderTrans;
            return snapshot;
          }
        }
        snapshot.x = snapshot.y = snapshot.z = 0;
      }
      snapshot.x = snapshot.y = snapshot.z = 0;
    }
    // Store
    builtSections[i] = builder.toTypedList();
    builtSectionsTrans[i] = builderTrans.toTypedList();
    builtSectionsFloat[i] = snapshot.builderFloat.toTypedList();

    sections[i].needsUpdate = true;
    needsUpdate = true;
    return null;
  }

  @override
  void unload(Renderer renderer) {
    RenderingContext gl = (renderer as WebGLRenderer).gl;
    for (Buffer buffer in normalBuffers) {
      gl.deleteBuffer(buffer);
    }
    for (Buffer buffer in transBuffers) {
      gl.deleteBuffer(buffer);
    }
    for (Buffer buffer in floatBuffers) {
      gl.deleteBuffer(buffer);
    }
  }
}