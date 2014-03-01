part of map_viewer;

class IsolateWorld extends World {


  Chunk newChunk() => null;

  int _queueCompare(_BuildJob a, _BuildJob b) => 1;
}