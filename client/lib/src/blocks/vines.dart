part of map_viewer;

class BlockVines extends Block {

  BlockVines();

  static Model top = new Model();

  static void register() {
    top.faces.add(new ModelFace(BlockFace.BOTTOM)
      ..moveY(15)
      ..r = 0x87
      ..g = 0xBA
      ..b = 0x34
      ..texture = "vine");
    for (int i = 0; i < 16; i++) {
      Model model = new Model();
      if (i == 0) {
        model.faces.addAll(top.faces);
      }
      if (i & 2 == 2) {
        model.faces.add(new ModelFace(BlockFace.LEFT)..moveX(1)
          ..r = 0x87
          ..g = 0xBA
          ..b = 0x34
          ..texture = "vine");
      }
      if (i & 8 == 8) {
        model.faces.add(new ModelFace(BlockFace.RIGHT)..moveX(15)
          ..r = 0x87
          ..g = 0xBA
          ..b = 0x34
          ..texture = "vine");
      }
      if (i & 4 == 4) {
        model.faces.add(new ModelFace(BlockFace.FRONT)..moveZ(1)
          ..r = 0x87
          ..g = 0xBA
          ..b = 0x34
          ..texture = "vine");
      }
      if (i & 1 == 1) {
        model.faces.add(new ModelFace(BlockFace.BACK)..moveZ(15)
          ..r = 0x87
          ..g = 0xBA
          ..b = 0x34
          ..texture = "vine");
      }
      BlockRegistry.registerBlock("vine_$i", new BlockVines()
          ..solid = false
          ..collidable = false
          ..forceColour = true
          ..colour = 0x87BA34
          ..texture = "vine"
          ..model = model)
          ..legacyId(106)
          ..dataValue(i)
          ..build();
    }
  }

  @override
  bool shouldRenderAgainst(Block block) => !block.solid;

  /**
   * Renders the block at the coordinates [x], [y] and [z] relative to the
   * [chunk]
   */
  void render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
    model.render(builder, x, y, z, chunk);
    if (!shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y + 1, (chunk.z * 16) + z))) {
      top.render(builder, x, y, z, chunk);
    }
  }

//  @override
//  void renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int
//      y, int z, Chunk chunk) {
//    int r = 255;
//    int g = 255;
//    int b = 255;
//    if (forceColour) {
//      r = (colour >> 16) & 0xFF;
//      g = (colour >> 8) & 0xFF;
//      b = colour & 0xFF;
//    }
//
//    LightInfo light = LightInfo.getLight(chunk.getLight(x, y, z), chunk.getSky(
//        x, y, z));
//
//    if (dataValue == 0 || !shouldRenderAgainst(chunk.world.getBlock((chunk.x *
//        16) + x, y + 1, (chunk.z * 16) + z))) {
//
//      TextureInfo texture = getTexture(BlockFace.BOTTOM);
//
//      addFaceBottom(fBulider, x, y + 0.95, z, 1, 1, r, g, b, light, light,
//          light, light, texture);
//    }
//
//    if (dataValue & 2 == 2) {
//
//      TextureInfo texture = getTexture(BlockFace.LEFT);
//
//      addFaceLeft(fBulider, x + 0.05, y, z, 1, 1, r, g, b, light, light, light,
//          light, texture);
//    }
//
//    if (dataValue & 8 == 8) {
//
//      TextureInfo texture = getTexture(BlockFace.RIGHT);
//
//      addFaceRight(fBulider, x + 0.95, y, z, 1, 1, r, g, b, light, light, light,
//          light, texture);
//    }
//
//    if (dataValue & 4 == 4) {
//
//      TextureInfo texture = getTexture(BlockFace.FRONT);
//
//      addFaceFront(fBulider, x, y, z + 0.05, 1, 1, r, g, b, light, light, light,
//          light, texture);
//    }
//
//    if (dataValue & 1 == 1) {
//
//      TextureInfo texture = getTexture(BlockFace.BACK);
//
//      addFaceBack(fBulider, x, y, z + 0.95, 1, 1, r, g, b, light, light, light,
//          light, texture);
//    }
//  }
}
