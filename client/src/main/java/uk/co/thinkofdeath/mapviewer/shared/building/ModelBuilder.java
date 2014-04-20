package uk.co.thinkofdeath.mapviewer.shared.building;

import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

public class ModelBuilder {

    private DynamicBuffer buffer;

    /**
     * Creates a model builder with provides helper methods to create models in a format ready to
     * uploaded as a WebGL buffer
     */
    public ModelBuilder() {
        buffer = new DynamicBuffer(80000);
    }

    /**
     * Adds a position attribute to the builder
     *
     * @param x
     *         The x position to add
     * @param y
     *         The y position to add
     * @param z
     *         The z position to add
     * @return This builder
     */
    public ModelBuilder position(float x, float y, float z) {
        buffer.addUnsignedShort((int) (x * 256 + 0.5 + 256));
        buffer.addUnsignedShort((int) (y * 256 + 0.5 + 256));
        buffer.addUnsignedShort((int) (z * 256 + 0.5 + 256));
        return this;
    }

    /**
     * Adds a colour attribute to the builder
     *
     * @param r
     *         The red component
     * @param g
     *         The green component
     * @param b
     *         The blue component
     * @return This builder
     */
    public ModelBuilder colour(int r, int g, int b) {
        return colour(r, g, b, 255);
    }


    /**
     * Adds a colour attribute to the builder
     *
     * @param r
     *         The red component
     * @param g
     *         The green component
     * @param b
     *         The blue component
     * @param a
     *         The alpha component
     * @return This builder
     */
    public ModelBuilder colour(int r, int g, int b, int a) {
        buffer.add(r);
        buffer.add(g);
        buffer.add(b);
        buffer.add(a);
        return this;
    }

    /**
     * Adds a texture id attribute to the builder
     *
     * @param start
     *         The start id of the texture
     * @param end
     *         The end id of the texture
     * @return This builder
     */
    public ModelBuilder textureId(int start, int end) {
        buffer.addUnsignedShort(start);
        buffer.addUnsignedShort(end);
        return this;
    }

    /**
     * Adds a texture position attribute to the builder
     *
     * @param x
     *         The x position to add
     * @param y
     *         The y position to add
     * @return This builder
     */
    public ModelBuilder texturePosition(float x, float y) {
        buffer.addUnsignedShort((int) (x * 256 + 0.5));
        buffer.addUnsignedShort((int) (y * 256 + 0.5));
        return this;
    }

    /**
     * Adds a lighting attribute to the builder
     *
     * @param emittedLight
     *         The emitted light from this vertex
     * @param skyLight
     *         The sky light from this vertex
     * @return The builder
     */
    public ModelBuilder lighting(int emittedLight, int skyLight) {
        buffer.add(emittedLight & 0xFF);
        buffer.add(skyLight & 0xFF);
        return this;
    }

    /**
     * Returns the backing typed array to be uploaded to a buffer
     *
     * @return The typed array
     */
    public TUint8Array toTypedArray() {
        return buffer.getArray();
    }
}
