part of map_viewer;


class WebGLWorld extends World {

  WebGLWorld(): super();

  Stopwatch renderTimer = new Stopwatch();

  void render(WebGLRenderer renderer) {

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

    World.BUILD_LIMIT_MS = 16000 - renderTimer.elapsedMicroseconds;
    if (World.BUILD_LIMIT_MS < 5000) {
      World.BUILD_LIMIT_MS = 5000;
    }

    renderTimer.reset();
    renderTimer.start();
    tickBuildQueue(renderTimer);
    renderTimer.stop();

    renderTimer.reset();
    renderTimer.start();

    tickLoaders();
  }

  @override
  Chunk newChunk() {
    return new WebGLChunk();
  }

  @override
  int _queueCompare(_BuildJob a, _BuildJob b) {
    Camera camera = (renderer as WebGLRenderer).camera;
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
