part of map_viewer;

class BlockWallSign extends Block {

    SignDirection direction;

    BlockWallSign(this.direction);


    static register() {
        BlockRegistry.registerBlock("wall_sign_north", new BlockWallSign(SignDirection.NORTH)
            ..collidable = false
            ..solid = false
            ..texture = "planks_oak")
            ..legacyId(68)
            ..dataValue(2)
            ..build();
        BlockRegistry.registerBlock("wall_sign_south", new BlockWallSign(SignDirection.SOUTH)
            ..collidable = false
            ..solid = false
            ..texture = "planks_oak")
            ..legacyId(68)
            ..dataValue(3)
            ..build();
        BlockRegistry.registerBlock("wall_sign_west", new BlockWallSign(SignDirection.WEST)
            ..collidable = false
            ..solid = false
            ..texture = "planks_oak")
            ..legacyId(68)
            ..dataValue(4)
            ..build();
        BlockRegistry.registerBlock("wall_sign_east", new BlockWallSign(SignDirection.EAST)
            ..collidable = false
            ..solid = false
            ..texture = "planks_oak")
            ..legacyId(68)
            ..dataValue(5)
            ..build();
    }

    @override
    renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int y, int z, Chunk chunk) {
        LightInfo light = LightInfo.getLight(chunk.getLight(x, y, z), chunk.getSky(x, y, z));
        switch (direction) {
            case SignDirection.NORTH:
                addCube(fBulider, x, y + (4.0/16.0), z + (15.0/16.0), 1, 8.0/16.0, 1.0/16.0,
                    255, 255, 255, light, getTexture, true);
                break;
            case SignDirection.SOUTH:
                addCube(fBulider, x, y + (4.0/16.0), z, 1, 8.0/16.0, 1.0/16.0,
                    255, 255, 255, light, getTexture, true);
                break;
            case SignDirection.EAST:
                addCube(fBulider, x, y + (4.0/16.0), z, 1.0/16.0, 8.0/16.0, 1,
                    255, 255, 255, light, getTexture, true);
                break;
            case SignDirection.WEST:
                addCube(fBulider, x + (15.0/16.0), y + (4.0/16.0), z, 1.0/16.0, 8.0/16.0, 1,
                    255, 255, 255, light, getTexture, true);
                break;
        }
    }
}

class SignDirection {

    static const NORTH = const SignDirection("north");
    static const SOUTH = const SignDirection("south");
    static const WEST = const SignDirection("west");
    static const EAST = const SignDirection("east");

    final String name;

    const SignDirection(this.name);

}

