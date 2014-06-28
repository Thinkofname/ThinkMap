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

package uk.co.thinkofdeath.thinkcraft.shared.block.blocks;

import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.Colour;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;

public class BlockStainedGlassPane extends BlockGlassPane {

    public final StateKey<Colour> COLOUR = stateAllocator.alloc("color", new EnumState<>(Colour.class));

    private final Texture[] textures = new Texture[Colour.values().length * 2];

    public BlockStainedGlassPane(IMapViewer iMapViewer) {
        super(iMapViewer);

        int i = 0;
        for (Colour colour : Colour.values()) {
            textures[i] = mapViewer.getTexture("glass_" + colour.texture);
            textures[i + 1] = mapViewer.getTexture("glass_pane_top_" + colour.texture);
            i += 2;
        }
    }

    @Override
    protected Texture getTexture(BlockGlassPane.BlockImpl block, boolean top) {
        return textures[block.getState(COLOUR).ordinal() * 2 + (top ? 1 : 0)];
    }

    @Override
    protected int legacy(BlockImpl block) {
        return block.getState(COLOUR).ordinal();
    }

}
