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

import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;

public final class Blocks {

    public static Block AIR;
    public static Block NULL_BLOCK;
    public static Block MISSING_BLOCK;

    /**
     * Called to load the static values after the blocks have been registered
     */
    public static void init(BlockRegistry blockRegistry) {
        AIR = blockRegistry.get("minecraft:air", new StateMap());
        NULL_BLOCK = blockRegistry.get("thinkmap:null");
        MISSING_BLOCK = blockRegistry.get("thinkmap:missing_block");
    }
}
