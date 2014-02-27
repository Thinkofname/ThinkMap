part of map_viewer;

class BlockWallSign {

  static Model _model;
  static Model get model {
    if (_model != null) return _model;
    _model =  new Model();
    _model.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "planks_oak"
      ..moveZ(1)
      ..sizeY(-8)
      ..sizeY(-4, true)
      ..sizeX(8, true)
      ..moveY(4)
      ..moveY(4, true)
      ..moveX(18, true));
    _model.faces.add(new ModelFace(BlockFace.BACK)
    ..texture = "planks_oak"
      ..sizeY(-8)
      ..sizeY(-4, true)
      ..sizeX(8, true)
      ..moveY(4)
      ..moveY(4, true)
      ..moveX(18, true));
    _model.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "planks_oak"
      ..sizeZ(-15)
      ..sizeX(-14, true)
      ..moveX(16)
      ..sizeY(-8)
      ..sizeY(-4, true)
      ..moveY(4));
    _model.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "planks_oak"
      ..sizeZ(-15)
      ..sizeX(-14, true)
      ..sizeY(-8)
      ..sizeY(-4, true)
      ..moveY(4));
    _model.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "planks_oak"
      ..sizeZ(-15)
      ..sizeY(-4, true)
      ..moveY(12));
    _model.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "planks_oak"
      ..sizeZ(-15)
      ..sizeY(-4, true)
      ..moveY(4));
    return _model;
  }
}
