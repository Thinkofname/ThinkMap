part of map_viewer;

abstract class Renderer {

    draw();
    resize(int width, int height);
    connected();

    bool shouldLoad(int x, int z);

    moveTo(int x, int y, int z);
}

class _BuildJob {
    Chunk chunk;
    int i;
    _BuildJob(this.chunk, this.i);

    Object exec(Object snapshot, Stopwatch stopwatch) {
        return chunk.buildSection(i, snapshot, stopwatch);
    }
}