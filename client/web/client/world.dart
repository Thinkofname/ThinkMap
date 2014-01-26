part of mapViewer;

class World {

  Map<int, Chunk> chunks = new Map();

  int currentTime = 6000;

  World() {
    new Timer.periodic(new Duration(milliseconds: 1000~/20), tick);
  }

  tick(Timer timer) {
    currentTime += 1;
    currentTime %= 24000;
  }
}
