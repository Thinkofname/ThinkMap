package uk.co.thinkofdeath.mapviewer.client.world;

import uk.co.thinkofdeath.mapviewer.client.render.Camera;

class ChunkArraySorter implements java.util.Comparator<int[]> {

    private final Camera camera;

    public ChunkArraySorter(Camera camera) {
        this.camera = camera;
    }

    @Override
    public int compare(int[] ints, int[] ints2) {
        int dx1 = (int) (ints[0] * 16 - camera.getX());
        int dz1 = (int) (ints[1] * 16 - camera.getZ());
        int dx2 = (int) (ints2[0] * 16 - camera.getX());
        int dz2 = (int) (ints2[1] * 16 - camera.getZ());
        return (dx1 * dx1 + dz1 * dz1) - (dx2 * dx2 + dz2 * dz2);
    }
}
