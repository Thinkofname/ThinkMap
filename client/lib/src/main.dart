part of map_viewer;

// Current world
World world;
Connection connection;
CanvasElement canvas;
Renderer renderer;

List<ImageElement> blockTexturesRaw = new List();
Map<String, TextureInfo> blockTextureInfo = new Map();

/// Entry point
void startMapViewer() {
  var img = new ImageElement();
  blockTexturesRaw.add(img);
  HttpRequest req = new HttpRequest();
  HttpRequest req2 = new HttpRequest();

  Future.wait([req.onLoad.first, img.onLoad.first, req2.onLoad.first]).then((e) {
    Map<String, Map<String, int>> js = new JsonDecoder(null).convert(
        req.responseText);
    js.forEach((k, v) {
      blockTextureInfo[k] = new TextureInfo(v["start"], v["end"]);
    });
    Map mJs = JSON.decode(req2.responseText);
    mJs.forEach((k, v) {
      models[k] = new Model.fromMap(v);
    });
    start();
  });

  req.open("GET", "block_images/blocks.json", async: true);
  req.send();
  req2.open("GET", "block_models/models.json", async: true);
  req2.send();
  img.src = "block_images/blocks_0.png";
}

/// Called once everything is loaded
void start() {
  BlockRegistry.init();

  canvas = document.getElementById("main");

  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
  window.onResize.listen((e) {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    renderer.resize(canvas.width, canvas.height);
  });

  world = new WebGLWorld();
  renderer = new WebGLRenderer(canvas);
  window.requestAnimationFrame(draw);

  connection = new Connection("${window.location.hostname}:23333");

}

void draw(num unused) {
  renderer.draw();
  window.requestAnimationFrame(draw);
}
