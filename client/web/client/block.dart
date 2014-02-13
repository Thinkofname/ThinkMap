part of mapViewer;

class Block {

    BlockRegistrationEntry _regBlock;

    bool renderable = true;
    bool transparent = false;

    bool solid = true;
    bool collidable = true;
    bool shade = true;

    int colour = 0xFFFFFF;
    bool forceColour = false;
    String texture;
    bool allowSelf = false;

    bool collidesWith(Box box, int x, int y, int z) {
        if (!collidable) return false;
        return box.checkBox(x.toDouble(), y.toDouble(), z.toDouble(), 1.0, 1.0, 1.0);
    }

    shouldRenderAgainst(Block block) => !block.solid && (!allowSelf || block != this);

    renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int y, int z, Chunk chunk) {
        render(builder, x, y, z, chunk);
    }

    render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
        int r = 255;
        int g = 255;
        int b = 255;
        if (forceColour) {
            r = (colour >> 16) & 0xFF;
            g = (colour >> 8) & 0xFF;
            b = colour & 0xFF;
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y + 1, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y + 1, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y + 1, z, x + 2, y + 2, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y + 1, z, x + 1, y + 2, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.TOP);

            addFaceTop(builder, x, y + 1, z, 1, 1,
                    r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                    texture);
        }


        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y - 1, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z - 1, x + 2, y, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z, x + 2, y, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z, x + 1, y, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.BOTTOM);

            addFaceBottom(builder, x, y, z, 1, 1,
                    r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                    texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y, z, x + 2, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y - 1, z, x + 2, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.LEFT);

            addFaceLeft(builder, x + 1, y, z, 1, 1,
                    r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                    texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x - 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z - 1, x, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z, x, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z, x, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x, y + 1, z + 1) / 4);

            TextureInfo texture = getTexture(BlockFace.RIGHT);

            addFaceRight(builder, x, y, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                    texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z + 1))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z + 1, x + 1, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y, z + 1, x + 2, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z + 1, x + 2, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.FRONT);

            addFaceFront(builder, x, y, z + 1, 1, 1,
                    r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                    texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z - 1))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x, y, z - 1, x + 2, y + 2, z) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z - 1, x + 1, y + 2, z) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y + 1, z) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z - 1, x + 2, y + 1, z) / 4);

            TextureInfo texture = getTexture(BlockFace.BACK);

            addFaceBack(builder, x, y, z, 1, 1,
                    r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                    texture);
        }
    }

    static double _numBlocksRegion(Chunk chunk, Block self, int x1, int y1, int z1, int x2, int y2, int z2) {
        double count = 0.0;
        for (int y = y1; y < y2; y++) {
            if (y < 0 || y > 255) continue;
            for (int x = x1; x < x2; x++) {
                for (int z = z1; z < z2; z++) {
                    double val = pow(0.9,
                        max(chunk.world.getSky((chunk.x * 16) + x, y, (chunk.z * 16) + z), chunk.world.getLight((chunk.x * 16) + x, y, (chunk.z * 16) + z)) + 1);

                    Block block = chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z);
                    if (block.shade && (block.solid || block == self)) {
                        val += (13/17) * (1.0 - val);
                    }
                    count += min(1.0, val);
                }
            }
        }
        return count;
    }

    TextureInfo getTexture(BlockFace face) {
        return blockTextureInfo[texture];
    }
}

typedef TextureInfo TextureGetter(BlockFace);

class BlockFace {

    static const TOP = const BlockFace(0);
    static const BOTTOM = const BlockFace(1);
    static const RIGHT = const BlockFace(2);
    static const LEFT = const BlockFace(3);
    static const BACK = const BlockFace(4);
    static const FRONT = const BlockFace(5);

    final int id;
    const BlockFace(this.id);
}

class TextureInfo {
    int start;
    int end;

    TextureInfo(this.start, this.end);
}