part of map_viewer;

class BlockCross extends Block {

  @override
  void render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {

    if (model == null) {
      int r = 255;
      int g = 255;
      int b = 255;
      if (forceColour) {
        r = (colour >> 16) & 0xFF;
        g = (colour >> 8) & 0xFF;
        b = colour & 0xFF;
      }
      model = new Model();
      model.faces.add(new ModelFace(BlockFace.FRONT)
        ..texture = texture
        ..vertices.where((e) => e.x == 1).forEach((e) => e.z = 1));
      model.faces.add(new ModelFace(BlockFace.BACK)
        ..texture = texture
        ..vertices.where((e) => e.x == 1).forEach((e) => e.z = 1));
      model.faces.add(new ModelFace(BlockFace.FRONT)
        ..texture = texture
        ..vertices.where((e) => e.x == 0).forEach((e) => e.z = 1));
      model.faces.add(new ModelFace(BlockFace.BACK)
        ..texture = texture
        ..vertices.where((e) => e.x == 0).forEach((e) => e.z = 1));
    }
    super.render(builder, x, y, z, chunk);
  }
}
