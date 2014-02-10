part of mapViewer;

abstract class Chunk {

    int x;
    int z;

    List<ChunkSection> sections = new List(16);

    bool needsUpdate = false;
    bool needsBuild = false;

    World world;

    bool noUpdates = true;

    Chunk(this.x, this.z, this.world) {

        Random random = new Random();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int o = (10 * sin((this.x * 16 + x) / 16.0) * cos((this.z * 16 + z) / 16.0)).floor();//random.nextInt(5);
                for (int y = 0; y < 50 + o; y++) {
                    if (y == 50 + o - 1)
                        setBlock(x, y, z, random.nextInt(5) != 0 ? Block.GRASS : Block._allBlocks[random.nextInt(Block._allBlocks.length - 1) + 1]);
                    else if (y >= 50 + o - 5)
                        setBlock(x, y, z, Block.DIRT);
                    else
                        setBlock(x, y, z, Block.STONE);
                }
            }
        }
        noUpdates = false;
        rebuild();
    }

    Chunk.fromBuffer(this.world, ByteBuffer buffer, [int o = 0]) {
        ByteData data = new ByteData.view(buffer);
        x = data.getInt32(o + 0);
        z = data.getInt32(o + 4);
        int sMask = data.getUint16(o + 8);
        int offset = o + 10;
        for (int i = 0; i < 16; i++) {
            if (sMask & (1 << i) != 0) {
                for (int oy = 0; oy < 16; oy++) {
                    for (int oz = 0; oz < 16; oz++) {
                        for (int ox = 0; ox < 16; ox++) {
                            _setBlock(ox, oy + (i << 4), oz, data.getUint16(offset, Endianness.BIG_ENDIAN));
                            offset += 2;
                            setData(ox, oy + (i << 4), oz, data.getUint8(offset));
                            offset++;
                            setLight(ox, oy + (i << 4), oz, data.getUint8(offset));
                            offset++;
                            setSky(ox, oy + (i << 4), oz, data.getUint8(offset));
                            offset++;
                        }
                    }
                }
            }
        }
        noUpdates = false;
        rebuild();
    }

    /**
   * Sets the [block] at the location given by the [x],
   * [y] and [z] coordinates relative to the chunk.
   *
   * The [x] and [z] coordinates must be between 0 and
   * 15 and [y] must be between 0 and 255.
   */
    setBlock(int x, int y, int z, Block block) {
        _setBlock(x, y, z, block.legacyId);
    }

    //Internal method for working with legacy ids until this
    //is changed in Minecraft
    _setBlock(int x, int y, int z, int block) {
        var section = sections[y >> 4];
        if (section == null) {
            if (block != Block.AIR) {
                section = new ChunkSection();
                sections[y >> 4] = section;
            } else {
                return;
            }
        }
        needsBuild = true;
        section.needsBuild = true;
        update(x, y, z);
        int idx = x | (z << 4) | ((y & 0xF) << 8);
        var old = section.blocks[idx];
        section.blocks[idx] = block;
        if (old == Block.AIR.legacyId && block != Block.AIR.legacyId) {
            section.count++;
        } else if (old != Block.AIR.legacyId && block == Block.AIR.legacyId) {
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
        return Block.blockFromLegacyId(_getBlock(x, y, z));
    }

    //Internal method for working with legacy ids until this
    //is changed in Minecraft
    int _getBlock(int x, int y, int z) {
        var section = sections[y >> 4];
        if (section == null) {
            return Block.AIR.legacyId;
        }
        return section.blocks[x | (z << 4) | ((y & 0xF) << 8)];
    }

    /**
     * Sets the [data] at the location given by the [x],
     * [y] and [z] coordinates relative to the chunk.
     *
     * The [x] and [z] coordinates must be between 0 and
     * 15 and [y] must be between 0 and 255.
     */
    setData(int x, int y, int z, int data) {
        var section = sections[y >> 4];
        if (section == null) {
            return;
        }
        needsBuild = true;
        section.needsBuild = true;
        update(x, y, z);

        int idx = x | (z << 4) | ((y & 0xF) << 8);
        section.data[idx] = data;
    }

    /**
     * Gets the data at the location given by the [x],
     * [y] and [z] coordinates relative to the chunk.
     *
     * The [x] and [z] coordinates must be between 0 and
     * 15 and [y] must be between 0 and 255.
     */
    int getData(int x, int y, int z) {
        var section = sections[y >> 4];
        if (section == null) {
            return 0;
        }
        return section.data[x | (z << 4) | ((y & 0xF) << 8)];
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
    Uint8List data = new Uint8List(16 * 16 * 16);
    Uint8List light = new Uint8List(16 * 16 * 16);
    Uint8List sky = new Uint8List(16 * 16 * 16)..setAll(0, _emptySkySection);

    int count = 0;

    bool needsBuild = false;
}