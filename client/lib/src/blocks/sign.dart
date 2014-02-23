part of map_viewer;

class BlockWallSign extends Block {

  static void register() {
    Model model = new Model();
    model.faces.add(new ModelFace(BlockFace.FRONT)
      ..texture = "planks_oak"
      ..moveZ(2/16)
      ..sizeY(-8/16)
      ..sizeY(-8/16, true)
      ..moveY(4/16));
    model.faces.add(new ModelFace(BlockFace.BACK)
      ..texture = "planks_oak"
      ..sizeY(-8/16)
      ..sizeY(-8/16, true)
      ..moveY(4/16));
    model.faces.add(new ModelFace(BlockFace.LEFT)
      ..texture = "planks_oak"
      ..sizeZ(-14/16)
      ..sizeX(-14/16, true)
      ..moveX(1)
      ..sizeY(-8/16)
      ..sizeY(-8/16, true)
      ..moveY(4/16));
    model.faces.add(new ModelFace(BlockFace.RIGHT)
      ..texture = "planks_oak"
      ..sizeZ(-14/16)
      ..sizeX(-14/16, true)
      ..sizeY(-8/16)
      ..sizeY(-8/16, true)
      ..moveY(4/16));
    model.faces.add(new ModelFace(BlockFace.TOP)
      ..texture = "planks_oak"
      ..sizeZ(-14/16)
      ..sizeY(-14/16, true)
      ..moveY(12/16));
    model.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..texture = "planks_oak"
      ..sizeZ(-14/16)
      ..sizeY(-14/16, true)
      ..moveY(4/16));
    int i = 0;
    ({
      "north": 2,
      "south": 0,
      "west": 1,
      "east": 3
    }).forEach((k, v) {
      BlockRegistry.registerBlock("wall_sign_$k", new BlockWallSign()
        ..collidable = false
        ..solid = false
        ..texture = "planks_oak"
        ..model = model.rotate(v * 90))
        ..legacyId(68)
        ..dataValue(2 + i)
        ..build();
        i++;
    });

  }

//  @override
//  void renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int
//      y, int z, Chunk chunk) {
//    LightInfo light = LightInfo.getLight(chunk.getLight(x, y, z), chunk.getSky(
//        x, y, z));
//    switch (direction) {
//      case SignDirection.NORTH:
//        addCube(fBulider, x, y + (4.0 / 16.0), z + (15.0 / 16.0), 1, 8.0 / 16.0,
//            1.0 / 16.0, 255, 255, 255, light, getTexture, true);
//        break;
//      case SignDirection.SOUTH:
//        addCube(fBulider, x, y + (4.0 / 16.0), z, 1, 8.0 / 16.0, 1.0 / 16.0,
//            255, 255, 255, light, getTexture, true);
//        break;
//      case SignDirection.EAST:
//        addCube(fBulider, x, y + (4.0 / 16.0), z, 1.0 / 16.0, 8.0 / 16.0, 1,
//            255, 255, 255, light, getTexture, true);
//        break;
//      case SignDirection.WEST:
//        addCube(fBulider, x + (15.0 / 16.0), y + (4.0 / 16.0), z, 1.0 / 16.0,
//            8.0 / 16.0, 1, 255, 255, 255, light, getTexture, true);
//        break;
//    }
//  }
}

class SignDirection {

  static const NORTH = const SignDirection("north");
  static const SOUTH = const SignDirection("south");
  static const WEST = const SignDirection("west");
  static const EAST = const SignDirection("east");

  final String name;

  const SignDirection(this.name);

}

