part of mapViewer;

class BlockGrass extends Block {

    Map<BlockFace, String> textures =
    {
        BlockFace.FRONT: "grass_side",
        BlockFace.TOP: "grass_top",
        BlockFace.BOTTOM: "dirt",
        BlockFace.BACK: "grass_side",
        BlockFace.LEFT: "grass_side",
        BlockFace.RIGHT: "grass_side"
    };

    @override
    TextureInfo getTexture(BlockFace face) {
        return blockTextureInfo[textures[face]];
    }

    @override
    render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
        int r = (colour >> 16) & 0xFF;
        int g = (colour >> 8) & 0xFF;
        int b = colour & 0xFF;

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y + 1, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y + 1, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y + 1, z, x + 2, y + 2, z + 2) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y + 1, z, x + 1, y + 2, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.TOP);

            addFaceTop(builder, x, y + 1, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                texture);
        }

        r = 255;
        g = 255;
        b = 255;

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y - 1, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y, z + 1) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y - 1, z - 1, x + 2, y, z + 1) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y - 1, z, x + 2, y, z + 2) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z, x + 1, y, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.BOTTOM);

            addFaceBottom(builder, x, y, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y, z, x + 2, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y - 1, z, x + 2, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.LEFT);

            addFaceLeft(builder, x + 1, y, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x - 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z - 1, x, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z, x, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z, x, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x, y + 1, z + 1) / 4);

            TextureInfo texture = getTexture(BlockFace.RIGHT);

            addFaceRight(builder, x, y, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z + 1))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z + 1, x + 1, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y, z + 1, x + 2, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y - 1, z + 1, x + 2, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.FRONT);

            addFaceFront(builder, x, y, z + 1, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                texture);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z - 1))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x, y, z - 1, x + 2, y + 2, z) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z - 1, x + 1, y + 2, z) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y + 1, z) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x, y - 1, z - 1, x + 2, y + 1, z) / 4);

            TextureInfo texture = getTexture(BlockFace.BACK);

            addFaceBack(builder, x, y, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight,
                texture);
        }
    }
}