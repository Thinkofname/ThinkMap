part of mapViewer;

class BlockBuilder {

    DynamicUint8List _buffer;

    BlockBuilder() {
        _buffer = new DynamicUint8List(80000);
    }

    position(num x, num y, num z) {
        _buffer.add(x.toInt());
        _buffer.add(y.toInt());
        _buffer.add(z.toInt());
    }

    colour(int r, int g, int b) {
        _buffer.add(r);
        _buffer.add(g);
        _buffer.add(b);
    }

    texId(int start, int end) {
        _buffer.addUnsignedShort(start);
        _buffer.addUnsignedShort(end);
    }

    tex(num x, num y) {
        _buffer.add(x.toInt());
        _buffer.add(y.toInt());
    }

    lighting(int light, int sky) {
        _buffer.add(light);
        _buffer.add(sky);
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
        _buffer.addFloat(x.toDouble());
        _buffer.addFloat(y.toDouble());
        _buffer.addFloat(z.toDouble());
    }

    colour(int r, int g, int b) {
        _buffer.add(r);
        _buffer.add(g);
        _buffer.add(b);
        //Padding
        _buffer.add(0);
    }

    texId(int start, int end) {
        _buffer.addUnsignedShort(start);
        _buffer.addUnsignedShort(end);
    }

    tex(num x, num y) {
        _buffer.addFloat(x.toDouble());
        _buffer.addFloat(y.toDouble());
    }

    lighting(int light, int sky) {
        _buffer.add(light);
        _buffer.add(sky);
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