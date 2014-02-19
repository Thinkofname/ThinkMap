part of map_viewer;


//NOTE: the top* and bottom* params may be incorrect for some of these

addFaceTop(BlockBuilder builder, num x, num y, num z, num w, num h, int r, int
    g, int b, LightInfo topLeft, LightInfo topRight, LightInfo bottomLeft, LightInfo
    bottomRight, TextureInfo texture, [bool scaleTextures = false]) {

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
      //
      ..position(x, y, z)
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x, y, z + h)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x + w, y, z + h)
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky)
      //
      ..position(x, y, z + h)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky);
}

addFaceBottom(BlockBuilder builder, num x, num y, num z, num w, num h, int
    r, int g, int b, LightInfo topLeft, LightInfo topRight, LightInfo
    bottomLeft, LightInfo bottomRight, TextureInfo texture, [bool scaleTextures =
    false]) {

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
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x, y, z + h)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x, y, z + h)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky)
      //
      ..position(x + w, y, z + h)
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky);

}

addFaceLeft(BlockBuilder builder, num x, num y, num z, num w, num h, int r, int
    g, int b, LightInfo topLeft, LightInfo topRight, LightInfo bottomLeft, LightInfo
    bottomRight, TextureInfo texture, [bool scaleTextures = false]) {

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
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky)
      //
      ..position(x, y, z + w)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x, y + h, z + w)
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x, y, z + w)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky);
}

addFaceRight(BlockBuilder builder, num x, num y, num z, num w, num h, int r, int
    g, int b, LightInfo topLeft, LightInfo topRight, LightInfo bottomLeft, LightInfo
    bottomRight, TextureInfo texture, [bool scaleTextures = false]) {

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
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x, y, z + w)
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x, y + h, z + w)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x, y, z + w)
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky);
}

addFaceFront(BlockBuilder builder, num x, num y, num z, num w, num h, int r, int
    g, int b, LightInfo topLeft, LightInfo topRight, LightInfo bottomLeft, LightInfo
    bottomRight, TextureInfo texture, [bool scaleTextures = false]) {

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
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x + w, y + h, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky);
}

addFaceBack(BlockBuilder builder, num x, num y, num z, num w, num h, int r, int
    g, int b, LightInfo topLeft, LightInfo topRight, LightInfo bottomLeft, LightInfo
    bottomRight, TextureInfo texture, [bool scaleTextures = false]) {

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
      ..colour(r, g, b)
      ..tex(tx + tw, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomLeft.light, bottomLeft.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x + w, y + h, z)
      ..colour(r, g, b)
      ..tex(tx, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topRight.light, topRight.sky)
      //
      ..position(x, y + h, z)
      ..colour(r, g, b)
      ..tex(tx + tw, ty)
      ..texId(texture.start, texture.end)
      ..lighting(topLeft.light, topLeft.sky)
      //
      ..position(x + w, y, z)
      ..colour(r, g, b)
      ..tex(tx, ty + th)
      ..texId(texture.start, texture.end)
      ..lighting(bottomRight.light, bottomRight.sky);
}

final LightInfo fullBright = LightInfo.getLight(15, 15);

addCube(BlockBuilder builder, num x, num y, num z, num w, num h, num d, int
    r, int g, int b, LightInfo light, TextureInfo getTexture(BlockFace), [bool
    scaleTextures = false]) {

  TextureInfo texture = getTexture(BlockFace.TOP);

  addFaceTop(builder, x, y + h, z, w, d, r, g, b, light, light, light, light,
      texture, scaleTextures);

  texture = getTexture(BlockFace.BOTTOM);

  addFaceBottom(builder, x, y, z, w, d, r, g, b, light, light, light, light,
      texture, scaleTextures);

  texture = getTexture(BlockFace.LEFT);

  addFaceLeft(builder, x + w, y, z, d, h, r, g, b, light, light, light, light,
      texture, scaleTextures);

  texture = getTexture(BlockFace.RIGHT);

  addFaceRight(builder, x, y, z, d, h, r, g, b, light, light, light, light,
      texture, scaleTextures);

  texture = getTexture(BlockFace.FRONT);

  addFaceFront(builder, x, y, z + d, w, h, r, g, b, light, light, light, light,
      texture, scaleTextures);

  texture = getTexture(BlockFace.BACK);

  addFaceBack(builder, x, y, z, w, h, r, g, b, light, light, light, light,
      texture, scaleTextures);
}
