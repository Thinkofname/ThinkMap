library packer;

import "dart:io";
import "dart:convert";
import "package:image/image.dart";
import "package:barback/barback.dart";
import "dart:async";

class ImagePacker extends Transformer {

  BarbackSettings settings;

  String host;

  ImagePacker(): settings = null;

  ImagePacker.asPlugin(this.settings) {

  }

  @override
  Future<bool> isPrimary(Asset input) {
    return new Future.value(input.id.path.contains("block_images") &&
        (input.id.path.endsWith(".png") || input.id.path.endsWith(".mcmeta")));
  }

  AssetId outputId;
  AssetId outputJson;

  bool hasRun = false;

  @override
  Future apply(Transform transform) {
    if (hasRun) return new Future.value(false);
    hasRun = true;

    var current = new Image(512, 512);
    int id = 0;
    int pos = 0;

    String path = transform.primaryInput.id.path;
    path = path.substring(0, path.lastIndexOf("/"));
    outputId = new AssetId(transform.primaryInput.id.package, path +
        "/blocks_${id}.png");
    outputJson = new AssetId(transform.primaryInput.id.package, path +
        "/blocks.json");
    Map<String, Map<String, int>> info = new Map();

    var inDir = new Directory(path);

    return inDir.list().where((e) => e.path.endsWith(".png")).listen((e) {
      var img = readPng(new File(e.path).readAsBytesSync());
      if (img.width != 16 || img.height != 16) {
        Map<String, Map<String, List<int>>> meta = new JsonDecoder(null
            ).convert(new File(e.path + ".mcmeta").readAsStringSync(encoding: UTF8));
        var frames = meta["animation"]["frames"];
        if (frames == null) {
          frames = new List();
          for (int i = 0; i < img.height ~/ img.width; i++) {
            frames.add(i);
          }
        }
        int frameTime = meta["animation"]["frametime"] != null ?
            meta["animation"]["frametime"] : 1;
        int start = (id * 32 * 32) + pos;
        for (int frame in frames) {
          for (int i = 0; i < frameTime; i++) {
            copyInto(current, img, dstX: (pos % 32) * 16, dstY: (pos ~/ 32) *
                16, srcX: 0, srcY: img.width * frame, srcH: img.width, blend: false);
            pos++;
            if (pos == 32 * 32) {
              transform.addOutput(new Asset.fromBytes(outputId, writePng(current
                  )));
              pos = 0;
              id++;
              outputId = new AssetId(transform.primaryInput.id.package, path +
                  "/blocks_${id}.png");
              current = new Image(512, 512);
            }
          }
        }
        Map<String, int> inf = new Map();
        inf["start"] = start;
        inf["end"] = (id * 32 * 32) + pos - 1;
        info[e.path.substring(e.path.lastIndexOf("\\") + 1).replaceAll(".png",
            "")] = inf;
        return;
      }
      copyInto(current, img, dstX: (pos % 32) * 16, dstY: (pos ~/ 32) * 16,
          blend: false);
      Map<String, int> inf = new Map();
      inf["start"] = (id * 32 * 32) + pos;
      inf["end"] = (id * 32 * 32) + pos;
      info[e.path.substring(e.path.lastIndexOf("\\") + 1).replaceAll(".png", ""
          )] = inf;
      pos++;
      if (pos == 32 * 32) {
        transform.addOutput(new Asset.fromBytes(outputId, writePng(current)));
        pos = 0;
        id++;
        outputId = new AssetId(transform.primaryInput.id.package, path +
            "/blocks_${id}.png");
        current = new Image(512, 512);
      }
    }).asFuture().then((v) {
      transform.addOutput(new Asset.fromBytes(outputId, writePng(current)));
      transform.addOutput(new Asset.fromString(outputJson, new JsonEncoder(null
          ).convert(info)));
      hasRun = false;
    });
  }
}

class ModelPacker extends Transformer {
  BarbackSettings settings;

  String host;

  ModelPacker(): settings = null;

  ModelPacker.asPlugin(this.settings) {

  }

  @override
  Future<bool> isPrimary(Asset input) {
    return new Future.value(input.id.path.contains("block_models") &&
      input.id.path.endsWith(".json"));
  }

  bool hasRun = false;

  @override
  Future apply(Transform transform) {
    if (hasRun) return new Future.value(false);
    hasRun = true;

    String path = transform.primaryInput.id.path;
    path = path.substring(0, path.lastIndexOf("/"));
    var outputJson = new AssetId(transform.primaryInput.id.package, path +
      "/models.json");
    Map info = new Map();

    var inDir = new Directory(path);

    return inDir.list().where((e) => e.path.endsWith(".json")).listen((e) {
      var name = e.path.substring(e.path.lastIndexOf("\\") + 1).replaceAll(".json", "");
      info[name] = JSON.decode(new File(e.path).readAsStringSync());
    }).asFuture().then((v) {
      transform.addOutput(new Asset.fromString(outputJson, JSON.encode(info)));
      hasRun = false;
    });
  }
}