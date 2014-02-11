part of mapViewer;

//NOTE: the top* and bottom* params may be incorrect for some of these

addFaceTop(BlockBuilder builder, num x, num y, num z, num w, num h,
            int r, int g, int b, double topLeft, double topRight,
            double bottomLeft, double bottomRight, TextureInfo texture,
            [bool scaleTextures = false]) {

    num tx = 0;
    num ty = 0;
    num tw = 1;
    num th = 1;
    if (!scaleTextures) {
        tx = x % 1;
        ty = z % 1;
        tw = w;
        th = h;
    }

    builder
        ..position(x, y, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + h)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z + h)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + h)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end);
}

addFaceBottom(BlockBuilder builder, num x, num y, num z, num w, num h,
           int r, int g, int b, double topLeft, double topRight,
           double bottomLeft, double bottomRight, TextureInfo texture,
           [bool scaleTextures = false]) {

    num tx = 0;
    num ty = 0;
    num tw = 1;
    num th = 1;
    if (!scaleTextures) {
        tx = x % 1;
        ty = z % 1;
        tw = w;
        th = h;
    }

    builder
        ..position(x, y, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + h)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + h)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z + h)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end);

}

addFaceLeft(BlockBuilder builder, num x, num y, num z, num w, num h,
              int r, int g, int b, double topLeft, double topRight,
              double bottomLeft, double bottomRight, TextureInfo texture,
              [bool scaleTextures = false]) {

    num tx = 0;
    num ty = 0;
    num tw = 1;
    num th = 1;
    if (!scaleTextures) {
        tx = z % 1;
        ty = y % 1;
        tw = w;
        th = h;
    }

    builder
        ..position(x, y, z)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + w)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z + w)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + w)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end);
}

addFaceRight(BlockBuilder builder, num x, num y, num z, num w, num h,
            int r, int g, int b, double topLeft, double topRight,
            double bottomLeft, double bottomRight, TextureInfo texture,
            [bool scaleTextures = false]) {

    num tx = 0;
    num ty = 0;
    num tw = 1;
    num th = 1;
    if (!scaleTextures) {
        tx = z % 1;
        ty = y % 1;
        tw = w;
        th = h;
    }

    builder
        ..position(x, y, z)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + w)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z + w)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y, z + w)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end);
}

addFaceFront(BlockBuilder builder, num x, num y, num z, num w, num h,
             int r, int g, int b, double topLeft, double topRight,
             double bottomLeft, double bottomRight, TextureInfo texture,
             [bool scaleTextures = false]) {

    num tx = 0;
    num ty = 0;
    num tw = 1;
    num th = 1;
    if (!scaleTextures) {
        tx = x % 1;
        ty = y % 1;
        tw = w;
        th = h;
    }

    builder
        ..position(x, y, z)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y + h, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end);
}

addFaceBack(BlockBuilder builder, num x, num y, num z, num w, num h,
             int r, int g, int b, double topLeft, double topRight,
             double bottomLeft, double bottomRight, TextureInfo texture,
             [bool scaleTextures = false]) {

    num tx = 0;
    num ty = 0;
    num tw = 1;
    num th = 1;
    if (!scaleTextures) {
        tx = x % 1;
        ty = y % 1;
        tw = w;
        th = h;
    }

    builder
        ..position(x, y, z)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..tex(tx + tw, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y + h, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..tex(tx, ty)
        ..texId(texture.start, texture.end)
        ..position(x, y + h, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..tex(tx + tw, ty)
        ..texId(texture.start, texture.end)
        ..position(x + w, y, z)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..tex(tx, ty + th)
        ..texId(texture.start, texture.end);
}

addCube(BlockBuilder builder, num x, num y, num z, num w, num h, num d,
            int r, int g, int b, TextureInfo getTexture(BlockFace), [bool scaleTextures = false]) {

    TextureInfo texture = getTexture(BlockFace.TOP);

    addFaceTop(builder, x, y + h, z, w, d,
        r, g, b, 1.0, 1.0, 1.0, 1.0,
        texture, scaleTextures);

    texture = getTexture(BlockFace.BOTTOM);

    addFaceBottom(builder, x, y, z, w, d,
        r, g, b, 1.0, 1.0, 1.0, 1.0,
        texture, scaleTextures);

    texture = getTexture(BlockFace.LEFT);

    addFaceLeft(builder, x + w, y, z, d, h,
        r, g, b, 1.0, 1.0, 1.0, 1.0,
        texture, scaleTextures);

    texture = getTexture(BlockFace.RIGHT);

    addFaceRight(builder, x, y, z, d, h,
        r, g, b, 1.0, 1.0, 1.0, 1.0,
        texture, scaleTextures);

    texture = getTexture(BlockFace.FRONT);

    addFaceFront(builder, x, y, z + d, w, h,
        r, g, b, 1.0, 1.0, 1.0, 1.0,
        texture, scaleTextures);

    texture = getTexture(BlockFace.BACK);

    addFaceBack(builder, x, y, z, w, h,
        r, g, b, 1.0, 1.0, 1.0, 1.0,
        texture, scaleTextures);
}