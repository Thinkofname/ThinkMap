package uk.co.thinkofdeath.mapviewer.client.render;

import elemental.html.WebGLBuffer;
import uk.co.thinkofdeath.mapviewer.shared.model.SendableModel;

import java.util.List;

public abstract class SortableRenderObject {

    /**
     * Gets the BSPTree for this object
     *
     * @return The BSPTree
     */
    public abstract List<SendableModel> getModels();

    /**
     * Gets the position of this object in the world
     *
     * @return The x position
     */
    public abstract int getX();

    /**
     * Gets the position of this object in the world
     *
     * @return The y position
     */
    public abstract int getY();

    /**
     * Gets the position of this object in the world
     *
     * @return The z position
     */
    public abstract int getZ();

    WebGLBuffer buffer;
    int count = 0;
    boolean needResort = true;
}
