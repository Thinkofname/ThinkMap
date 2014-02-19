part of map_viewer;

class BlockWater extends Block {

    @override
    shouldRenderAgainst(Block block) => !block.solid && block != Blocks.WATER && block != Blocks.FLOWING_WATER;

}

class BlockSidedTextures extends Block {


    Map<BlockFace, String> textures;

    @override
    TextureInfo getTexture(BlockFace face) {
        return blockTextureInfo[textures[face]];
    }
}