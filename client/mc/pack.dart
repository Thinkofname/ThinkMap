
import "dart:io";
import "dart:convert";
import "package:image/image.dart";

main() {
    var inDir = new Directory("blocks");

    var current = new Image(512, 512);
    int id = 0;
    int pos = 0;

    Map<String, Map<String, int>> info = new Map();

    inDir.list().where((e) => e.path.endsWith(".png")).listen((e){
        var img = readPng(new File(e.path).readAsBytesSync());
        if (img.width != 16 || img.height != 16) {
            print("Special Image: " + e.path);
            Map<String, Map<String, List<int>>> meta = new JsonDecoder(null).convert(new File(e.path + ".mcmeta").readAsStringSync(encoding: UTF8));
            var frames = meta["animation"]["frames"];
            if (frames == null) {
                frames = new List();
                for (int i = 0; i < img.height ~/ img.width; i++) {
                    frames.add(i);
                }
            }
            int frameTime = meta["animation"]["frametime"] != null ? meta["animation"]["frametime"] : 1;
            int start = (id * 32 * 32) + pos;
            for (int frame in frames) {
                for (int i = 0; i < frameTime; i++) {
                    copyInto(current, img, dstX: (pos % 32) * 16, dstY: (pos ~/ 32) * 16, srcX: 0, srcY: img.width * frame, srcH: img.width, blend:false);
                    pos++;
                    if (pos == 32 * 32) {
                        new File("out/blocks_" + id.toString() + ".png")
                            ..createSync(recursive: true)
                            ..writeAsBytesSync(writePng(current));
                        pos = 0;
                        id++;
                        current = new Image(512, 512);
                        print("New " + id.toString());
                    }
                }
            }
            Map<String, int> inf = new Map();
            inf["start"] = start;
            inf["end"] = (id * 32 * 32) + pos - 1;
            info[e.path.substring(e.path.lastIndexOf("\\") + 1).replaceAll(".png","")] = inf;
            return;
        }
        copyInto(current, img, dstX: (pos % 32) * 16, dstY: (pos ~/ 32) * 16, blend:false);
        Map<String, int> inf = new Map();
        inf["start"] = (id * 32 * 32) + pos;
        inf["end"] = (id * 32 * 32) + pos;
        info[e.path.substring(e.path.lastIndexOf("\\") + 1).replaceAll(".png","")] = inf;
        pos++;
        if (pos == 32 * 32) {
            new File("out/blocks_" + id.toString() + ".png")
                ..createSync(recursive: true)
                ..writeAsBytesSync(writePng(current));
            pos = 0;
            id++;
            current = new Image(512, 512);
            print("New " + id.toString());
        }
    }).onDone((){
        print("Final " + id.toString());
        new File("out/blocks_" + id.toString() + ".png")
            ..createSync(recursive: true)
            ..writeAsBytesSync(writePng(current));
        new File("out/blocks.json")
            ..createSync(recursive: true)
            ..writeAsStringSync(new JsonEncoder(null).convert(info));
    });
}