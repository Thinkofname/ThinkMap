part of mapViewer;

class Box {
    double x;
    double y;
    double z;
    double w;
    double h;
    double d;

    Box(this.x, this.y, this.z, this.w, this.h, this.d);

    bool checkBox(double ox, double oy, double oz, double ow, double oh, double od) {
        double rx = x - (w/2.0);
        double ry = y;
        double rz = z - (d/2.0);
        return !(
            rx + w < ox ||
            rx > ox + ow ||
            ry + h < oy ||
            ry > oy + oh ||
            rz + d < oz ||
            rz > oz + od
        );
    }
}