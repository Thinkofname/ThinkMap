part of map_viewer;

class BlockChest {

  static Model _model;
  static Model get model {
    if (_model != null) return _model;

    Model modelBottom = new Model();
    //Bottom
    modelBottom.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "chest_side_front"
      ..moveZ(15)
      ..sizeX(-2, true)
      ..sizeX(-2)
      ..sizeY(-6, true)
      ..sizeY(-6)
      ..moveX(1));
    modelBottom.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "chest_side"
      ..moveZ(1)
      ..sizeX(-2, true)
      ..sizeX(-2)
      ..sizeY(-6, true)
      ..sizeY(-6)
      ..moveX(1));
    modelBottom.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "chest_side"
      ..sizeX(-2, true)
      ..sizeZ(-2)
      ..sizeY(-6, true)
      ..sizeY(-6)
      ..moveX(15)
      ..moveZ(1));
    modelBottom.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "chest_side"
      ..sizeX(-2, true)
      ..sizeZ(-2)
      ..sizeY(-6, true)
      ..sizeY(-6)
      ..moveX(1)
      ..moveZ(1));
    modelBottom.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "chest_bottom_top"
      ..moveY(10)
      ..sizeX(-2)
      ..sizeZ(-2)
      ..sizeX(-2, true)
      ..sizeY(-2, true)
      ..moveX(1)
      ..moveZ(1));
    modelBottom.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "chest_top"
      ..sizeX(-2)
      ..sizeZ(-2)
      ..sizeX(-2, true)
      ..sizeY(-2, true)
      ..moveX(1)
      ..moveZ(1));

    Model modelLid = new Model();

    //Top
    modelLid.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "chest_side_front"
      ..moveZ(15)
      ..sizeX(-2, true)
      ..sizeX(-2)
      ..sizeY(-11, true)
      ..sizeY(-11)
      ..moveX(1)
      ..moveY(10, true));
    modelLid.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "chest_side"
      ..moveZ(1)
      ..sizeX(-2, true)
      ..sizeX(-2)
      ..sizeY(-11, true)
      ..sizeY(-11)
      ..moveX(1)
      ..moveY(10, true));
    modelLid.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "chest_side"
      ..sizeX(-2, true)
      ..sizeZ(-2)
      ..sizeY(-11, true)
      ..sizeY(-11)
      ..moveX(15)
      ..moveZ(1)
      ..moveY(10, true));
    modelLid.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "chest_side"
      ..sizeX(-2, true)
      ..sizeZ(-2)
      ..sizeY(-11, true)
      ..sizeY(-11)
      ..moveX(1)
      ..moveZ(1)
      ..moveY(10, true));
    modelLid.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "chest_top"
      ..moveY(5)
      ..sizeX(-2)
      ..sizeZ(-2)
      ..sizeX(-2, true)
      ..sizeY(-2, true)
      ..moveX(1)
      ..moveZ(1));
    modelLid.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "chest_top_bottom"
      ..sizeX(-2)
      ..sizeZ(-2)
      ..sizeX(-2, true)
      ..sizeY(-2, true)
      ..moveX(1)
      ..moveZ(1));

    //Lock
    modelLid.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "chest_lock"
      ..moveZ(1)
      ..sizeX(-14, true)
      ..sizeX(-14)
      ..sizeY(-12, true)
      ..sizeY(-12)
      ..moveX(7)
      ..moveY(-2)
      ..moveX(1, true)
      ..moveY(1, true)
      ..moveZ(15));
    modelLid.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "chest_lock"
      ..sizeX(-15, true)
      ..sizeZ(-15)
      ..sizeY(-12, true)
      ..sizeY(-12)
      ..moveX(9)
      ..moveY(-2)
      ..moveY(1, true)
      ..moveZ(15));
    modelLid.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "chest_lock"
      ..sizeX(-15, true)
      ..sizeZ(-15)
      ..sizeY(-12, true)
      ..sizeY(-12)
      ..moveX(7)
      ..moveY(-2)
      ..moveY(1, true)
      ..moveZ(15));
    modelLid.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "chest_lock"
      ..moveY(4)
      ..sizeX(-14)
      ..sizeZ(-15)
      ..sizeX(-14, true)
      ..sizeY(-15, true)
      ..moveX(7)
      ..moveY(-2)
      ..moveX(1, true)
      ..moveZ(15));
    modelLid.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "chest_lock"
      ..sizeX(-14)
      ..sizeZ(-15)
      ..sizeX(-14, true)
      ..sizeY(-15, true)
      ..moveX(7)
      ..moveY(-2)
      ..moveX(3, true)
      ..moveZ(15));
    return modelBottom.clone()..join(modelLid, 0, 9, 0);
  }
}