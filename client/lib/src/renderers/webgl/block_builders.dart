part of map_viewer;

class BlockBuilder {

  DynamicUint8List _buffer;

  BlockBuilder() {
    _buffer = new DynamicUint8List(80000);
  }

  void position(num x, num y, num z) {
    _buffer.add(x.toInt());
    _buffer.add(y.toInt());
    _buffer.add(z.toInt());
  }

  void colour(int r, int g, int b) {
    _buffer.add(r);
    _buffer.add(g);
    _buffer.add(b);
  }

  void texId(int start, int end) {
    _buffer.addUnsignedShort(start);
    _buffer.addUnsignedShort(end);
  }

  void tex(num x, num y) {
    _buffer.add(x.toInt());
    _buffer.add(y.toInt());
  }

  void lighting(int light, int sky) {
    _buffer.add(light & 0xFF);
    _buffer.add(sky & 0xFF);
  }

  Uint8List toTypedList() {
    var ret = _buffer.getList();
    _buffer.free();
    return ret;
  }
}

class FloatBlockBuilder implements BlockBuilder {

  DynamicUint8List _buffer = new DynamicUint8List(80000);

  FloatBlockBuilder() {

  }

  @override
  void position(num x, num y, num z) {
    _buffer.addFloat(x.toDouble());
    _buffer.addFloat(y.toDouble());
    _buffer.addFloat(z.toDouble());
  }

  @override
  void colour(int r, int g, int b) {
    _buffer.add(r);
    _buffer.add(g);
    _buffer.add(b);
    //Padding
    _buffer.add(0);
  }

  @override
  void texId(int start, int end) {
    _buffer.addUnsignedShort(start);
    _buffer.addUnsignedShort(end);
  }

  @override
  void tex(num x, num y) {
    _buffer.addFloat(x.toDouble());
    _buffer.addFloat(y.toDouble());
  }

  @override
  void lighting(int light, int sky) {
    _buffer.add(light & 0xFF);
    _buffer.add(sky & 0xFF);
    //Padding
    _buffer.add(0);
    _buffer.add(0);
  }

  Uint8List toTypedList() {
    var ret = _buffer.getList();
    _buffer.free();
    return ret;
  }
}
