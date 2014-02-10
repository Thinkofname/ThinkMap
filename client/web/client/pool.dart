part of mapViewer;

class DynamicUint8List {

    Uint8List buf;
    ByteData data;
    int offset = 0;

    DynamicUint8List(int size) {
        buf = Uint8ListPool.getList(size);
        data = new ByteData.view(buf.buffer);
    }

    add(int val) {
        if (offset >= buf.length) {
            resize();
        }
        buf[offset++] = val;
    }

    addShort(int val) {
        if (offset + 1 >= buf.length) {
            resize();
        }
        data.setUint16(offset, val, Endianness.HOST_ENDIAN);
        offset += 2;
    }

    addFloat(double val) {
        if (offset + 3 >= buf.length) {
            resize();
        }
        data.setFloat32(offset, val, Endianness.HOST_ENDIAN);
        offset += 4;
    }

    resize() {
        var newList = Uint8ListPool.getList(buf.length * 2);
        newList.setAll(0, buf);
        Uint8ListPool.freeList(buf);
        buf = newList;
        data = new ByteData.view(buf.buffer);
        print("Resize to ${buf.length}");
    }

    free() {
        Uint8ListPool.freeList(buf);
        buf = null;
        data = null;
    }

    Uint8List getList() {
        Uint8List ret = new Uint8List(offset);
        ret.setRange(0, ret.length, buf);
        return ret;
    }
}

class Uint8ListPool {

    static List<Uint8List> freeLists = new List();

    static Uint8List getList(int size) {
        var ret = freeLists.firstWhere((e) => e.length >= size, orElse: () => new Uint8List(size));
        freeLists.remove(ret);
        return ret;
    }

    static freeList(Uint8List list) {
        freeLists.add(list);
    }
}