part of map_viewer;

abstract class Renderer {

  void draw();
  void resize(int width, int height);
  void connected();

  bool shouldLoad(int x, int z);

  void moveTo(int x, int y, int z);
}

class _BuildJob {
  Chunk chunk;
  int i;
  _BuildJob(this.chunk, this.i);

  Object exec(Object snapshot, Stopwatch stopwatch) {
    return chunk.buildSection(i, snapshot, stopwatch);
  }

  void drop(Object snapshot) {
    WebGLSnapshot snap = snapshot as WebGLSnapshot;
    snap.builder._buffer.free();
    snap.builderTrans._buffer.free();
  }
}
