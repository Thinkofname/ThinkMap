library mapViewer;

import "dart:html";
import "dart:web_gl";
import "dart:async";
import 'dart:typed_data';
import "dart:math";
import "dart:convert";
import "dart:js";
import "package:vector_math/vector_math.dart";

part "renderers/webglRenderer.dart";
part "renderers/canvasRenderer.dart";
part "chunk.dart";
part "world.dart";
part "shaders.dart";
part "blocks.dart";
part "otherblocks.dart";
part "utils.dart";
part "rendering.dart";
part "box.dart";
part "blocks/grass.dart";
part 'blocks/cross.dart';
part "blocks/vines.dart";
part "blockBuilders.dart";
part "network.dart";
part "pool.dart";

// Current world
World world;
Connection connection;
CanvasElement canvas;
Renderer renderer;

List<ImageElement> blockTexturesRaw = new List();
Map<String, TextureInfo> blockTextureInfo = new Map();

// Entry point
main() {
    var img = new ImageElement();
    blockTexturesRaw.add(img);
    HttpRequest req = new HttpRequest();

    Future.wait([req.onLoad.first, img.onLoad.first]).then((e){
        Map<String, Map<String, int>> js = new JsonDecoder(null).convert(req.responseText);
        js.forEach((k, v) {
            blockTextureInfo[k] = new TextureInfo(v["start"], v["end"]);
        });
        start();
    });

    req.open("GET", "imgs/blocks.json", async: true);
    req.send();
    img.src = "imgs/blocks_0.png";
}

// Called once everything is loaded
start() {
    // Get around a dart issue where it 'optimizes' out unused variables (all blocks)
    // which causes them to never be added to the maps so they can't be rendered
    Block._allBlocks;

    canvas = document.getElementById("main");

    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    window.onResize.listen((e) {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        renderer.resize(canvas.width, canvas.height);
    });

    if (window.location.search.contains("force2d")) {
        world = new CanvasWorld(); // TODO:
        renderer = new CanvasRenderer(canvas);
    } else {
        try {
            world = new WebGLWorld();
            renderer = new WebGLRenderer(canvas);
        } catch (e) {
            print(e);
            world = new CanvasWorld(); // TODO:
            renderer = new CanvasRenderer(canvas);
        }
    }

    window.requestAnimationFrame(draw);

    connection = new Connection("ws://${window.location.hostname}:23333/server");

}

draw(num unused) {
    renderer.draw();
    window.requestAnimationFrame(draw);
}