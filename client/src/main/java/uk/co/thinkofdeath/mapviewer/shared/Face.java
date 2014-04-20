package uk.co.thinkofdeath.mapviewer.shared;

public enum Face {
    /**
     * Top face
     */
    TOP(0, 1, 0),
    /**
     * Bottom face
     */
    BOTTOM(0, -1, 0),
    /**
     * Left face
     */
    LEFT(1, 0, 0),
    /**
     * Right face
     */
    RIGHT(-1, 0, 0),
    /**
     * Front face
     */
    FRONT(0, 0, 1),
    /**
     * Back face
     */
    BACK(0, 0, -1);

    private final String name;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;

    Face(int offsetX, int offsetY, int offsetZ) {
        this.name = name().toLowerCase();
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    /**
     * Returns the readable name of this facing direction
     *
     * @return The readable name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the offset x of the face
     *
     * @return The x offset
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Returns the offset y of the face
     *
     * @return The y offset
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Returns the offset z of the face
     *
     * @return The z offset
     */
    public int getOffsetZ() {
        return offsetZ;
    }
}
