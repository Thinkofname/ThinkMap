/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.mapviewer.shared.block;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;

import java.util.Map;

public class Block implements Model.RenderChecker {

    private final Map<String, Object> state;
    String plugin;
    String name;
    // The following should be mirrored in BlockFactory, BlockBuilder
    // and the constructor
    private boolean renderable;
    private boolean solid;
    private boolean collidable;
    private boolean transparent;
    private String texture;
    // Cache value since it doesn't change
    private String toString;
    private Model model;

    protected Block(BlockFactory factory, Map<String, Object> state) {
        this.state = state;
        renderable = factory.renderable;
        solid = factory.solid;
        collidable = factory.collidable;
        transparent = factory.transparent;
        texture = factory.texture;
    }

    /**
     * Returns the state for the given name
     *
     * @param name
     *         The state's name
     * @return The state's value or null
     */
    public Object getState(String name) {
        return state.get(name);
    }

    /**
     * Returns whether this block is renderable or not
     *
     * @return Whether this block is renderable
     */
    public boolean isRenderable() {
        return renderable;
    }

    /**
     * Returns whether this block is solid or not
     *
     * @return Whether this block is solid
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * Returns whether this block is collidable or not
     *
     * @return Whether this block is collidable
     */
    public boolean isCollidable() {
        return collidable;
    }

    /**
     * Returns whether this block is transparent or not
     *
     * @return Whether this block is transparent
     */
    public boolean isTransparent() {
        return transparent;
    }

    /**
     * Returns the texture used by this block (if it has one)
     *
     * @return The texture or null
     */
    public String getTexture() {
        return texture;
    }

    /**
     * Returns the old style data value
     *
     * @return The data value
     */
    public int getLegacyData() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (toString == null) {
            StringBuilder builder = new StringBuilder();
            builder.append(plugin);
            builder.append(':');
            builder.append(name);
            if (state.size() > 0) {
                builder.append('[');
                int i = 0;
                for (Map.Entry<String, Object> s : state.entrySet()) {
                    builder.append(s.getKey());
                    builder.append('=');
                    builder.append(s.getValue());
                    i++;
                    if (i != state.size()) {
                        builder.append(',');
                    }
                }
                builder.append(']');
            }
            toString = builder.toString();
        }
        return toString;
    }

    /**
     * Gets the model that should be rendered at the passed location, relative to the passed chunk
     *
     * @param chunk
     *         The chunk the positions are relative too
     * @param x
     *         The x position
     * @param y
     *         The y position
     * @param z
     *         The z position
     * @return The model
     */
    public Model getModel(Chunk chunk, int x, int y, int z) {
        if (model == null) {
            model = new Model();
            Face[] faces = {Face.TOP, Face.BOTTOM, Face.LEFT, Face.RIGHT, Face.FRONT, Face.BACK};
            for (int i = 0; i < faces.length; i++) {
                Face face = faces[i];
                // TODO: Colour support
                // TODO: Per a face texture support
                model.addFace(new ModelFace(face, getTexture(), 0, 0, 16, 16, ((i & 1) == 0) ? 16 : 0,
                        true));
            }
        }
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldRenderAgainst(Block other) {
        return !other.isSolid() && other != this; // FIXME: Missing allow self support
    }
}
