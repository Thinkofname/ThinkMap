part of mapViewer;

class BlockSapling extends Block {

    BlockSapling._internal(id, name) : super._internal(id, name, 0x49CC25, texture: "sapling_oak", solid: false);

    @override
    render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {

        TextureInfo texture = getTexture(BlockFace.LEFT);

        builder
            ..position(x, y, z)
            ..colour(255, 255, 255)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end);

        builder
            ..position(x + 1, y, z)
            ..colour(255, 255, 255)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end);

        builder
            ..position(x, y, z)
            ..colour(255, 255, 255)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end);

        builder
            ..position(x + 1, y, z)
            ..colour(255, 255, 255)
            ..tex(0, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x, y + 1, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 0)
            ..texId(texture.start, texture.end)
            ..position(x, y, z + 1)
            ..colour(255, 255, 255)
            ..tex(1, 1)
            ..texId(texture.start, texture.end)
            ..position(x + 1, y + 1, z)
            ..colour(255, 255, 255)
            ..tex(0, 0)
            ..texId(texture.start, texture.end);
    }
}