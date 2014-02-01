part of mapViewer;


// Because dart is broken
//int leftShift(int v, int x) => v < 0 ? -((-v)<<x) : v << x;


addPlane(BlockBuilder builder, int r, int g, int b, num x, num y, num z, num w, num d, TextureInfo texture) {

    num fx = x % 1;
    num fz = z % 1;
    builder
        ..position(x, y, z)
        ..colour(r, g, b)
        ..tex(fx + w, fz)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour(r, g, b)
        ..tex(fx, fz)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + d)
        ..colour(r, g, b)
        ..tex(fx + w, fz + d)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour(r, g, b)
        ..tex(fx, fz)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z + d)
        ..colour(r, g, b)
        ..tex(fx, fz + d)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + d)
        ..colour(r, g, b)
        ..tex(fx + w, fz + d)
        ..texId(texture.start, texture.end);
}

addCube(BlockBuilder builder,
                int r, int g, int b, num x, num y, num z, num w, num h, num d,
                bool top, bool bottom, bool left, bool right, bool back, bool front, TextureGetter getTexture) {

    num fx = x % 1;
    num fy = y % 1;
    num fz = z % 1;

    if (top) {

        TextureInfo texture = getTexture(BlockFace.TOP);

        builder
            ..position(x, y + h, z)
            ..colour(r, g, b)
            ..tex(fx + w, fz)
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z)
            ..colour(r, g, b)
            ..tex(fx, fz)
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z + d)
            ..colour(r, g, b)
            ..tex(fx + w, fz + d)
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z)
            ..colour(r, g, b)
            ..tex(fx, fz)
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z + d)
            ..colour(r, g, b)
            ..tex(fx, fz + d)
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z + d)
            ..colour(r, g, b)
            ..tex(fx + w, fz + d)
            ..texId(texture.start, texture.end);
    }

    //TODO: Bottom side

    if (left) {

        TextureInfo texture = getTexture(BlockFace.LEFT);

        builder
            ..position(x + w, y, z)
            ..colour(r, g, b)
            ..tex(1 - (fz + d), 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x + w, y, z + d)
            ..colour(r, g, b)
            ..tex(1 - fz, 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z)
            ..colour(r, g, b)
            ..tex(1 - (fz + d), 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z + d)
            ..colour(r, g, b)
            ..tex(1 - fz, 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z)
            ..colour(r, g, b)
            ..tex(1 - (fz + d), 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y, z + d)
            ..colour(r, g, b)
            ..tex(1 - fz, 1 - fy)
            ..texId(texture.start, texture.end);
    }

    if (right) {

        TextureInfo texture = getTexture(BlockFace.RIGHT);

        builder
            ..position(x, y, z)
            ..colour(r, g, b)
            ..tex(1 - fz, 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z)
            ..colour(r, g, b)
            ..tex(1 - fz, 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x, y, z + d)
            ..colour(r, g, b)
            ..tex(1 - (fz + d), 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z)
            ..colour(r, g, b)
            ..tex(1 - fz, 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z + d)
            ..colour(r, g, b)
            ..tex(1 - (fz + d), 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x, y, z + d)
            ..colour(r, g, b)
            ..tex(1 - (fz + d), 1 - fy)
            ..texId(texture.start, texture.end);
    }

    if (front) {

        TextureInfo texture = getTexture(BlockFace.FRONT);

        builder
            ..position(x, y, z + d)
            ..colour(r, g, b)
            ..tex(1 - (fx + w), 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z + d)
            ..colour(r, g, b)
            ..tex(1 - (fx + w), 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y, z + d)
            ..colour(r, g, b)
            ..tex(1 - fx, 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z + d)
            ..colour(r, g, b)
            ..tex(1 - (fx + w), 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z + d)
            ..colour(r, g, b)
            ..tex(1 - fx, 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y, z + d)
            ..colour(r, g, b)
            ..tex(1 - fx, 1 - fy)
            ..texId(texture.start, texture.end);
    }

    if (back) {

        TextureInfo texture = getTexture(BlockFace.BACK);

        builder
            ..position(x, y, z)
            ..colour(r, g, b)
            ..tex(1 - (fx + w), 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x + w, y, z)
            ..colour(r, g, b)
            ..tex(1 - fx, 1 - fy)
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z)
            ..colour(r, g, b)
            ..tex(1 - (fx + w), 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y + h, z)
            ..colour(r, g, b)
            ..tex(1 - fx, 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x, y + h, z)
            ..colour(r, g, b)
            ..tex(1 - (fx + w), 1 - (fy + h))
            ..texId(texture.start, texture.end)
            ..position(x + w, y, z)
            ..colour(r, g, b)
            ..tex(1 - fx, 1 - fy)
            ..texId(texture.start, texture.end);
    }
}