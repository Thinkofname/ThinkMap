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

public class BlockBuilder {

    private BlockFactory block;

    /**
     * Creates a block builder to allow chainable block creation
     */
    public BlockBuilder() {
        this(new BlockFactory());
    }

    public BlockBuilder(BlockFactory factory) {
        block = factory;
    }

    /**
     * Returns the created block
     *
     * @return The created block
     */
    public BlockFactory create() {
        return block;
    }

    /**
     * Sets whether this block is renderable or not
     *
     * @param renderable The renderable state for this block
     * @return This builder
     */
    public BlockBuilder renderable(boolean renderable) {
        block.renderable = renderable;
        return this;
    }

    /**
     * Sets whether this block is solid or not
     *
     * @param solid The solid state for this block
     * @return This builder
     */
    public BlockBuilder solid(boolean solid) {
        block.solid = solid;
        return this;
    }

    /**
     * Sets whether this block has collisions or not
     *
     * @param collidable The collidable state for this block
     * @return This builder
     */
    public BlockBuilder collidable(boolean collidable) {
        block.collidable = collidable;
        return this;
    }

    /**
     * Sets whether this block is transparent or not
     *
     * @param transparent The transparent state for this block
     * @return This builder
     */
    public BlockBuilder transparent(boolean transparent) {
        block.transparent = transparent;
        return this;
    }

    /**
     * Sets the texture to be used by the block if the default
     * model is used
     *
     * @param texture The block texture
     * @return This builder
     */
    public BlockBuilder texture(String texture) {
        block.texture = texture;
        return this;
    }

    /**
     * Adds a state to this block
     *
     * @param name Name of the state
     * @param blockState The state to add
     * @return This builder
     */
    public BlockBuilder state(String name, BlockState blockState) {
        block.states.put(name, blockState);
        return this;
    }
}
