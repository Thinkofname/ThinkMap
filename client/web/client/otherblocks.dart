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

class BlockFlat extends Block {

    BlockFlat._internal(id, name, texture) : super._internal(id, name, 0x000000, solid: false, texture: texture);

    @override
    renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int y, int z, Chunk chunk) {
        addPlane(fBulider, 255, 255, 255, x, y + 1/32, z, 1, 1, blockTextureInfo[texture]);
    }

    @override
    bool collidesWith(Box box, int x, int y, int z) {
        if (!collidable) return false;
        return box.checkBox(x.toDouble(), y.toDouble(), z.toDouble(), 1.0, 1/32, 1.0);
    }
}

class BlockBed extends Block {

    BlockBed._internal(id, name) : super._internal(id, name, 0x000000, solid: false);


    Map<BlockFace, String> textures = {
        BlockFace.TOP: "bed_head_top",
        BlockFace.FRONT: "bed_head_side",
        BlockFace.LEFT: "bed_head_side",
        BlockFace.RIGHT: "bed_head_end",
        BlockFace.BACK: "bed_head_side"
    };

    @override
    TextureInfo getTexture(BlockFace face) {
        return blockTextureInfo[textures[face]];
    }

    @override
    renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int y, int z, Chunk chunk) {
        addCube(fBulider, 255, 255, 255, x, y, z, 1, 6 / 16, 1, true, false, true, true, true, true, getTexture);
    }

    @override
    bool collidesWith(Box box, int x, int y, int z) {
        if (!collidable) return false;
        return box.checkBox(x.toDouble(), y.toDouble(), z.toDouble(), 1.0, 6/16, 1.0);
    }
}