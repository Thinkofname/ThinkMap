part of mapViewer;

class Chunk {

    int x, z;

    List<ChunkSection> sections = new List(16);

    bool needsUpdate = false;
    bool needsBuild = false;

    World world;

    Chunk(int x, int z) {
        this.x = x;
        this.z = z;

        window.console.time("Chunk Gen");
        Random random = new Random();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int o = (10 * sin((this.x * 16 + x) / 16.0) * cos((this.z * 16 + z) / 16.0)).floor();//random.nextInt(5);
                for (int y = 15; y < 50 + o; y++) {
                    setBlock(x, y, z, x == 0 || x == 15 ? Block.GRASS : (z == 0 || z == 15 ? Block.DIRT : Block.STONE));//Block.blockFromLegacyId(random.nextInt(3) + 1));
                }
            }
        }
        window.console.timeEnd("Chunk Gen");
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

    render(RenderingContext gl) {
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
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null) {
                    if (builtSections[i] != null) {
                        Uint8List temp = chunkData;
                        chunkData = new Uint8List(temp.length + builtSections[i].length);
                        chunkData.setAll(0, temp);
                        chunkData.setAll(temp.length, builtSections[i]);
                    }
                }
            }
            if (renderBuffer == null) {
                renderBuffer = gl.createBuffer();
            }
            gl.bindBuffer(ARRAY_BUFFER, renderBuffer);
            gl.bufferData(ARRAY_BUFFER, chunkData, STATIC_DRAW);

            triangleCount = chunkData.length ~/ 6;
        }
        if (renderBuffer != null && triangleCount != 0) {
            gl.uniform2f(offsetLocation, x, z);
            gl.bindBuffer(ARRAY_BUFFER, renderBuffer);
            gl.vertexAttribPointer(positionLocation, 3, UNSIGNED_BYTE, false, 6, 0);
            gl.vertexAttribPointer(colourLocation, 3, UNSIGNED_BYTE, true, 6, 3);
            gl.drawArrays(TRIANGLES, 0, triangleCount);
        }
    }

    BuildSnapshot buildSection(int i, BuildSnapshot snapshot, Stopwatch stopwatch) {
        if (snapshot == null) {
            snapshot = new BuildSnapshot();
        }
        BlockBuilder builder = snapshot.builder;
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
                        block.render(builder, x, (i << 4) + y, z, this);
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
        needsUpdate = true;
        return null;
    }
}

class BuildSnapshot {
    BlockBuilder builder = new BlockBuilder();
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

    position(int x, int y, int z) {
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

    Uint8List toTypedList() {
        return new Uint8List.fromList(_buffer);
    }
}