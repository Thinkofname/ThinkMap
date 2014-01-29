part of mapViewer;

class BlockWater extends Block {

    BlockWater._internal(id, name, texture) : super._internal(id, name, 0x2838E2, texture: texture, solid: false, transparent: true);

    @override
    shouldRenderAgainst(Block block) => !block.solid && block != Block.WATER && block != Block.FLOWING_WATER;

}

class BlockSidedTextures extends Block {

    BlockSidedTextures._internal(int _legacyId, String _name, int _colour,
                                 {String texture: "stone", bool solid: true, bool transparent : false}):
            super._internal(_legacyId, _name, _colour, texture: texture, solid: solid, transparent: transparent);


    Map<BlockFace, String> textures;

    @override
    TextureInfo getTexture(BlockFace face) {
        return blockTextureInfo[textures[face]];
    }
}

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