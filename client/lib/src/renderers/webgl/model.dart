part of map_viewer;

final Model testModel = new Model();

final Map<String, Model> models = new Map()..['test_model'] = testModel;

class Model {

  List<ModelFace> faces = new List();

  Model();

  Model.fromJson(String json) : this.fromMap(JSON.decode(json));

  Model.fromMap(Map input) {
    for (Map iFace in input['faces'] as List) {
      ModelFace face = new ModelFace.zero(new BlockFace.fromName(iFace['face']));
      faces.add(face);
      face.texture = iFace['texture'];
      face.r = iFace['colour']['r'];
      face.g = iFace['colour']['g'];
      face.b = iFace['colour']['b'];
      for (Map iVert in iFace['vertices'] as List) {
        ModelVertex vert = new ModelVertex.zero();
        face.vertices.add(vert);
        vert.x = iVert['x'] / 16;
        vert.y = iVert['y'] / 16;
        vert.z = iVert['z'] / 16;
        vert.textureX = iVert['textureX'] / 16;
        vert.textureY = iVert['textureY'] / 16;
      }
    }
  }

  void render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
    LightInfo light = new LightInfo(chunk.getLight(x, y, z), chunk.getSky(x, y, z));
    for (ModelFace face in faces) {
      TextureInfo texture = blockTextureInfo[face.texture];
      for (int i = 0; i < 3; i++) {
        ModelVertex vert = face.vertices[i];
        builder
          ..position(x + vert.x, y + vert.y, z + vert.z)
          ..colour(face.r, face.g, face.b)
          ..tex(vert.textureX, vert.textureY)
          ..texId(texture.start, texture.end)
          ..lighting(light.light, light.sky);
      }
      for (int i = 3; i > 0; i--) {
        ModelVertex vert = face.vertices[i];
        builder
          ..position(x + vert.x, y + vert.y, z + vert.z)
          ..colour(face.r, face.g, face.b)
          ..tex(vert.textureX, vert.textureY)
          ..texId(texture.start, texture.end)
          ..lighting(light.light, light.sky);
      }
    }
  }

  static final List<BlockFace> _rotateFaces = [
    BlockFace.LEFT,
    BlockFace.FRONT,
    BlockFace.RIGHT,
    BlockFace.BACK
  ];

  Model rotate(int deg) {
    Model out = new Model();
    Quaternion quat = new Quaternion.axisAngle(new Vector3(0.0, 1.0, 0.0), radians(deg));
    for (ModelFace face in faces) {
      ModelFace newFace = new ModelFace.zero(_rotateFaces[(_rotateFaces.indexOf(face.face) + deg~/90) % 4]);
      newFace.texture = face.texture;
      newFace.r = face.r;
      newFace.g = face.g;
      newFace.b = face.b;
      out.faces.add(newFace);
      for (ModelVertex vert in face.vertices) {
        ModelVertex newVert = vert.clone();
        Vector3 vec = new Vector3(newVert.x - 0.5, newVert.y - 0.5, newVert.z - 0.5);
        quat.rotate(vec);
        newVert
          ..x = vec.x + 0.5
          ..y = vec.y + 0.5
          ..z = vec.z + 0.5;
        newFace.vertices.add(newVert);
      }
    }
    return out;
  }

  Model join(Model other) {
    Model out = new Model();
    for (Model target in [this, other]) {
      for (ModelFace face in target.faces) {
        ModelFace newFace = new ModelFace.zero(face.face);
        newFace.texture = face.texture;
        newFace.r = face.r;
        newFace.g = face.g;
        newFace.b = face.b;
        out.faces.add(newFace);
        for (ModelVertex vert in face.vertices) {
          ModelVertex newVert = vert.clone();
          newFace.vertices.add(newVert);
        }
      }
    }
    return out;
  }

  static String _noopTextureGetter(String texture) => texture;

  Model clone([String getTexture(String texture) = _noopTextureGetter]) {
    Model out = new Model();
    for (ModelFace face in faces) {
      ModelFace newFace = new ModelFace.zero(face.face);
      newFace.texture = getTexture(face.texture);
      newFace.r = face.r;
      newFace.g = face.g;
      newFace.b = face.b;
      out.faces.add(newFace);
      for (ModelVertex vert in face.vertices) {
        ModelVertex newVert = vert.clone();
        newFace.vertices.add(newVert);
      }
    }
    return out;
  }

  @override
  dynamic toJson() {
    Map output = new Map();

    List oFaces = new List();
    output['faces'] = oFaces;
    for (ModelFace face in faces) {
      Map oFace = new Map();
      oFaces.add(oFace);
      oFace['colour'] = {'r':face.r, 'g': face.g, 'b': face.b};
      oFace['texture'] = face.texture;
      oFace['face'] = face.face.name;
      List oVerts = new List();
      oFace['vertices'] = oVerts;
      for (ModelVertex vert in face.vertices) {
        Map oVert = new Map();
        oVerts.add(oVert);
        oVert['x'] = (vert.x * 16).toInt();
        oVert['y'] = (vert.y * 16).toInt();
        oVert['z'] = (vert.z * 16).toInt();
        oVert['textureX'] = (vert.textureX * 16).toInt();
        oVert['textureY'] = (vert.textureY * 16).toInt();
      }
    }
    return output;
  }
}

class ModelFace {
  static final Map<BlockFace, List<ModelVertex>> _defaultFaces = {
    BlockFace.TOP: [
      new ModelVertex(0, 0, 0, 0, 0),
      new ModelVertex(1, 0, 0, 1, 0),
      new ModelVertex(0, 0, 1, 0, 1),
      new ModelVertex(1, 0, 1, 1, 1)
    ],
    BlockFace.BOTTOM: [
      new ModelVertex(0, 0, 0, 0, 0),
      new ModelVertex(0, 0, 1, 0, 1),
      new ModelVertex(1, 0, 0, 1, 0),
      new ModelVertex(1, 0, 1, 1, 1)
    ],
    BlockFace.LEFT: [
      new ModelVertex(0, 0, 0, 1, 1),
      new ModelVertex(0, 0, 1, 0, 1),
      new ModelVertex(0, 1, 0, 1, 0),
      new ModelVertex(0, 1, 1, 0, 0)
    ],
    BlockFace.RIGHT: [
      new ModelVertex(0, 0, 0, 0, 1),
      new ModelVertex(0, 1, 0, 0, 0),
      new ModelVertex(0, 0, 1, 1, 1),
      new ModelVertex(0, 1, 1, 1, 0),
    ],
    BlockFace.FRONT: [
      new ModelVertex(0, 0, 0, 0, 1),
      new ModelVertex(0, 1, 0, 0, 0),
      new ModelVertex(1, 0, 0, 1, 1),
      new ModelVertex(1, 1, 0, 1, 0)
    ],
    BlockFace.BACK: [
      new ModelVertex(0, 0, 0, 1, 1),
      new ModelVertex(1, 0, 0, 0, 1),
      new ModelVertex(0, 1, 0, 1, 0),
      new ModelVertex(1, 1, 0, 0, 0)
    ]
  };

  String texture;
  List<ModelVertex> vertices = new List();
  BlockFace face;
  int r = 255;
  int g = 255;
  int b = 255;

  ModelFace.zero(this.face);

  ModelFace(this.face) {
    for (ModelVertex vert in _defaultFaces[face]) {
      vertices.add(vert.clone());
    }
  }

  // Editor methods
  moveY(num a, [bool tex = false]) {
    for (ModelVertex vert in vertices) {
      if (!tex)
        vert.y += a;
      else
        vert.textureY += a;
    }
  }

  moveX(num a, [bool tex = false]) {
    for (ModelVertex vert in vertices) {
      if (!tex)
        vert.x += a;
      else
        vert.textureX += a;
    }
  }

  moveZ(num a, [bool tex = false]) {
    for (ModelVertex vert in vertices) {
      vert.z += a;
    }
  }

  sizeY(num a, [bool tex = false]) {
    num largest = 0;
    if (!tex) {
      for (ModelVertex vert in vertices) {
        if (vert.y > largest) largest = vert.y;
      }
      for (ModelVertex vert in vertices) {
        if (vert.y == largest) {
          vert.y += a;
        }
      }
    } else {
      for (ModelVertex vert in vertices) {
        if (vert.textureY > largest) largest = vert.textureY;
      }
      for (ModelVertex vert in vertices) {
        if (vert.textureY == largest) {
          vert.textureY += a;
        }
      }
    }
  }

  sizeX(num a, [bool tex = false]) {
    num largest = 0;
    if (!tex) {
      for (ModelVertex vert in vertices) {
        if (vert.x > largest) largest = vert.x;
      }
      for (ModelVertex vert in vertices) {
        if (vert.x == largest) {
          vert.x += a;
        }
      }
    } else {
      for (ModelVertex vert in vertices) {
        if (vert.textureX > largest) largest = vert.textureX;
      }
      for (ModelVertex vert in vertices) {
        if (vert.textureX == largest) {
          vert.textureX += a;
        }
      }
    }
  }

  sizeZ(num a, [bool tex = false]) {
    num largest = 0;
    for (ModelVertex vert in vertices) {
      if (vert.z > largest) largest = vert.z;
    }
    for (ModelVertex vert in vertices) {
      if (vert.z == largest) {
        vert.z += a;
      }
    }
  }
}

class ModelVertex {
  ModelFace face;

  num x;
  num y;
  num z;

  num textureX;
  num textureY;

  ModelVertex.zero();

  ModelVertex(this.x, this.y, this.z, this.textureX, this.textureY);

  ModelVertex clone() => new ModelVertex(x, y, z, textureX, textureY);
}


