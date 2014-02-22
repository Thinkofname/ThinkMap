part of map_viewer;


class WebGLWorld extends World {

  WebGLWorld(): super();

  Stopwatch renderTimer = new Stopwatch();

  void render(WebGLRenderer renderer) {
    renderTimer.reset();
    renderTimer.start();

    renderer.gl.uniform1i(renderer.disAlphaLocation, 1);
    chunks.forEach((k, v) {
      v.render(renderer, 0);
    });
    renderer.gl.enable(BLEND);
    renderer.gl.uniform1i(renderer.disAlphaLocation, 0);
    chunks.forEach((k, v) {
      v.render(renderer, 1);
    });
    renderer.gl.disable(BLEND);

    renderTimer.stop();

    World.BUILD_LIMIT_MS = 13000 - renderTimer.elapsedMicroseconds;
    if (World.BUILD_LIMIT_MS < 5000) {
      World.BUILD_LIMIT_MS = 5000;
    }
    World.LOAD_LIMIT_MS = World.BUILD_LIMIT_MS - 3000;

    renderTimer.reset();
    renderTimer.start();
    tickBuildQueue(renderTimer);
    renderTimer.stop();
  }

  @override
  Chunk newChunk() {
    return new WebGLChunk();
  }

  @override
  int _queueCompare(_BuildJob a, _BuildJob b) {
    Camera camera = (renderer as WebGLRenderer).camera;
    Frustum frustum = (renderer as WebGLRenderer).viewFrustum;
    if (!(a is _LoadJob) && !(b is _LoadJob)) {
      bool aIn = frustum.intersectsWithAabb3((a.chunk as WebGLChunk).sectionAABBs[a.i]);
      bool bIn = frustum.intersectsWithAabb3((b.chunk as WebGLChunk).sectionAABBs[b.i]);
      if (aIn && !bIn) return 1;
      if (bIn && !aIn) return -1;
    }
    num adx = (a.chunk.x * 16) + 8 - camera.x;
    num ady = (a.i * 16) + 8 - camera.y;
    num adz = (a.chunk.z * 16) + 8 - camera.z;
    num distA = adx * adx + ady * ady + adz * adz;
    num bdx = (b.chunk.x * 16) + 8 - camera.x;
    num bdy = (b.i * 16) + 8 - camera.y;
    num bdz = (b.chunk.z * 16) + 8 - camera.z;
    num distB = bdx * bdx + bdy * bdy + bdz * bdz;
    return (distB - distA).toInt();
  }
}
