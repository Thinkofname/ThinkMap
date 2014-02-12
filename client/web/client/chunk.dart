part of mapViewer;

abstract class Chunk {

    int x;
    int z;

    List<ChunkSection> sections = new List(16);

    bool needsUpdate = false;
    bool needsBuild = false;

    World world;

    bool noUpdates = true;

    Map<int, Block> _idMap = {0 : Blocks.AIR};
    Map<Block, int> _blockMap = {Blocks.AIR: 0};
    int _nextId = 1;

    /**
   * Sets the [block] at the location given by the [x],
   * [y] and [z] coordinates relative to the chunk.
   *
   * The [x] and [z] coordinates must be between 0 and
   * 15 and [y] must be between 0 and 255.
   */
    setBlock(int x, int y, int z, Block block) {
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
    setLight(int x, int y, int z, int light) {
        var section = sections[y >> 4];
        if (section == null) {
            if (light == 0)
                return;
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
    setSky(int x, int y, int z, int sky) {
        var section = sections[y >> 4];
        if (section == null) {
            if (sky == 15)
                return;
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


    update(int x, int y, int z) {
        if (!noUpdates) {
            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    for (int oy = -1; oy <= 1; oy++) {
                        Chunk chunk = world.getChunk(((this.x*16 + x + ox) >> 4), ((this.x*16 + x + ox) >> 4));
                        if (chunk == null) continue;
                        chunk.needsBuild = true;
                        int idx = (y + oy)>>4;
                        if (idx < 0 || idx >= 16) continue;
                        ChunkSection section = chunk.sections[idx];
                        if (section != null) section.needsBuild = true;
                    }
                }
            }
        }
    }

    rebuild() {
        needsBuild = true;
        for (int i = 0; i < 16; i++) {
            var section = sections[i];
            if (section != null) {
                section.needsBuild = true;
            }
        }
    }

    Object buildSection(int i, Object snapshot, Stopwatch stopwatch);

    unload(Renderer renderer);
}

class ChunkSection {

    static final _emptySkySection = new Uint8List(16 * 16 * 16)..fillRange(0, 16 * 16 * 16, 15);

    Uint16List blocks = new Uint16List(16 * 16 * 16);
    Uint8List light = new Uint8List(16 * 16 * 16);
    Uint8List sky = new Uint8List(16 * 16 * 16)..setAll(0, _emptySkySection);

    int count = 0;

    bool needsBuild = false;
}

class _LoadJob implements _BuildJob {
    Chunk chunk;
    ByteData data;
    int sMask;
    int offset;

    int i = 0;
    int x = 0;
    int y = 0;
    int z = 0;

    _LoadJob(this.chunk, ByteBuffer buffer) {
        data = new ByteData.view(buffer);
        chunk.x = data.getInt32(0);
        chunk.z = data.getInt32(4);
        sMask = data.getUint16(8);
        offset = 10;
    }

    Object exec(Object snapshot, Stopwatch stopwatch) {
        for (; i < 16; i++) {
            if (sMask & (1 << i) != 0) {
                for (int oy = y; oy < 16; oy++) {
                    y = 0;
                    for (int oz = z; oz < 16; oz++) {
                        z = 0;
                        for (int ox = x; ox < 16; ox++) {
                            x = 0;
                            int id = data.getUint16(offset, Endianness.BIG_ENDIAN);
                            offset += 2;
                            int dataVal = data.getUint8(offset);
                            offset++;
                            chunk.setBlock(ox, oy + (i << 4), oz, BlockRegistry.getByLegacy(id, dataVal));
                            chunk.setLight(ox, oy + (i << 4), oz, data.getUint8(offset));
                            offset++;
                            chunk.setSky(ox, oy + (i << 4), oz, data.getUint8(offset));
                            offset++;

                            if (!(stopwatch.elapsedMilliseconds < World.LOAD_LIMIT_MS)) {
                                x = ox + 1;
                                y = oy;
                                z = oz;
                                return this;
                            }
                        }
                        x = y = z = 0;
                    }
                    x = y = z = 0;
                }
                x = y = z = 0;
            }
            x = y = z = 0;
        }
        chunk.noUpdates = false;
        chunk.rebuild();
        String key = world._chunkKey(chunk.x, chunk.z);
        world.chunksLoading.remove(key);
        world.addChunk(chunk);
        return null;
    }
}