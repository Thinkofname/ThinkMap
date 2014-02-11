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
    renderCanvas(Uint8ClampedList data, int width, int x, int y, int z, int ry, CanvasChunk chunk) {
        int gr = (colour >> 16) & 0xFF;
        int gg = (colour >> 8) & 0xFF;
        int gb = colour & 0xFF;

        if (!forceColour) {
            gr = 255;
            gg = 255;
            gb = 255;
        }

        // Left side
        int offsetX = x * 16 + z * 16;
        int offsetY = x * 8 + (15-z) * 8 + ((15-y) * 16 + 8);

        bool renderLeft = shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, ry, (chunk.z * 16) + z - 1));
        bool renderRight = shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, ry, (chunk.z * 16) + z));
        if (renderLeft || renderRight) {
            TextureInfo texture = getTexture(BlockFace.LEFT);
            Uint8ClampedList textureData = (renderer as CanvasRenderer).blockRawData[texture.start].data;
            int textureDataWidth = (renderer as CanvasRenderer).blockRawData[texture.start].width;

            double bottomLeft = renderLeft ? 1.0 - (Block._numBlocksRegion(chunk, this, x, ry, z - 1, x + 2, ry + 2, z) / 4) : 0.0;
            double bottomRight = renderLeft ? 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, ry, z - 1, x + 1, ry + 2, z) / 4) : 0.0;
            double topRight = renderLeft ? 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, ry - 1, z - 1, x + 1, ry + 1, z) / 4) : 0.0;
            double topLeft = renderLeft ? 1.0 - (Block._numBlocksRegion(chunk, this, x, ry - 1, z - 1, x + 2, ry + 1, z) / 4) : 0.0;

            TextureInfo textureRight = getTexture(BlockFace.FRONT);
            Uint8ClampedList textureDataRight = (renderer as CanvasRenderer).blockRawData[textureRight.start].data;

            double bottomLeftRight = renderRight ? 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, ry, z, x + 2, ry + 2, z + 2) / 4) : 0.0;
            double bottomRightRight = renderRight ? 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, ry, z - 1, x + 2, ry + 2, z + 1) / 4) : 0.0;
            double topRightRight = renderRight ? 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, ry - 1, z - 1, x + 2, ry + 1, z + 1) / 4) : 0.0;
            double topLeftRight = renderRight ? 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, ry - 1, z, x + 2, ry + 1, z + 2) / 4) : 0.0;

            for (int tx = 0; tx < 16; tx++) {
                for (int ty = 0; ty < 16; ty++) {
                    int i = tx + ty * textureDataWidth;
                    i *= 4;

                    int r = (textureData[i] * (gr/255)).toInt();
                    int g = (textureData[i + 1] * (gg/255)).toInt();
                    int b = (textureData[i + 2] * (gb/255)).toInt();
                    int a = textureData[i + 3];

                    if (renderLeft) {
                        double modi = ((topLeft * (tx/16) + topRight * ((15-tx)/16))*(ty/16))
                        + ((bottomLeft * (tx/16) + bottomRight * ((15-tx)/16))*((15-ty)/16));

                        putPixel(data, width, (offsetX + tx).toInt(), (offsetY + ty + tx*0.5).toInt(),
                        (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                    }

                    r = (textureDataRight[i] * (gr/255)).toInt();
                    g = (textureDataRight[i + 1] * (gg/255)).toInt();
                    b = (textureDataRight[i + 2] * (gb/255)).toInt();
                    a = textureDataRight[i + 3];

                    if (renderRight) {
                        double modi = ((topLeftRight * (tx/16) + topRightRight * ((15-tx)/16))*(ty/16))
                        + ((bottomLeftRight * (tx/16) + bottomRightRight * ((15-tx)/16))*((15-ty)/16));

                        putPixel(data, width, (offsetX + 16 + tx).toInt(), (offsetY + ty + 8 - tx*0.5).toInt(),
                        (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                    }
                }
            }
        }
        //Top side
        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, ry + 1, (chunk.z * 16) + z))) {

            int gr = (colour >> 16) & 0xFF;
            int gg = (colour >> 8) & 0xFF;
            int gb = colour & 0xFF;

            TextureInfo texture = getTexture(BlockFace.TOP);
            Uint8ClampedList textureData = (renderer as CanvasRenderer).blockRawData[texture.start].data;
            int textureDataWidth = (renderer as CanvasRenderer).blockRawData[texture.start].width;

            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, ry + 1, z - 1, x + 1, ry + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, ry + 1, z - 1, x + 2, ry + 2, z + 1) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, ry + 1, z, x + 2, ry + 2, z + 2) / 4);
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, ry + 1, z, x + 1, ry + 2, z + 2) / 4);

            //Overdraw to fix white lines
            for (int ttx = -1; ttx < 17; ttx++) {
                for (int tty = -1; tty < 16; tty++) {
                    int tx = ttx.clamp(0, 15);
                    int ty = tty.clamp(0, 15);
                    int i = tx + ty * textureDataWidth;
                    i *= 4;
                    int r = (textureData[i] * (gr/255)).toInt();
                    int g = (textureData[i + 1] * (gg/255)).toInt();
                    int b = (textureData[i + 2] * (gb/255)).toInt();
                    int a = textureData[i + 3];

                    double modi = ((topLeft * (tx/16) + topRight * ((15-tx)/16))*(ty/16))
                    + ((bottomLeft * (tx/16) + bottomRight * ((15-tx)/16))*((15-ty)/16));

                    putPixel(data, width, (offsetX + 1.5 + ttx + tty).toInt(), (offsetY + ttx * 0.5 - 0.75 - tty * 0.5).toInt(),
                    (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                }
            }
        }
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

            builder
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end);
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

            builder
                ..position(x, y, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y, z, x + 2, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x + 1, y - 1, z, x + 2, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.LEFT);

            builder
                ..position(x + 1, y, z)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x - 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z - 1, x, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z, x, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z, x, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x, y + 1, z + 1) / 4);

            TextureInfo texture = getTexture(BlockFace.RIGHT);

            builder
                ..position(x, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z + 1))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z + 1, x + 1, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y, z + 1, x + 2, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x, y - 1, z + 1, x + 2, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.FRONT);

            builder
                ..position(x, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z - 1))) {
            double topRight = 1.0 - (Block._numBlocksRegion(chunk, this, x, y, z - 1, x + 2, y + 2, z) / 4);
            double topLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y, z - 1, x + 1, y + 2, z) / 4);
            double bottomLeft = 1.0 - (Block._numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y + 1, z) / 4);
            double bottomRight = 1.0 - (Block._numBlocksRegion(chunk, this, x, y - 1, z - 1, x + 2, y + 1, z) / 4);

            TextureInfo texture = getTexture(BlockFace.BACK);

            builder
                ..position(x, y, z)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }
    }
}