part of mapViewer;

class BlockVines extends Block {

    int dataValue;

    BlockVines(this.dataValue);

    static register() {
        for (int i = 0; i < 16; i++) {
            BlockRegistry.registerBlock("vine_$i", new BlockVines(i)
                ..solid = false
                ..collidable = false
                ..forceColour = true
                ..colour = 0x87BA34
                ..texture = "vine")
                ..legacyId(106)
                ..dataValue(i)
                ..build();
        }
    }

    @override
    shouldRenderAgainst(Block block) => !block.solid;

    @override
    renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int y, int z, Chunk chunk) {
        int r = 255;
        int g = 255;
        int b = 255;
        if (forceColour) {
            r = (colour >> 16) & 0xFF;
            g = (colour >> 8) & 0xFF;
            b = colour & 0xFF;
        }

        double val = pow(0.9,
            max(chunk.world.getSky((chunk.x * 16) + x, y, (chunk.z * 16) + z), chunk.world.getLight((chunk.x * 16) + x, y, (chunk.z * 16) + z)) + 1);
        double topRight = 1.0 - val;
        double topLeft = topRight;
        double bottomLeft = topRight;
        double bottomRight = topRight;

        if (dataValue == 0 || !shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y + 1, (chunk.z * 16) + z))) {

            TextureInfo texture = getTexture(BlockFace.BOTTOM);

            addFaceBottom(fBulider, x, y + 0.95, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight, texture);
        }

        if (dataValue & 2 == 2) {

            TextureInfo texture = getTexture(BlockFace.LEFT);

            addFaceLeft(fBulider, x + 0.05, y, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight, texture);
        }

        if (dataValue & 8 == 8) {

            TextureInfo texture = getTexture(BlockFace.RIGHT);

            addFaceRight(fBulider, x + 0.95, y, z, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight, texture);
        }

        if (dataValue & 4 == 4) {

            TextureInfo texture = getTexture(BlockFace.FRONT);

            addFaceFront(fBulider, x, y, z + 0.05, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight, texture);
        }

        if (dataValue & 1 == 1) {

            TextureInfo texture = getTexture(BlockFace.BACK);

            addFaceBack(fBulider, x, y, z + 0.95, 1, 1,
                r, g, b, topLeft, topRight, bottomLeft, bottomRight, texture);
        }
    }
}