part of map_viewer;

class IsolateChunk extends Chunk {
  IsolateChunk(Uint8List data) {
    _fromData(data);
  }

  IsolateChunk.stream(Uint8List data, SendPort port) {
    _fromData(data);
    Map out = new Map();
    out['x'] = x;
    out['z'] = z;

    List<Map> secs = new List(16);
    for (int i = 0; i < 16; i++) {
      if (sections[i] != null) {
        var s = secs[i] = new Map();
        s['count'] = sections[i].count;
        s['buffer'] = CryptoUtils.bytesToBase64(sections[i]._buffer);
      }
    }
    out['sections'] = secs;

    out['nextId'] = _nextId;
    Map<int, String> idMap = new Map();
    _idMap.forEach((k, v) {
      idMap[k] = v._regBlock.toString();
    });
    out['idMap'] = idMap;
    Map<String, int> blockMap = new Map();
    _blockMap.forEach((k, v) {
      blockMap[k._regBlock.toString()] = v;
    });
    out['blockMap'] = blockMap;
    port.send(out);
  }

  void _fromData(Uint8List byteData) {
    ByteData data = new ByteData.view(byteData.buffer);
    int sMask = data.getUint16(8);
    x = data.getInt32(0);
    z = data.getInt32(4);
    int offset = 10;

    for (int i = 0; i < 16; i++) {
      if (sMask & (1 << i) != 0) {
        int idx = 0;
        ChunkSection cs = sections[i] = new ChunkSection();
        for (int oy = 0; oy < 16; oy++) {
          for (int oz = 0; oz < 16; oz++) {
            for (int ox = 0; ox < 16; ox++) {
              int id = (byteData[offset]<<8) + byteData[offset + 1];
              int dataVal = byteData[offset + 2];
              int light = byteData[offset + 3];
              int sky = byteData[offset + 4];
              offset += 5;
              Block block = BlockRegistry.getByLegacy(id, dataVal);
              int rid = _blockMap[block];
              if (rid == null) {
                _idMap[_nextId] = block;
                rid = _blockMap[block] = _nextId;
                _nextId = (_nextId + 1) & 0xFFFF;
              }

              cs.blocks[idx] = rid;
              cs.light[idx] = light;
              cs.sky[idx] = sky;
              idx++;

              if (block != Blocks.AIR) cs.count++;
              if (light != 0) cs.count++;
              if (sky != 15) cs.count++;
            }
          }
        }
      }
    }
  }

  init() {

  }

  Object buildSection(int i, Object snapshot, Stopwatch stopwatch) => null;

  void unload(Renderer renderer) {}
}