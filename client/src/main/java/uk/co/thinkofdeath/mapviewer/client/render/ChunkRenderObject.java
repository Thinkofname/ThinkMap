package uk.co.thinkofdeath.mapviewer.client.render;

import elemental.html.WebGLBuffer;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

public class ChunkRenderObject {

    final int x;
    final int y;
    final int z;

    WebGLBuffer buffer;
    TUint8Array data;
    int triangleCount;

    /**
     * Creates a new chunk render object used by the render to render a chunk section
     *
     * @param x
     *         The x position of the chunk
     * @param y
     *         The y position of the chunk
     * @param z
     *         The z position of the chunk
     */
    public ChunkRenderObject(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Loads/replaced the data used for this object
     *
     * @param data
     *         The new data to use
     */
    public void load(TUint8Array data) {
        this.data = data;
        triangleCount = data.length() / 20;
    }
}
