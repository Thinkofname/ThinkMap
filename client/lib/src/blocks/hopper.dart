part of map_viewer;

class BlockHopper {

  static Model _model;
  static Model get model {
    if (_model != null) return _model;

    _model = new Model();

    _model.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "hopper_top"
      ..moveY(16));
    _model.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "hopper_inside"
      ..moveY(10));

    // Inside

    _model.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveX(2)
      ..moveX(2, true)
      ..sizeX(-4)
      ..sizeX(-4, true)
      ..moveZ(2));

    _model.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveX(2)
      ..moveX(2, true)
      ..sizeX(-4)
      ..sizeX(-4, true)
      ..moveZ(14));

    _model.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveZ(2)
      ..moveX(2, true)
      ..sizeZ(-4)
      ..sizeX(-4, true)
      ..moveX(2));

    _model.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveZ(2)
      ..moveX(2, true)
      ..sizeZ(-4)
      ..sizeX(-4, true)
      ..moveX(14));

    // Outside - top

    _model.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true));
    _model.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveZ(16));

    _model.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveX(0));
    _model.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "hopper_outside"
      ..moveY(10)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveX(16));

    _model.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "hopper_outside"
      ..moveY(10));

    // Outside - middle

    _model.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "hopper_outside"
      ..moveY(4)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveY(6, true)
      ..moveZ(4)
      ..moveX(4)
      ..moveX(4, true)
      ..sizeX(-8)
      ..sizeX(-8, true));
    _model.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "hopper_outside"
      ..moveY(4)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveY(6, true)
      ..moveZ(12)
      ..moveX(4)
      ..moveX(4, true)
      ..sizeX(-8)
      ..sizeX(-8, true));

    _model.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "hopper_outside"
      ..moveY(4)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveY(6, true)
      ..moveX(4)
      ..moveZ(4)
      ..moveX(4, true)
      ..sizeZ(-8)
      ..sizeX(-8, true));
    _model.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "hopper_outside"
      ..moveY(4)
      ..sizeY(-10)
      ..sizeY(-10, true)
      ..moveY(6, true)
      ..moveX(12)
      ..moveZ(4)
      ..moveX(4, true)
      ..sizeZ(-8)
      ..sizeX(-8, true));

    _model.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "hopper_outside"
      ..moveX(4)
      ..moveX(4, true)
      ..sizeX(-8)
      ..sizeX(-8, true)
      ..moveZ(4)
      ..moveY(4, true)
      ..sizeZ(-8)
      ..sizeY(-8, true)
      ..moveY(4));

    return _model;
  }

  static Model _spout;
  static Model get spout {
    if (_spout != null) return _spout;

    _spout = new Model();

    _spout.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "hopper_outside"
      ..sizeY(-12)
      ..sizeY(-12, true)
      ..moveY(12, true)
      ..moveX(6, true)
      ..sizeX(-12)
      ..sizeX(-12, true));
    _spout.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "hopper_outside"
      ..sizeY(-12)
      ..sizeY(-12, true)
      ..moveY(12, true)
      ..moveZ(4)
      ..moveX(6, true)
      ..sizeX(-12)
      ..sizeX(-12, true));

    _spout.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "hopper_outside"
      ..sizeY(-12)
      ..sizeY(-12, true)
      ..moveY(12, true)
      ..moveX(6, true)
      ..sizeZ(-12)
      ..sizeX(-12, true));
    _spout.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "hopper_outside"
      ..sizeY(-12)
      ..sizeY(-12, true)
      ..moveY(12, true)
      ..moveX(4)
      ..moveX(6, true)
      ..sizeZ(-12)
      ..sizeX(-12, true));

    _spout.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "hopper_outside"
      ..moveX(6, true)
      ..sizeX(-12)
      ..sizeX(-12, true)
      ..moveY(6, true)
      ..sizeZ(-12)
      ..sizeY(-12, true));
    _spout.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "hopper_outside"
      ..moveX(6, true)
      ..sizeX(-12)
      ..sizeX(-12, true)
      ..moveY(6, true)
      ..sizeZ(-12)
      ..sizeY(-12, true)
      ..moveY(4));

    return _spout;
  }
}