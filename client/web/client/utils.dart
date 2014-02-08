part of mapViewer;

putPixel(Uint8ClampedList data, int width, int x, int y, int r, int g, int b, int a) {
    int i = (x + y * width) * 4;

    if (a == 0) return;

    if (data[i + 3] == 0) {
        data[i] = r;
        data[i + 1] = g;
        data[i + 2] = b;
        data[i + 3] = 255;
    } else {
        data[i] = (data[i] * ((255-a)/255) + r * (a/255)).toInt();
        data[i + 1] = (data[i + 1] * ((255-a)/255) + g * (a/255)).toInt();
        data[i + 2] = (data[i + 2] * ((255-a)/255) + b * (a/255)).toInt();
        data[i + 3] = 255;
    }
}

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