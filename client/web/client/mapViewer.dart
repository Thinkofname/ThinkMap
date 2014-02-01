library mapViewer;

import "dart:html";
import "dart:web_gl";
import "dart:async";
import 'dart:typed_data';
import "dart:math";
import "dart:convert";
import "package:vector_math/vector_math.dart";

part "chunk.dart";
part "world.dart";
part "shaders.dart";
part "blocks.dart";
part "otherblocks.dart";
part "utils.dart";
part "rendering.dart";
part "box.dart";
part "blocks/grass.dart";
part "blocks/sapling.dart";

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