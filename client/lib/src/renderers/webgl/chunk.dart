part of map_viewer;

class WebGLChunk extends Chunk {

  List<Uint8List> builtSections = new List(16);
  List<Buffer> normalBuffers = new List(16);
  List<int> normalTriangleCount = new List(16);

  List<Uint8List> builtSectionsTrans = new List(16);
  List<Buffer> transBuffers = new List(16);
  List<int> transTriangleCount = new List(16);

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

    for (int i = 0; i < 16; i++) {
      var section = sections[i];
      if (section == null) continue;

      if (!renderer.viewFrustum.intersectsWithAabb3(sectionAABBs[i])) continue;

      if (pass == 0) {
        _render(renderer, gl, normalBuffers[i], normalTriangleCount[i]);
      } else {
        _render(renderer, gl, transBuffers[i], transTriangleCount[i]);
      }
    }
  }

  void _render(WebGLRenderer renderer, RenderingContext gl, Buffer buffer, int triangleCount) {
    if (buffer != null && triangleCount != 0) {
      gl.uniform2f(renderer.offsetLocation, x, z);
      gl.bindBuffer(ARRAY_BUFFER, buffer);
      gl.vertexAttribPointer(renderer.positionLocation, 3, UNSIGNED_SHORT, false, 20, 0);
      gl.vertexAttribPointer(renderer.colourLocation, 3, UNSIGNED_BYTE, true, 20, 6);
      gl.vertexAttribPointer(renderer.texturePosLocation, 2, UNSIGNED_SHORT, false, 20, 10);
      gl.vertexAttribPointer(renderer.textureIdLocation, 2, UNSIGNED_SHORT, false, 20, 14);
      gl.vertexAttribPointer(renderer.lightingLocation, 2, BYTE, false, 20, 18);
      gl.drawArrays(TRIANGLES, 0, triangleCount);
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

        normalTriangleCount[i] = builtSections[i].length ~/ 20;
        transTriangleCount[i] = builtSectionsTrans[i].length ~/ 20;
      }
    }
  }

  @override
  WebGLSnapshot buildSection(int i, WebGLSnapshot snapshot, Stopwatch stopwatch) {
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
            block.render(block.transparent ? builderTrans : builder, x, (i << 4) + y, z, this);
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
  }
}

class WebGLSnapshot {
  BlockBuilder builder;
  BlockBuilder builderTrans;
  int x = 0;
  int y = 0;
  int z = 0;

  WebGLSnapshot() {
    builder = new BlockBuilder();
    builderTrans = new BlockBuilder();
  }
}