part of map_viewer;

/**
 * A simple box used for collision checking
 */
class Box {
  /// X coordinate
  double x;
  /// Y coordinate
  double y;
  /// Z coordinate
  double z;
  /// Width
  double w;
  /// Height
  double h;
  /// Depth
  double d;

  /// Creates a new box
  Box(this.x, this.y, this.z, this.w, this.h, this.d);

  /**
   * Checks whether this box collides with the box created from the
   * parameters.
   */
  bool checkBox(double ox, double oy, double oz, double ow, double oh, double
      od) {
    double rx = x - (w / 2.0);
    double ry = y;
    double rz = z - (d / 2.0);
    return !(rx + w < ox || rx > ox + ow || ry + h < oy || ry > oy + oh || rz +
        d < oz || rz > oz + od);
  }
}
