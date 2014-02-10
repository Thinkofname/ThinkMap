part of mapViewer;

class BlockBuilder {

//    List<int> _buffer = new List();
    DynamicUint8List _buffer;

    BlockBuilder() {
        _buffer = new DynamicUint8List(80000);
    }

    position(num x, num y, num z) {
        _buffer.add(x);
        _buffer.add(y);
        _buffer.add(z);
    }

    colour(int r, int g, int b) {
        _buffer.add(r);
        _buffer.add(g);
        _buffer.add(b);
    }

    texId(int start, int end) {
        _buffer.addShort(start);
        _buffer.addShort(end);
    }

    tex(num x, num y) {
        _buffer.add(x);
        _buffer.add(y);
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

    position(num x, num y, num z) {
        _buffer.addFloat(x);
        _buffer.addFloat(y);
        _buffer.addFloat(z);
    }

    colour(int r, int g, int b) {
        _buffer.add(r);
        _buffer.add(g);
        _buffer.add(b);
        //Padding
        _buffer.add(0);
    }

    texId(int start, int end) {
        _buffer.addShort(start);
        _buffer.addShort(end);
    }

    tex(num x, num y) {
        _buffer.addFloat(x);
        _buffer.addFloat(y);
    }

    Uint8List toTypedList() {
        var ret = _buffer.getList();
        _buffer.free();
        return ret;
    }
}