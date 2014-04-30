package uk.co.thinkofdeath.mapviewer.client.render;

import uk.co.thinkofdeath.mapviewer.shared.model.SendableModel;

import java.util.Comparator;

class ModelSorter implements Comparator<SendableModel> {

    private final Camera camera;
    private final int cx;
    private final int cz;

    public ModelSorter(int cx, int cz, Camera camera) {
        this.camera = camera;
        this.cx = cx;
        this.cz = cz;
    }

    @Override
    public int compare(SendableModel a, SendableModel b) {
        double ax = (cx << 4) + a.getX() + 0.5 - camera.getX();
        double ay = a.getY() + 0.5 - camera.getY();
        double az = (cz << 4) + a.getZ() + 0.5 - camera.getZ();
        double bx = (cx << 4) + b.getX() + 0.5 - camera.getX();
        double by = b.getY() + 0.5 - camera.getY();
        double bz = (cz << 4) + b.getZ() + 0.5 - camera.getZ();
        return (int) (((bx * bx + by * by + bz * bz) - (ax * ax + ay * ay + az * az)) * 32d);
    }
}
