part of mapViewer;

class BlockCross extends Block {

    BlockCross._internal(id, name, {texture: "sapling_oak", forceColour : false, colour: 0x49CC25}) :
        super._internal(id, name, colour, texture: texture, solid: false, forceColour: forceColour);


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