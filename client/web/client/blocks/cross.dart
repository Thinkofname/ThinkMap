part of mapViewer;

class BlockCross extends Block {

    @override
    renderCanvas(Uint8ClampedList data, int width, int x, int y, int z, int ry, CanvasChunk chunk) {

        int gr = (colour >> 16) & 0xFF;
        int gg = (colour >> 8) & 0xFF;
        int gb = colour & 0xFF;

        // Left side
        int offsetX = x * 16 + z * 16;
        int offsetY = x * 8 + (15-z) * 8 + ((15-y) * 16 + 8);

        bool renderLeft = shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, ry, (chunk.z * 16) + z - 1));
        bool renderRight = shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, ry, (chunk.z * 16) + z));
        if (renderLeft || renderRight) {
            TextureInfo texture = getTexture(BlockFace.LEFT);
            Uint8ClampedList textureData = (renderer as CanvasRenderer).blockRawData[texture.start].data;
            int textureDataWidth = (renderer as CanvasRenderer).blockRawData[texture.start].width;

            TextureInfo textureRight = getTexture(BlockFace.FRONT);
            Uint8ClampedList textureDataRight = (renderer as CanvasRenderer).blockRawData[textureRight.start].data;
//            int textureDataWidthRight = (renderer as CanvasRenderer).blockRawData[textureRight.start].width;  // Assume it has same width textures

            for (int tx = 0; tx < 16; tx++) {
                for (int ty = 0; ty < 16; ty++) {
                    int i = tx + ty * textureDataWidth;
                    i *= 4;

                    int r = (textureData[i] * (gr/255)).toInt();
                    int g = (textureData[i + 1] * (gg/255)).toInt();
                    int b = (textureData[i + 2] * (gb/255)).toInt();
                    int a = textureData[i + 3];

                    if (renderLeft) {
                        double modi = 1.0;

                        putPixel(data, width, (offsetX + tx + 8).toInt(), (offsetY + ty + tx*0.5 - 4).toInt(),
                        (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                    }

                    r = (textureDataRight[i] * (gr/255)).toInt();
                    g = (textureDataRight[i + 1] * (gg/255)).toInt();
                    b = (textureDataRight[i + 2] * (gb/255)).toInt();
                    a = textureDataRight[i + 3];

                    if (renderRight) {
                        double modi = 1.0;

                        putPixel(data, width, (offsetX + 8 + tx).toInt(), (offsetY + ty + 8 - tx*0.5 - 4).toInt(),
                        (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                    }
                }
            }
        }
    }

    @override
    render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
        int r = 255; //(colour >> 16) & 0xFF;
        int g = 255; //(colour >> 8) & 0xFF;
        int b = 255; //colour & 0xFF;
        if (forceColour) {
            r = (colour >> 16) & 0xFF;
            g = (colour >> 8) & 0xFF;
            b = colour & 0xFF;
        }

        TextureInfo texture = getTexture(BlockFace.LEFT);

        builder
            ..position(x, y, z)
            ..colour(r, g, b)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z + 1)
            ..colour(r, g, b)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end);

        builder
            ..position(x + 1, y, z)
            ..colour(r, g, b)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z + 1)
            ..colour(r, g, b)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end);

        builder
            ..position(x, y, z)
            ..colour(r, g, b)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z + 1)
            ..colour(r, g, b)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end);

        builder
            ..position(x + 1, y, z)
            ..colour(r, g, b)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z + 1)
            ..colour(r, g, b)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(r, g, b)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(r, g, b)
            ..tex(0, 0)
            ..texId(texture.start, texture.end);
    }
}