part of map_viewer;

/**
 * A block. Cube thingy (most of the time)
 */
class Block {

  /// The registration entry for this block
  BlockRegistrationEntry _regBlock;

  /// Whether the block can actually be rendered
  bool renderable = true;
  /// Whether the block needs to be rendered with other transparent objects
  bool transparent = false;

  /// Whether the block is solid
  bool solid = true;
  /// Whether the block has collisions
  bool collidable = true;
  /// Whether the block gives a shadow (AO)
  bool shade = true;

  /// The colour (tint) of the block
  int colour = 0xFFFFFF;
  /// Whether to force tinting for the block
  bool forceColour = false;
  /// The name of the texture for this block
  String texture;
  /// Whether the block can render against itself
  bool allowSelf = false;

  /**
   * Returns whether the block at the coordinates [x], [y] and [z]
   * collides with the [box]
   */
  bool collidesWith(Box box, int x, int y, int z) {
    if (!collidable) return false;
    return box.checkBox(x.toDouble(), y.toDouble(), z.toDouble(), 1.0, 1.0, 1.0
        );
  }

  /**
   * Returns whether this should render its side against the [block]
   */
  bool shouldRenderAgainst(Block block) => !block.solid && (!allowSelf || block !=
      this);

  /**
   * Renders the block at the coordinates [x], [y] and [z] relative to the
   * [chunk]
   */
  void render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
    int r = 255;
    int g = 255;
    int b = 255;
    if (forceColour) {
      r = (colour >> 16) & 0xFF;
      g = (colour >> 8) & 0xFF;
      b = colour & 0xFF;
    }

    if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y + 1,
        (chunk.z * 16) + z))) {
      TextureInfo texture = getTexture(BlockFace.TOP);

      int light = chunk.world.getLight((chunk.x * 16) + x, y + 1, (chunk.z * 16) + z);
      int sky = chunk.world.getSky((chunk.x * 16) + x, y + 1, (chunk.z * 16) + z);

      addFaceTop(builder, x, y + 1, z, 1, 1, r, g, b,
        _blockLightingRegion(chunk, this, x, y + 1, z - 1, x + 2, y + 2, z + 1, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1, light, sky),
        _blockLightingRegion(chunk, this, x, y + 1, z, x + 2, y + 2, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y + 1, z, x + 1, y + 2, z + 2, light, sky),
        texture);
    }


    if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y - 1,
        (chunk.z * 16) + z))) {
      TextureInfo texture = getTexture(BlockFace.BOTTOM);

      int light = chunk.world.getLight((chunk.x * 16) + x, y - 1, (chunk.z * 16) + z);
      int sky = chunk.world.getSky((chunk.x * 16) + x, y - 1, (chunk.z * 16) + z);

      addFaceBottom(builder, x, y, z, 1, 1, r, g, b,
        _blockLightingRegion(chunk, this, x, y - 1, z - 1, x + 2, y, z + 1, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y, z + 1, light, sky),
        _blockLightingRegion(chunk, this, x, y - 1, z, x + 2, y, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y - 1, z, x + 1, y, z + 2, light, sky),
        texture);
    }

    if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, y,
        (chunk.z * 16) + z))) {
      TextureInfo texture = getTexture(BlockFace.LEFT);

      int light = chunk.world.getLight((chunk.x * 16) + x + 1, y, (chunk.z * 16) + z);
      int sky = chunk.world.getSky((chunk.x * 16) + x + 1, y, (chunk.z * 16) + z);

      addFaceLeft(builder, x + 1, y, z, 1, 1, r, g, b,
        _blockLightingRegion(chunk, this, x + 1, y, z - 1, x + 2, y + 2, z + 1, light, sky),
        _blockLightingRegion(chunk, this, x + 1, y, z, x + 2, y + 2, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1, light, sky),
        _blockLightingRegion(chunk, this, x + 1, y - 1, z, x + 2, y + 1, z + 2, light, sky),
        texture);
    }

    if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x - 1, y,
        (chunk.z * 16) + z))) {
      TextureInfo texture = getTexture(BlockFace.RIGHT);

      int light = chunk.world.getLight((chunk.x * 16) + x - 1, y, (chunk.z * 16) + z);
      int sky = chunk.world.getSky((chunk.x * 16) + x - 1, y, (chunk.z * 16) + z);

      addFaceRight(builder, x, y, z, 1, 1, r, g, b,
        _blockLightingRegion(chunk, this, x - 1, y, z, x, y + 2, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y, z - 1, x, y + 2, z + 1, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y - 1, z, x, y + 1, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x, y + 1, z + 1, light, sky),
        texture);
    }

    if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z
        * 16) + z + 1))) {
      TextureInfo texture = getTexture(BlockFace.FRONT);

      int light = chunk.world.getLight((chunk.x * 16) + x, y, (chunk.z * 16) + z + 1);
      int sky = chunk.world.getSky((chunk.x * 16) + x, y, (chunk.z * 16) + z + 1);

      addFaceFront(builder, x, y, z + 1, 1, 1, r, g, b,
        _blockLightingRegion(chunk, this, x, y, z + 1, x + 2, y + 2, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y, z + 1, x + 1, y + 2, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x, y - 1, z + 1, x + 2, y + 1, z + 2, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2, light, sky),
        texture);
    }

    if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z
        * 16) + z - 1))) {
      TextureInfo texture = getTexture(BlockFace.BACK);

      int light = chunk.world.getLight((chunk.x * 16) + x, y, (chunk.z * 16) + z - 1);
      int sky = chunk.world.getSky((chunk.x * 16) + x, y, (chunk.z * 16) + z - 1);

      addFaceBack(builder, x, y, z, 1, 1, r, g, b,
        _blockLightingRegion(chunk, this, x - 1, y, z - 1, x + 1, y + 2, z, light, sky),
        _blockLightingRegion(chunk, this, x, y, z - 1, x + 2, y + 2, z, light, sky),
        _blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y + 1, z, light, sky),
        _blockLightingRegion(chunk, this, x, y - 1, z - 1, x + 2, y + 1, z, light, sky),
        texture);
    }
  }

  /**
   * Returns the texture for the [face]
   */
  TextureInfo getTexture(BlockFace face) {
    return blockTextureInfo[texture];
  }
}

/**
 * Calculate the average lighting in the region defined by the
 * two sets of coordinates
 */
LightInfo _blockLightingRegion(Chunk chunk, Block self, int x1, int y1,
    int z1, int x2, int y2, int z2, [int faceLight = 0, int faceSkyLight = 0]) {
  int light = 0;
  int sky = 0;
  int count = 0;
  int valSolid = (15 * pow(0.85, faceLight + 1)).toInt();
  int valSkySolid = (15 * pow(0.85, faceSkyLight + 1)).toInt();
  for (int y = y1; y < y2; y++) {
    if (y < 0 || y > 255) continue;
    for (int x = x1; x < x2; x++) {
      for (int z = z1; z < z2; z++) {
        int px = (chunk.x * 16) + x;
        int pz = (chunk.z * 16) + z;
        if (!chunk.world.isLoaded(px, y, pz)) continue;
        count++;
        int valSky = 6;
        int val = 6;

        Block block = chunk.world.getBlock(px, y, pz);
        if (block.shade && (block.solid || block == self)) {
          val -= valSolid;
          valSky -= valSkySolid;
        } else {
          valSky = chunk.world.getSky(px, y, pz);
          val = chunk.world.getLight(px, y, pz);
        }
        light += val;
        sky += valSky;
      }
    }
  }
  if (count == 0) return fullBright;
  return LightInfo.getLight(light ~/ count, sky ~/ count);
}

/**
 * Used to store light information
 */
class LightInfo {
  /// Block emitted light level
  final int light;
  /// Sky light level
  final int sky;

  /// Creates a new LightInfo
  LightInfo(this.light, this.sky);

  //TODO: Remove - Left over from old cache method
  static LightInfo getLight(int light, int sky) {
    return new LightInfo(light, sky);
  }

}

/**
 * A enum of block faces
 */
class BlockFace {
  static const TOP = const BlockFace(0);
  static const BOTTOM = const BlockFace(1);
  static const RIGHT = const BlockFace(2);
  static const LEFT = const BlockFace(3);
  static const BACK = const BlockFace(4);
  static const FRONT = const BlockFace(5);

  /// Integer version of the face
  final int id;
  const BlockFace(this.id);
}

/**
 * Stores the start and end index of a texture in the
 * texture map
 */
class TextureInfo {
  /// Start index
  int start;
  /// End index
  int end;

  TextureInfo(this.start, this.end);
}
