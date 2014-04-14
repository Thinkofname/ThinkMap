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

import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.block.blocks.BlockDirt;
import uk.co.thinkofdeath.mapviewer.shared.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class BlockRegistry {

    private final Logger logger;
    private IMapViewer mapViewer;
    private Map<String, Block> blockMap = new HashMap<>();

    /**
     * Creates a block registry which contains all the known blocks
     *
     * @param mapViewer The map viewer to use for access to other parts of the program
     */
    public BlockRegistry(IMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        logger = mapViewer.getLoggerFactory().getLogger("BlockRegistry");
    }

    /**
     * Adds the block to this registry
     *
     * @param plugin The plugin the block belongs to
     * @param name   The name of the block
     * @param blocks The blocks to add
     */
    private void register(String plugin, String name, BlockFactory blocks) {
        for (Block block : blocks.getBlocks()) {
            block.plugin = plugin;
            block.name = name;
            logger.debug(block.toString());
            blockMap.put(block.toString(), block);
        }
    }

    /**
     * Loads all known blocks
     */
    public void init() {
        // Vanilla blocks
        register("minecraft", "air", new BlockBuilder()
                .renderable(false)
                .collidable(false)
                .solid(false).create());
        register("minecraft", "stone", new BlockBuilder()
                .texture("stone")
                .create());
        //register("minecraft", "grass", new BlockBuilder()
        //    .create());
        register("minecraft", "dirt", new BlockBuilder(new BlockDirt())
                .create());
        register("minecraft", "cobblestone", new BlockBuilder()
                .texture("cobblestone")
                .create());


        // ThinkMap blocks
        register("thinkmap", "missing_block", new BlockBuilder()
                .texture("missing_texture")
                .create());
        register("thinkmap", "null", new BlockBuilder()
                .renderable(false)
                .create());
    }
}
