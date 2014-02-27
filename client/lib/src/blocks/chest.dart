part of map_viewer;

class BlockChest {

  static Model _model;
  static Model get model {
    if (_model != null) return _model;

    Model modelBottom = new Model();
    //Bottom
    modelBottom.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "chest_side_front"
      ..moveZ(15/16)
      ..sizeX(-2/16, true)
      ..sizeX(-2/16)
      ..sizeY(-6/16, true)
      ..sizeY(-6/16)
      ..moveX(1/16));
    modelBottom.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "chest_side"
      ..moveZ(1/16)
      ..sizeX(-2/16, true)
      ..sizeX(-2/16)
      ..sizeY(-6/16, true)
      ..sizeY(-6/16)
      ..moveX(1/16));
    modelBottom.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "chest_side"
      ..sizeX(-2/16, true)
      ..sizeZ(-2/16)
      ..sizeY(-6/16, true)
      ..sizeY(-6/16)
      ..moveX(15/16)
      ..moveZ(1/16));
    modelBottom.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "chest_side"
      ..sizeX(-2/16, true)
      ..sizeZ(-2/16)
      ..sizeY(-6/16, true)
      ..sizeY(-6/16)
      ..moveX(1/16)
      ..moveZ(1/16));
    modelBottom.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "chest_bottom_top"
      ..moveY(10/16)
      ..sizeX(-2/16)
      ..sizeZ(-2/16)
      ..sizeX(-2/16, true)
      ..sizeY(-2/16, true)
      ..moveX(1/16)
      ..moveZ(1/16));
    modelBottom.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "chest_top"
      ..sizeX(-2/16)
      ..sizeZ(-2/16)
      ..sizeX(-2/16, true)
      ..sizeY(-2/16, true)
      ..moveX(1/16)
      ..moveZ(1/16));

    Model modelLid = new Model();

    //Top
    modelLid.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "chest_side_front"
      ..moveZ(15/16)
      ..sizeX(-2/16, true)
      ..sizeX(-2/16)
      ..sizeY(-11/16, true)
      ..sizeY(-11/16)
      ..moveX(1/16)
      ..moveY(9/16)
      ..moveY(10/16, true));
    modelLid.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "chest_side"
      ..moveZ(1/16)
      ..sizeX(-2/16, true)
      ..sizeX(-2/16)
      ..sizeY(-11/16, true)
      ..sizeY(-11/16)
      ..moveX(1/16)
      ..moveY(9/16)
      ..moveY(10/16, true));
    modelLid.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "chest_side"
      ..sizeX(-2/16, true)
      ..sizeZ(-2/16)
      ..sizeY(-11/16, true)
      ..sizeY(-11/16)
      ..moveX(15/16)
      ..moveZ(1/16)
      ..moveY(9/16)
      ..moveY(10/16, true));
    modelLid.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "chest_side"
      ..sizeX(-2/16, true)
      ..sizeZ(-2/16)
      ..sizeY(-11/16, true)
      ..sizeY(-11/16)
      ..moveX(1/16)
      ..moveZ(1/16)
      ..moveY(9/16)
      ..moveY(10/16, true));
    modelLid.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "chest_top"
      ..moveY(5/16)
      ..sizeX(-2/16)
      ..sizeZ(-2/16)
      ..sizeX(-2/16, true)
      ..sizeY(-2/16, true)
      ..moveX(1/16)
      ..moveZ(1/16)
      ..moveY(9/16));
    modelLid.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "chest_top_bottom"
      ..sizeX(-2/16)
      ..sizeZ(-2/16)
      ..sizeX(-2/16, true)
      ..sizeY(-2/16, true)
      ..moveX(1/16)
      ..moveZ(1/16)
      ..moveY(9/16));

    //Lock
    modelLid.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "chest_lock"
      ..moveZ(1/16)
      ..sizeX(-14/16, true)
      ..sizeX(-14/16)
      ..sizeY(-12/16, true)
      ..sizeY(-12/16)
      ..moveX(7/16)
      ..moveY(7/16)
      ..moveX(1/16, true)
      ..moveY(1/16, true)
      ..moveZ(15/16));
    modelLid.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "chest_lock"
      ..sizeX(-15/16, true)
      ..sizeZ(-15/16)
      ..sizeY(-12/16, true)
      ..sizeY(-12/16)
      ..moveX(9/16)
      ..moveY(7/16)
      ..moveY(1/16, true)
      ..moveZ(15/16));
    modelLid.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "chest_lock"
      ..sizeX(-15/16, true)
      ..sizeZ(-15/16)
      ..sizeY(-12/16, true)
      ..sizeY(-12/16)
      ..moveX(7/16)
      ..moveY(7/16)
      ..moveY(1/16, true)
      ..moveZ(15/16));
    modelLid.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "chest_lock"
      ..moveY(4/16)
      ..sizeX(-14/16)
      ..sizeZ(-15/16)
      ..sizeX(-14/16, true)
      ..sizeY(-15/16, true)
      ..moveX(7/16)
      ..moveY(7/16)
      ..moveX(1/16, true)
      ..moveZ(15/16));
    modelLid.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "chest_lock"
      ..sizeX(-14/16)
      ..sizeZ(-15/16)
      ..sizeX(-14/16, true)
      ..sizeY(-15/16, true)
      ..moveX(7/16)
      ..moveY(7/16)
      ..moveX(3/16, true)
      ..moveZ(15/16));
    return modelBottom.join(modelLid);
  }
}