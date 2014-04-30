package uk.co.thinkofdeath.mapviewer.client.render;

import java.util.Comparator;

class ChunkSorter implements Comparator<ChunkRenderObject> {

    private final Camera camera;

    public ChunkSorter(Camera camera) {
        this.camera = camera;
    }

    @Override
    public int compare(ChunkRenderObject a, ChunkRenderObject b) {
        int ax = (a.x << 4) + 8 - (int) camera.getX();
        int ay = (a.y << 4) + 8 - (int) camera.getY();
        int az = (a.z << 4) + 8 - (int) camera.getZ();
        int bx = (b.x << 4) + 8 - (int) camera.getX();
        int by = (b.y << 4) + 8 - (int) camera.getY();
        int bz = (b.z << 4) + 8 - (int) camera.getZ();
        return (bx * bx + by * by + bz * bz) - (ax * ax + ay * ay + az * az);
    }
}
