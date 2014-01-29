part of mapViewer;

class Chunk {

    int x;
    int z;

    List<ChunkSection> sections = new List(16);

    bool needsUpdate = false;
    bool needsBuild = false;

    World world;

    Chunk(int x, int z) {
        this.x = x;
        this.z = z;

        Random random = new Random();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int o = (10 * sin((this.x * 16 + x) / 16.0) * cos((this.z * 16 + z) / 16.0)).floor();//random.nextInt(5);
                for (int y = 0; y < 50 + o; y++) {
                    if (y == 50 + o - 1)
                        setBlock(x, y, z, Block._allBlocks[random.nextInt(Block._allBlocks.length - 1) + 1]);
                    else
                        setBlock(x, y, z, Block.STONE);
                }
            }
        }
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
        int idx = x | (z << 4) | ((y & 0xF) << 8);
        var old = section.blocks[x];
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

    List<Uint8List> builtSections = new List(16);
    Buffer renderBuffer;
    int triangleCount = 0;

    List<Uint8List> builtSectionsTrans = new List(16);
    Buffer renderBufferTrans;
    int triangleCountTrans = 0;

    render(RenderingContext gl, int pass) {
        if (needsBuild) {
            needsBuild = false;
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null && section.needsBuild) {
                    section.needsBuild = true;
                    world.requestBuild(this, i);
                }
            }
        }
        if (needsUpdate) {
            needsUpdate = false;
            Uint8List chunkData = new Uint8List(0);
            Uint8List chunkDataTrans = new Uint8List(0);
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null) {
                    if (builtSections[i] != null) {
                        Uint8List temp = chunkData;
                        chunkData = new Uint8List(temp.length + builtSections[i].length);
                        chunkData.setAll(0, temp);
                        chunkData.setAll(temp.length, builtSections[i]);
                    }
                    if (builtSectionsTrans[i] != null) {
                        Uint8List temp = chunkDataTrans;
                        chunkDataTrans = new Uint8List(temp.length + builtSectionsTrans[i].length);
                        chunkDataTrans.setAll(0, temp);
                        chunkDataTrans.setAll(temp.length, builtSectionsTrans[i]);
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

            triangleCount = chunkData.length ~/ 12;
            triangleCountTrans = chunkDataTrans.length ~/ 12;
        }
        if (pass == 0 && renderBuffer != null && triangleCount != 0) {
            gl.uniform2f(offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBuffer);
            gl.vertexAttribPointer(positionLocation, 3, UNSIGNED_BYTE, false, 12, 0);
            gl.vertexAttribPointer(colourLocation, 3, UNSIGNED_BYTE, true, 12, 3);
            gl.vertexAttribPointer(textirePosLocation, 2, UNSIGNED_BYTE, false, 12, 6);
            gl.vertexAttribPointer(textureIdLocation, 2, UNSIGNED_SHORT, false, 12, 8);
            gl.drawArrays(TRIANGLES, 0, triangleCount);
        }
        if (pass == 1 && renderBufferTrans != null && triangleCountTrans != 0) {
            gl.uniform2f(offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBufferTrans);
            gl.vertexAttribPointer(positionLocation, 3, UNSIGNED_BYTE, false, 12, 0);
            gl.vertexAttribPointer(colourLocation, 3, UNSIGNED_BYTE, true, 12, 3);
            gl.vertexAttribPointer(textirePosLocation, 2, UNSIGNED_BYTE, false, 12, 6);
            gl.vertexAttribPointer(textureIdLocation, 2, UNSIGNED_SHORT, false, 12, 8);
            gl.drawArrays(TRIANGLES, 0, triangleCountTrans);
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
                        block.render(block.transparent ? builderTrans : builder, x, (i << 4) + y, z, this);
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
        builtSections[i] = builder.toTypedList();
        builtSectionsTrans[i] = builderTrans.toTypedList();
        needsUpdate = true;
        return null;
    }
}

class BuildSnapshot {
    BlockBuilder builder = new BlockBuilder();
    BlockBuilder builderTrans = new BlockBuilder();
    int x = 0, y = 0, z = 0;
}

class ChunkSection {

    Uint8List blocks = new Uint8List(16 * 16 * 16);

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
        transFloat[0] = x;
        transFloat[1] = y;
        transFloat[2] = z;
        _buffer.addAll(transHelper);
        count++;
    }

    colour(int r, int g, int b) {
        _buffer.add(r);
        _buffer.add(g);
        _buffer.add(b);
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
        transFloat[0] = x;
        transFloat[1] = y;
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