part of mapViewer;

class CanvasRenderer extends Renderer {

    CanvasRenderingContext2D ctx;

    int cx = 0;
    int cz = 0;

    double cameraX = 0.0;
    double cameraZ = 0.0;
    double zoom = 1.0;

    Map<int, ImageData> blockRawData = new Map();

    CanvasRenderer(CanvasElement canvas) {
        ctx = canvas.getContext("2d");
        ctx.imageSmoothingEnabled = false;

        var temp = new CanvasElement();
        var ttx = temp.getContext("2d") as CanvasRenderingContext2D;
        temp.width = blockTexturesRaw[0].width;
        temp.height = blockTexturesRaw[0].height;
        ttx.drawImage(blockTexturesRaw[0], 0, 0);
        for (TextureInfo info in blockTextureInfo.values) {
            // No animations just the start texture
            var x = info.start % 32;
            var y = (info.start ~/ 32);

            blockRawData[info.start] = ttx.getImageData(x * 16, y * 16, 16, 16);
        }

        bool down = false;
        int x = 0;
        int y = 0;
        document.body.onMouseDown.listen((e) {
            down = true;
            x = e.client.x;
            y = e.client.y;
            document.body.onMouseUp.first.then((e) {
                down = false;
            });
        });
        document.body.onMouseMove.where((e) => down).listen((e) {
            double dx = -(e.client.x - x) / 8 / zoom;
            double dy = -(e.client.y - y) / 8 / zoom;
            cameraX += dx + dy;
            cameraZ += dx * 0.5 - dy * 0.5;
            x = e.client.x;
            y = e.client.y;
        });
        document.body.onMouseWheel.listen((e) {
            JsObject jse = new JsObject.fromBrowserObject(e);
            // TODO: Fix once dart fixes this bug
            // zoom += e.wheelDeltaY;
            if (jse["deltaY"] != null) {
                zoom += -(jse["deltaY"] as int) < 0 ? -0.2 : 0.2;
            } else {
                zoom += (jse["wheelDeltaY"] as int) < 0 ? -0.2 : 0.2;
            }
            e.preventDefault();
        });
    }

    static const int viewDistance = 4;

    @override
    connected() {
        for (int x = -viewDistance; x < viewDistance; x++) {
            for (int z = -viewDistance; z < viewDistance; z++) {
                connection.writeRequestChunk(x, z);
            }
        }
    }

    @override
    draw() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.save();
        ctx.scale(zoom, zoom);
        ctx.translate(((canvas.width / zoom) - canvas.width) / 2, ((canvas.height / zoom) - canvas.height) / 2);
        ctx.translate((-cameraX * 16 - cameraZ * 16).toInt(), (-cameraX * 8 + cameraZ * 8).toInt());
        (world as CanvasWorld).render(this);
        ctx.restore();

        int nx = (cameraX - 8) ~/ 16;
        int nz = (cameraZ - 8) ~/ 16;
        if (nx != cx || nz != cz) {
            for (int x = nx-viewDistance; x < nx+viewDistance; x++) {
                for (int z = nz-viewDistance; z < nz+viewDistance; z++) {
                    if (world.getChunk(x, z) == null)
                        connection.writeRequestChunk(x, z);
                }
            }

            for (Chunk chunk in new List.from(world.chunks.values)) {
                int x = chunk.x;
                int z = chunk.z;
                if (x < nx-viewDistance || x >= nx+viewDistance
                || z < nz-viewDistance || z >= nz+viewDistance) {
                    world.removeChunk(x, z);
                }
            }
            cx = nx;
            cz = nz;
        }
    }

    @override
    resize(int width, int height) {

    }

    @override
    bool shouldLoad(int x, int z) {
        if (x < cx-viewDistance || x >= cx+viewDistance
        || z < cz-viewDistance || z >= cz+viewDistance) {
            return false;
        }
        return true;
    }
}

class CanvasWorld extends World {

    Map<String, bool> _waitingForBuild = new Map();
    List<_BuildJob> _buildQueue = new List();
    _BuildJob currentBuild;
    CanvasSnapshot currentSnapshot;
    Stopwatch stopwatch = new Stopwatch();

    List<Chunk> orderedChunkList = new List();

    requestBuild(Chunk chunk, int i) {
        String key = "${chunk.x}:${chunk.z}@$i";
        if (_waitingForBuild.containsKey(key)) {
            return; // Already queued
        }
        _waitingForBuild[key] = true;
        _buildQueue.add(new _BuildJob(chunk, i));
    }

    static const int BUILD_LIMIT_MS = 8;
    int lastSort = 0;

    render(CanvasRenderer renderer) {
        stopwatch.reset();
        stopwatch.start();
        while (stopwatch.elapsedMilliseconds < BUILD_LIMIT_MS && toLoad.isNotEmpty) {
            addChunk(new CanvasChunk.fromBuffer(this, toLoad.removeLast(), 0));
        }
        bool run = stopwatch.elapsedMilliseconds < BUILD_LIMIT_MS;

        if (run && currentBuild != null) {
            var job = currentBuild;
            CanvasSnapshot snapshot = job.chunk.buildSection(job.i, currentSnapshot, stopwatch);
            currentBuild = null;
            currentSnapshot = null;
            if (snapshot != null) {
                currentBuild = job;
                currentSnapshot = snapshot;
                run = false;
            }
        }

        if (run) {
            if (_buildQueue.isNotEmpty && lastSort <= 0) {
                lastSort = 60;
                _buildQueue.sort(_queueCompare);
            }
            lastSort--;
            while (stopwatch.elapsedMilliseconds < BUILD_LIMIT_MS && _buildQueue.isNotEmpty) {
                var job = _buildQueue.removeLast();
                String key = "${job.chunk.x}:${job.chunk.z}@${job.i}";
                if (!_waitingForBuild.containsKey(key)) continue;
                _waitingForBuild.remove(key);
                CanvasSnapshot snapshot = job.chunk.buildSection(job.i, null, stopwatch);
                if (snapshot != null) {
                    currentBuild = job;
                    currentSnapshot = snapshot;
                    break;
                }
            }
        }
        stopwatch.stop();

        var ctx = renderer.ctx;

        ctx.save();
        ctx.translate(canvas.width ~/ 2, (canvas.height ~/ 2) - 12 * 256);
        orderedChunkList.forEach((v) {
            v.render(renderer);
        });
        ctx.restore();
    }

    @override
    addChunk(Chunk chunk) {
        super.addChunk(chunk);
        orderedChunkList.add(chunk);
        orderedChunkList.sort(_chunkSort);
    }

    @override
    removeChunk(int x, int z) {
        var chunk = getChunk(x, z);
        super.removeChunk(x, z);
        orderedChunkList.remove(chunk);
        String key = "${x}:${z}@";
        for (int i = 0; i < 16; i++) {
            _waitingForBuild.remove(key + i.toString());
        }
    }

    int _chunkSort(Chunk a, Chunk b) {
        if (a.z == b.z) {
            return a.x - b.x;
        }
        return b.z - a.z;
    }

    int _queueCompare(_BuildJob a, _BuildJob b) {
        double cameraX = (renderer as CanvasRenderer).cameraX;
        double cameraZ = (renderer as CanvasRenderer).cameraZ;
        num adx = (a.chunk.x * 16) + 8 - cameraX;
        num ady = (a.i * 16) + 8 - 6 * 16;
        num adz = (a.chunk.z * 16) + 8 - cameraZ;
        num distA = adx*adx + ady*ady + adz*adz;
        num bdx = (b.chunk.x * 16) + 8 - cameraX;
        num bdy = (b.i * 16) + 8 - 6 * 16;
        num bdz = (b.chunk.z * 16) + 8 - cameraZ;
        num distB = bdx*bdx + bdy*bdy + bdz*bdz;
        return (distB - distA).toInt();
    }
}

class CanvasChunk extends Chunk {

    CanvasChunk(int x, int z, World world) : super(x, z, world);

    CanvasChunk.fromBuffer(World world, ByteBuffer buffer, [int o = 0]) : super.fromBuffer(world, buffer, o);

    CanvasElement canvas;
    List<CanvasElement> parts = new List(16);

    render(CanvasRenderer renderer) {
        if (needsBuild) {
            needsBuild = false;
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null && section.needsBuild) {
                    section.needsBuild = false;
                    (world as CanvasWorld).requestBuild(this, i);
                }
            }
        }
        if (needsUpdate) {
            needsUpdate = false;
            if (canvas == null) {
                canvas = new CanvasElement();
                canvas.width = 256 * 2;
                canvas.height = 256 * 16 + 256;
            }
            var ctx = canvas.getContext("2d") as CanvasRenderingContext2D;
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            for (int i = 0; i < 16; i++) {
                var section = sections[i];
                if (section != null && parts[i] != null) {
                    ctx.drawImage(parts[i], 0, 256 * (15-i));
                }
            }

        }

        if (canvas != null) {
            renderer.ctx.drawImage(canvas, x * 256 + z * 256, x * 128 - z * 128);
        }
    }

    CanvasSnapshot buildSection(int i, CanvasSnapshot snapshot, Stopwatch stopwatch) {
        if (snapshot == null) {
            snapshot = new CanvasSnapshot();
        }
        var canvas = snapshot.canvas;
        var ctx = snapshot.ctx;

        ImageData data = snapshot.data == null ? ctx.getImageData(0, 0, canvas.width, canvas.height) : snapshot.data;

        for (int x = snapshot.x; x < 16; x++) {
            int sz = x == snapshot.x ? snapshot.z : 15;
            for (int z = sz; z >= 0; z--) {
                int sy = 0;
                if (z == snapshot.z){
                    sy = snapshot.y;
                    snapshot.z = -1;
                }
                for (int y = sy; y < 16; y++) {
                    Block block = getBlock(x, (i << 4) + y, z);
                    if (block.renderable)
                        block.renderCanvas(data, x, y, z, (i << 4) + y, this);

                    if (stopwatch.elapsedMilliseconds >= CanvasWorld.BUILD_LIMIT_MS) {
                        snapshot.x = x;
                        snapshot.y = y + 1;
                        snapshot.z = z;
                        snapshot.data = data;
                        return snapshot;
                    }
                }
            }
        }

        ctx.putImageData(data, 0, 0);
        parts[i] = snapshot.canvas;
        needsUpdate = true;
        return null;
    }

    @override
    unload(Renderer renderer) {

    }
}

class CanvasSnapshot {
    CanvasElement canvas = new CanvasElement();
    CanvasRenderingContext2D ctx;
    ImageData data;

    int x = 0;
    int y = 0;
    int z = 15;

    CanvasSnapshot() {
        canvas.width = 256 * 2;
        canvas.height = (256 * 2.5).toInt();
        ctx = canvas.getContext("2d");
    }
}