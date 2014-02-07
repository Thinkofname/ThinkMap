part of mapViewer;

abstract class Renderer {

    draw();
    resize(int width, int height);
    connected();
}

class _BuildJob {
    Chunk chunk;
    int i;
    _BuildJob(this.chunk, this.i);
}
