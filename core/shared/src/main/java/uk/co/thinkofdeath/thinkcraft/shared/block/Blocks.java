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

package uk.co.thinkofdeath.thinkcraft.shared.block;

import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public final class Blocks {

    private static Block AIR;
    private static Block NULL_BLOCK;
    private static Block MISSING_BLOCK;

    /**
     * Called to load the static values after the blocks have been registered
     */
    static void init(BlockRegistry blockRegistry) {
        AIR = blockRegistry.get("minecraft:air", new StateMap());
        NULL_BLOCK = blockRegistry.get("thinkmap:null");
        MISSING_BLOCK = blockRegistry.get("thinkmap:missing_block");
    }


    public static Block AIR() {
        return AIR;
    }

    public static Block NULL_BLOCK() {
        return NULL_BLOCK;
    }

    public static Block MISSING_BLOCK() {
        return MISSING_BLOCK;
    }
}
