package uk.co.thinkofdeath.mapviewer.client.render;

import elemental.html.WebGLBuffer;
import uk.co.thinkofdeath.mapviewer.shared.model.SendableModel;

import java.util.List;

public class SortableRenderObject {

    private List<SendableModel> models;
    private final int x;
    private final int y;
    private final int z;

    public SortableRenderObject(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the BSPTree for this object
     *
     * @return The BSPTree
     */
    public List<SendableModel> getModels() {
        return models;
    }

    /**
     * Changes the models for this object
     *
     * @param models
     */
    public void setModels(List<SendableModel> models) {
        this.models = models;
        needResort = true;
    }

    /**
     * Gets the position of this object in the world
     *
     * @return The x position
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the position of this object in the world
     *
     * @return The y position
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the position of this object in the world
     *
     * @return The z position
     */
    public int getZ() {
        return z;
    }

    WebGLBuffer buffer;
    int count = 0;
    boolean needResort = true;
}
