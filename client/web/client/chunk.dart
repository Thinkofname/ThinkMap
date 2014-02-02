part of mapViewer;

class Chunk {

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
                            _setBlock(ox, oy + (i << 4), oz, data.getUint16(offset));
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
                        Chunk chunk = world.getChunk((this.x*16 + x + ox) ~/ 16, (this.x*16 + x + ox) ~/ 16);
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

    List<Uint8List> builtSections = new List(16);
    Buffer renderBuffer;
    int bufferSize = 0;
    int triangleCount = 0;

    List<Uint8List> builtSectionsTrans = new List(16);
    Buffer renderBufferTrans;
    int bufferSizeTrans = 0;
    int triangleCountTrans = 0;

    List<Uint8List> builtSectionsFloat = new List(16);
    Buffer renderBufferFloat;
    int bufferSizeFloat = 0;
    int triangleCountFloat = 0;

    render(RenderingContext gl, int pass) {
        if (needsBuild) {
            needsBuild = false;
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null && section.needsBuild) {
                    section.needsBuild = false;
                    world.requestBuild(this, i);
                }
            }
        }
        if (needsUpdate) {
            needsUpdate = false;
            Uint8List chunkData = new Uint8List(bufferSize);
            int offset = 0;
            Uint8List chunkDataTrans = new Uint8List(bufferSizeTrans);
            int offsetTrans = 0;
            Uint8List chunkDataFloat = new Uint8List(bufferSizeFloat);
            int offsetFloat = 0;
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null) {
                    if (builtSections[i] != null) {
                        chunkData.setAll(offset, builtSections[i]);
                        offset += builtSections[i].length;
                    }
                    if (builtSectionsTrans[i] != null) {
                        chunkDataTrans.setAll(offsetTrans, builtSectionsTrans[i]);
                        offsetTrans += builtSectionsTrans[i].length;
                    }
                    if (builtSectionsFloat[i] != null) {
                        chunkDataFloat.setAll(offsetFloat, builtSectionsFloat[i]);
                        offsetFloat += builtSectionsFloat[i].length;
                    }
                }
            }
            if (renderBuffer == null) {
                renderBuffer = gl.createBuffer();
            }
            gl.bindBuffer(ARRAY_BUFFER, renderBuffer);
            gl.bufferData(ARRAY_BUFFER, chunkData, STATIC_DRAW);

            if (renderBufferTrans == null) {
                renderBufferTrans = gl.createBuffer();
            }
            gl.bindBuffer(ARRAY_BUFFER, renderBufferTrans);
            gl.bufferData(ARRAY_BUFFER, chunkDataTrans, STATIC_DRAW);

            if (renderBufferFloat == null) {
                renderBufferFloat = gl.createBuffer();
            }
            gl.bindBuffer(ARRAY_BUFFER, renderBufferFloat);
            gl.bufferData(ARRAY_BUFFER, chunkDataFloat, STATIC_DRAW);

            triangleCount = chunkData.length ~/ 12;
            triangleCountTrans = chunkDataTrans.length ~/ 12;
            triangleCountFloat = chunkDataFloat.length ~/ 28;
        }
        if (pass == 0 && renderBuffer != null && triangleCount != 0) {
            gl.uniform2f(offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBuffer);
            gl.vertexAttribPointer(positionLocation, 3, UNSIGNED_BYTE, false, 12, 0);
            gl.vertexAttribPointer(colourLocation, 3, UNSIGNED_BYTE, true, 12, 3);
            gl.vertexAttribPointer(texturePosLocation, 2, UNSIGNED_BYTE, false, 12, 6);
            gl.vertexAttribPointer(textureIdLocation, 2, UNSIGNED_SHORT, false, 12, 8);
            gl.drawArrays(TRIANGLES, 0, triangleCount);
        } else if (pass == 1 && renderBufferFloat != null && triangleCountFloat != 0) {
            gl.uniform2f(offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBufferFloat);
            gl.vertexAttribPointer(positionLocation, 3, FLOAT, false, 28, 0);
            gl.vertexAttribPointer(colourLocation, 3, UNSIGNED_BYTE, true, 28, 12);
            gl.vertexAttribPointer(texturePosLocation, 2, FLOAT, false, 28, 16);
            gl.vertexAttribPointer(textureIdLocation, 2, UNSIGNED_SHORT, false, 28, 24);
            gl.drawArrays(TRIANGLES, 0, triangleCountFloat);
        } else if (pass == 2 && renderBufferTrans != null && triangleCountTrans != 0) {
            gl.uniform2f(offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBufferTrans);
            gl.vertexAttribPointer(positionLocation, 3, UNSIGNED_BYTE, false, 12, 0);
            gl.vertexAttribPointer(colourLocation, 3, UNSIGNED_BYTE, true, 12, 3);
            gl.vertexAttribPointer(texturePosLocation, 2, UNSIGNED_BYTE, false, 12, 6);
            gl.vertexAttribPointer(textureIdLocation, 2, UNSIGNED_SHORT, false, 12, 8);
            gl.drawArrays(TRIANGLES, 0, triangleCountTrans);
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

    BuildSnapshot buildSection(int i, BuildSnapshot snapshot, Stopwatch stopwatch) {
        if (snapshot == null) {
            snapshot = new BuildSnapshot();
        }
        BlockBuilder builder = snapshot.builder;
        BlockBuilder builderTrans = snapshot.builderTrans;
        for (int x = snapshot.x; x < 16; x++) {
            int sz = x == snapshot.x ? snapshot.z : 0;
            for (int z = sz; z < 16; z++) {
                int sy = 0;
                if (z == snapshot.z){
                    sy = snapshot.y;
                    snapshot.z = -1;
                }
                for (int y = sy; y < 16; y++) {
                    Block block = getBlock(x, (i << 4) + y, z);
                    if (block.renderable) {
                        block.renderFloat(block.transparent ? builderTrans : builder, snapshot.builderFloat, x, (i << 4) + y, z, this);
                    }
                    if (stopwatch.elapsedMilliseconds >= World.BUILD_LIMIT_MS) {
                        snapshot.x = x;
                        snapshot.y = y + 1;
                        snapshot.z = z;
                        return snapshot;
                    }
                }
            }
        }
        // Resize
        bufferSize -= builtSections[i] != null ? builtSections[i].length : 0;
        bufferSizeTrans -= builtSectionsTrans[i] != null ? builtSectionsTrans[i].length : 0;
        bufferSizeFloat -= builtSectionsFloat[i] != null ? builtSectionsFloat[i].length : 0;
        // Store
        builtSections[i] = builder.toTypedList();
        builtSectionsTrans[i] = builderTrans.toTypedList();
        builtSectionsFloat[i] = snapshot.builderFloat.toTypedList();
        // Resize
        bufferSize += builtSections[i].length;
        bufferSizeTrans += builtSectionsTrans[i].length;
        bufferSizeFloat += builtSectionsFloat[i].length;
        needsUpdate = true;
        return null;
    }

    unload(RenderingContext gl) {
        if (renderBuffer != null) {
            gl.deleteBuffer(renderBuffer);
        }
        if (renderBufferTrans != null) {
            gl.deleteBuffer(renderBufferTrans);
        }
        if (renderBufferFloat != null) {
            gl.deleteBuffer(renderBufferFloat);
        }
    }
}

class BuildSnapshot {
    BlockBuilder builder = new BlockBuilder();
    BlockBuilder builderTrans = new BlockBuilder();
    FloatBlockBuilder builderFloat = new FloatBlockBuilder();
    int x = 0;
    int y = 0;
    int z = 0;
}

class ChunkSection {

    Uint16List blocks = new Uint16List(16 * 16 * 16);
    Uint8List data = new Uint8List(16 * 16 * 16);
    Uint8List light = new Uint8List(16 * 16 * 16);
    Uint8List sky = new Uint8List(16 * 16 * 16)..fillRange(0, 16 * 16 * 16, 15);

    int count = 0;

    bool needsBuild = false;
}

class BlockBuilder {

    List<int> _buffer = new List();

    int count = 0;

    BlockBuilder() {

    }

    position(num x, num y, num z) {
        _buffer.add(x);
        _buffer.add(y);
        _buffer.add(z);
        count++;
    }

    colour(int r, int g, int b) {
        _buffer.add(r);
        _buffer.add(g);
        _buffer.add(b);
    }

    static Uint8List transHelper = new Uint8List(8);
    static Uint16List trans16 = new Uint16List.view(transHelper.buffer);

    texId(int start, int end) {
        trans16[0] = start;
        trans16[1] = end;
        _buffer.add(transHelper[0]);
        _buffer.add(transHelper[1]);
        _buffer.add(transHelper[2]);
        _buffer.add(transHelper[3]);
    }

    tex(num x, num y) {
        _buffer.add(x);
        _buffer.add(y);
    }

    Uint8List toTypedList() {
        return new Uint8List.fromList(_buffer);
    }
}

class FloatBlockBuilder implements BlockBuilder {

    List<int> _buffer = new List();

    int count = 0;

    FloatBlockBuilder() {

    }

    static Uint8List transHelper = new Uint8List(12);
    static Uint16List trans16 = new Uint16List.view(transHelper.buffer);
    static Float32List transFloat = new Float32List.view(transHelper.buffer);

    position(num x, num y, num z) {
        transFloat[0] = x.toDouble();
        transFloat[1] = y.toDouble();
        transFloat[2] = z.toDouble();
        _buffer.addAll(transHelper);
        count++;
    }

    colour(int r, int g, int b) {
        _buffer.add(r);
        _buffer.add(g);
        _buffer.add(b);

        _buffer.add(0);
    }

    texId(int start, int end) {
        trans16[0] = start;
        trans16[1] = end;
        _buffer.add(transHelper[0]);
        _buffer.add(transHelper[1]);
        _buffer.add(transHelper[2]);
        _buffer.add(transHelper[3]);
    }

    tex(num x, num y) {
        transFloat[0] = x.toDouble();
        transFloat[1] = y.toDouble();
        _buffer.add(transHelper[0]);
        _buffer.add(transHelper[1]);
        _buffer.add(transHelper[2]);
        _buffer.add(transHelper[3]);
        _buffer.add(transHelper[4]);
        _buffer.add(transHelper[5]);
        _buffer.add(transHelper[6]);
        _buffer.add(transHelper[7]);
    }

    Uint8List toTypedList() {
        return new Uint8List.fromList(_buffer);
    }
}