part of map_viewer;

/**
 * DynamicUint8List is a resizable Uint8List with support
 * for other types (e.g. Float and UnsignedShort). The
 * Uint8Lists are pools to prevent unnecessary allocations and
 * this must be freed after use.
 */
class DynamicUint8List {

    Uint8List _buf;
    ByteData _data;
    int _offset = 0;

    /**
     * Create a DynamicUint8List with a starting [size]. If
     * the size is less than 16 then it will be set to 16.
     */
    DynamicUint8List(int size) {
        if (size < 16) size = 16;
        _buf = Uint8ListPool.getList(size);
        _data = new ByteData.view(_buf.buffer);
    }

    /**
     * Append one unsigned byte to the list
     */
    add(int val) {
        if (_offset >= _buf.length) {
            _resize();
        }
        _buf[_offset++] = val;
    }

    /**
     * Append one unsigned short to the list
     */
    addUnsignedShort(int val) {
        if (_offset + 1 >= _buf.length) {
            _resize();
        }
        _data.setUint16(_offset, val, Endianness.HOST_ENDIAN);
        _offset += 2;
    }

    /**
     * Append one 32 bit float to the list
     */
    addFloat(double val) {
        if (_offset + 3 >= _buf.length) {
            _resize();
        }
        _data.setFloat32(_offset, val, Endianness.HOST_ENDIAN);
        _offset += 4;
    }

    _resize() {
        var newList = Uint8ListPool.getList(_buf.length * 2);
        newList.setAll(0, _buf);
        Uint8ListPool.freeList(_buf);
        _buf = newList;
        _data = new ByteData.view(_buf.buffer);
    }

    /**
     * Release the internal buffer used by this list
     */
    free() {
        Uint8ListPool.freeList(_buf);
        _buf = null;
        _data = null;
    }

    /**
     * Returns a Uint8List containing the data created by
     * this list
     */
    Uint8List getList() {
        Uint8List ret = new Uint8List(_offset);
        ret.setRange(0, ret.length, _buf);
        return ret;
    }
}

/**
 * A simple pool of Uint8Lists
 */
class Uint8ListPool {

    static List<Uint8List> _freeLists = new List();

    /**
     * Gets or creates a Uint8List that is at least
     * [size] bytes
     */
    static Uint8List getList(int size) {
        var ret = _freeLists.firstWhere((e) => e.length >= size, orElse: () {
            print("Alloc $size");
            return new Uint8List(size);
        });
        _freeLists.remove(ret);
        return ret;
    }

    /**
     * Returns the [list] too the pool
     */
    static freeList(Uint8List list) {
        _freeLists.add(list);
    }
}