part of map_viewer;

class BlockWallSign {

  static Model _model;
  static Model get model {
    if (_model != null) return _model;
    _model =  new Model();
    _model.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "planks_oak"
      ..moveZ(1/16)
      ..sizeY(-8/16)
      ..sizeY(-4/16, true)
      ..sizeX(8/16, true)
      ..moveY(4/16)
      ..moveY(4/16, true)
      ..moveX(18/16, true));
    _model.faces.add(new ModelFace(BlockFace.BACK)
    ..texture = "planks_oak"
      ..sizeY(-8/16)
      ..sizeY(-4/16, true)
      ..sizeX(8/16, true)
      ..moveY(4/16)
      ..moveY(4/16, true)
      ..moveX(18/16, true));
    _model.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "planks_oak"
      ..sizeZ(-15/16)
      ..sizeX(-14/16, true)
      ..moveX(1)
      ..sizeY(-8/16)
      ..sizeY(-4/16, true)
      ..moveY(4/16));
    _model.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "planks_oak"
      ..sizeZ(-15/16)
      ..sizeX(-14/16, true)
      ..sizeY(-8/16)
      ..sizeY(-4/16, true)
      ..moveY(4/16));
    _model.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "planks_oak"
      ..sizeZ(-15/16)
      ..sizeY(-4/16, true)
      ..moveY(12/16));
    _model.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "planks_oak"
      ..sizeZ(-15/16)
      ..sizeY(-4/16, true)
      ..moveY(4/16));
    return _model;
  }
}
