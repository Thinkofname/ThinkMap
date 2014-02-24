part of map_viewer;

abstract class Chunk {

  int x;
  int z;

  List<ChunkSection> sections = new List(16);

  bool needsUpdate = false;
  bool needsBuild = false;

  World world;

  bool noUpdates = true;

  Map<int, Block> _idMap = {
    0: Blocks.AIR
  };
  Map<Block, int> _blockMap = {
    Blocks.AIR: 0
  };
  int _nextId = 1;

  // Called once the basic chunk information is loaded
  void init();

  /**
   * Sets the [block] at the location given by the [x],
   * [y] and [z] coordinates relative to the chunk.
   *
   * The [x] and [z] coordinates must be between 0 and
   * 15 and [y] must be between 0 and 255.
   */
  void setBlock(int x, int y, int z, Block block) {
    var section = sections[y >> 4];
    if (section == null) {
      if (block != Blocks.AIR) {
        section = new ChunkSection();
        sections[y >> 4] = section;
      } else {
        return;
      }
    }
    if (!_blockMap.containsKey(block)) {
      _idMap[_nextId] = block;
      _blockMap[block] = _nextId;
      _nextId = (_nextId + 1) & 0xFFFF;
    }
    needsBuild = true;
    section.needsBuild = true;
    update(x, y, z);
    int idx = x | (z << 4) | ((y & 0xF) << 8);
    var old = _idMap[section.blocks[idx]];
    section.blocks[idx] = _blockMap[block];
    if (old == Blocks.AIR && block != Blocks.AIR) {
      section.count++;
    } else if (old != Blocks.AIR && block == Blocks.AIR) {
      section.count--;
    }

    if (section.count == 0) {
      sections[y >> 4] = null;
    }
  }

  /**
   * Gets the block at the location given by the [x],
   * [y] and [z] coordinates relative to the chunk.
   *
   * The [x] and [z] coordinates must be between 0 and
   * 15 and [y] must be between 0 and 255.
   */
  Block getBlock(int x, int y, int z) {
    var section = sections[y >> 4];
    if (section == null) {
      return Blocks.AIR;
    }
    return _idMap[section.blocks[x | (z << 4) | ((y & 0xF) << 8)]];
  }

  /**
     * Sets the [light] at the location given by the [x],
     * [y] and [z] coordinates relative to the chunk.
     *
     * The [x] and [z] coordinates must be between 0 and
     * 15 and [y] must be between 0 and 255.
     */
  void setLight(int x, int y, int z, int light) {
    var section = sections[y >> 4];
    if (section == null) {
      if (light == 0) return;
      section = new ChunkSection();
      sections[y >> 4] = section;
    }
    needsBuild = true;
    section.needsBuild = true;
    update(x, y, z);

    int idx = x | (z << 4) | ((y & 0xF) << 8);
    int old = section.light[idx];
    section.light[idx] = light;

    if (old == 0 && light != 0) {
      section.count++;
    } else if (old != 0 && light == 0) {
      section.count--;
    }

    if (section.count == 0) {
      sections[y >> 4] = null;
    }
  }

  /**
     * Gets the light at the location given by the [x],
     * [y] and [z] coordinates relative to the chunk.
     *
     * The [x] and [z] coordinates must be between 0 and
     * 15 and [y] must be between 0 and 255.
     */
  int getLight(int x, int y, int z) {
    var section = sections[y >> 4];
    if (section == null) {
      return 0;
    }
    return section.light[x | (z << 4) | ((y & 0xF) << 8)];
  }

  /**
     * Sets the [sky] at the location given by the [x],
     * [y] and [z] coordinates relative to the chunk.
     *
     * The [x] and [z] coordinates must be between 0 and
     * 15 and [y] must be between 0 and 255.
     */
  void setSky(int x, int y, int z, int sky) {
    var section = sections[y >> 4];
    if (section == null) {
      if (sky == 15) return;
      section = new ChunkSection();
      sections[y >> 4] = section;
    }
    needsBuild = true;
    section.needsBuild = true;
    update(x, y, z);

    int idx = x | (z << 4) | ((y & 0xF) << 8);
    int old = section.sky[idx];
    section.sky[idx] = sky;

    if (old == 15 && sky != 15) {
      section.count++;
    } else if (old != 15 && sky == 15) {
      section.count--;
    }

    if (section.count == 0) {
      sections[y >> 4] = null;
    }
  }

  /**
     * Gets the sky at the location given by the [x],
     * [y] and [z] coordinates relative to the chunk.
     *
     * The [x] and [z] coordinates must be between 0 and
     * 15 and [y] must be between 0 and 255.
     */
  int getSky(int x, int y, int z) {
    var section = sections[y >> 4];
    if (section == null) {
      return 15;
    }
    return section.sky[x | (z << 4) | ((y & 0xF) << 8)];
  }


  void update(int x, int y, int z) {
    if (!noUpdates) {
      for (int ox = -1; ox <= 1; ox++) {
        for (int oz = -1; oz <= 1; oz++) {
          for (int oy = -1; oy <= 1; oy++) {
            Chunk chunk = world.getChunk(((this.x * 16 + x + ox) >> 4), ((this.x
                * 16 + x + ox) >> 4));
            if (chunk == null) continue;
            chunk.needsBuild = true;
            int idx = (y + oy) >> 4;
            if (idx < 0 || idx >= 16) continue;
            ChunkSection section = chunk.sections[idx];
            if (section != null) section.needsBuild = true;
          }
        }
      }
    }
  }

  void rebuild() {
    needsBuild = true;
    for (int i = 0; i < 16; i++) {
      var section = sections[i];
      if (section != null) {
        section.needsBuild = true;
      }
    }
  }

  Object buildSection(int i, Object snapshot, Stopwatch stopwatch);

  void unload(Renderer renderer);

  void fromMap(Map data) {
    x = data['x'];
    z = data['z'];

    List<Map> dSections = data['sections'];
    for (int i = 0; i < 16; i++) {
      var dSection = dSections[i];
      if (dSection != null) {
        ChunkSection section = sections[i] = new ChunkSection();
        section.count = dSection['count'];
        section.blocks = new Uint16List.fromList(dSection['blocks']);
        section.light = new Uint8List.fromList(dSection['light']);
        section.sky = new Uint8List.fromList(dSection['sky']);
      }
    }
    _nextId = data['nextId'];
    (data['idMap'] as Map<int, String>).forEach((k, v) {
      _idMap[k] = BlockRegistry.getByName(v);
    });
    (data['blockMap'] as Map<String, int>).forEach((k, v) {
      _blockMap[BlockRegistry.getByName(k)] = v;
    });
  }

  static Map processData(Uint8List byteData, ByteData data) {
    Map out = new Map();
    int sMask = data.getUint16(8);
    List<Map> sections = new List(16);
    out['sections'] = sections;
    out['x'] = data.getInt32(0);
    out['z'] = data.getInt32(4);
    int offset = 10;

    Map<int, String> _idMap = {
      0: Blocks.AIR._regBlock.toString()
    };
    Map<String, int> _blockMap = {
      Blocks.AIR._regBlock.toString(): 0
    };
    int _nextId = 1;
    for (int i = 0 ; i < 16; i++) {
      if (sMask & (1 << i) != 0) {
        int idx = 0;
        Map section = sections[i];
        if (section == null) section = sections[i] = new Map();
        List<int> blocks = new List(16 * 16 * 16);
        List<int> lights = new List(16 * 16 * 16);
        List<int> skys = new List(16 * 16 * 16);
        int count = 0;
        for (int oy = 0; oy < 16; oy++) {
          for (int oz = 0; oz < 16; oz++) {
            for (int ox = 0; ox < 16; ox++) {
              int id = (byteData[offset]<<8) + byteData[offset + 1];
              int dataVal = byteData[offset + 2];
              int light = byteData[offset + 3];
              int sky = byteData[offset + 4];
              offset += 5;
              Block block = BlockRegistry.getByLegacy(id, dataVal);
              int rid = _blockMap[block._regBlock.toString()];
              if (rid == null) {
                _idMap[_nextId] = block._regBlock.toString();
                rid = _blockMap[block._regBlock.toString()] = _nextId;
                _nextId = (_nextId + 1) & 0xFFFF;
              }

              blocks[idx] = rid;
              lights[idx] = light;
              skys[idx] = sky;
              idx++;

              if (block != Blocks.AIR) count++;
              if (light != 0) count++;
              if (sky != 15) count++;
            }
          }
        }
        section['blocks'] = blocks;
        section['light'] = lights;
        section['sky'] = skys;
        section['count'] = count;
      }
    }
    out['idMap'] = _idMap;
    out['blockMap'] = _blockMap;
    out['nextId'] = _nextId;
    return out;
  }
}

class ChunkSection {

  static final Uint8List _emptySkySection = new Uint8List(16 * 16 * 16)..fillRange(0, 16 *
      16 * 16, 15);

  Uint16List blocks = new Uint16List(16 * 16 * 16);
  Uint8List light = new Uint8List(16 * 16 * 16);
  Uint8List sky = new Uint8List(16 * 16 * 16)..setAll(0, _emptySkySection);

  int count = 0;
  bool needsBuild = false;
  bool needsUpdate = false;
}
