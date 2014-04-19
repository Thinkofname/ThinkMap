package uk.co.thinkofdeath.mapviewer.shared.building;

import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.mapviewer.shared.support.DataReader;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

// TODO: Think about readding the pool
public class DynamicBuffer {

    private static final boolean IS_LITTLE_ENDIAN =
            (DataReader.create(createEndianTestBuffer()).getInt8(0) == 1);

    private TUint8Array buffer;
    private DataReader dataReader;
    private int offset = 0;

    /**
     * Creates a DynamicBuffer which resizes as it needs more space. The endianness of the buffer is
     * that of the current system. The starting start has a minimum value of 16.
     *
     * @param size
     *         The starting size of the buffer
     */
    public DynamicBuffer(int size) {
        if (size < 16) size = 16;
        buffer = TUint8Array.create(size);
        dataReader = DataReader.create(buffer.getBuffer());
    }

    /**
     * Adds a single byte to the buffer
     *
     * @param val
     *         The byte to the buffer
     */
    public void add(int val) {
        if (offset >= buffer.length()) {
            resize();
        }
        buffer.set(offset++, val);
    }

    /**
     * Adds a unsigned short to the buffer
     *
     * @param val
     *         The short to add
     */
    public void addUnsignedShort(int val) {
        if (offset + 1 >= buffer.length()) {
            resize();
        }
        dataReader.setUint16(offset, val, IS_LITTLE_ENDIAN);
        offset += 2;
    }

    /**
     * Adds a float to the buffer
     *
     * @param val
     *         The float to add
     */
    public void addFloat(float val) {
        if (offset + 3 >= buffer.length()) {
            resize();
        }
        dataReader.setFloat32(offset, val, IS_LITTLE_ENDIAN);
        offset += 4;
    }

    // Doubles the size of the buffer
    private void resize() {
        TUint8Array newBuffer = TUint8Array.create(buffer.length() * 2);
        newBuffer.set(buffer);
        buffer = newBuffer;
        dataReader = DataReader.create(buffer.getBuffer());
    }

    /**
     * Returns a view into the buffer sized at the final size of the buffer
     *
     * @return The view into the array
     */
    public TUint8Array getArray() {
        return buffer.subarray(0, offset);
    }

    // Used by IS_LITTLE_ENDIAN
    private static native ArrayBuffer createEndianTestBuffer()/*-{
        return new Uint16Array([1]).buffer;
    }-*/;
}
