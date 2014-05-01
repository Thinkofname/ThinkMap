package uk.co.thinkofdeath.mapviewer.client.render;

import java.util.Comparator;

class SortableSorter implements Comparator<SortableRenderObject> {

    private final Camera camera;

    public SortableSorter(Camera camera) {
        this.camera = camera;
    }

    @Override
    public int compare(SortableRenderObject a, SortableRenderObject b) {
        int ax = (a.getX() << 4) + 8 - (int) camera.getX();
        int ay = (a.getY() << 4) + 8 - (int) camera.getY();
        int az = (a.getZ() << 4) + 8 - (int) camera.getZ();
        int bx = (b.getX() << 4) + 8 - (int) camera.getX();
        int by = (b.getY() << 4) + 8 - (int) camera.getY();
        int bz = (b.getZ() << 4) + 8 - (int) camera.getZ();
        return (ax * ax + ay * ay + az * az) - (bx * bx + by * by + bz * bz);
    }
}
