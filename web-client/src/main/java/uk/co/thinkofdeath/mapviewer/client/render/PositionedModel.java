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

package uk.co.thinkofdeath.mapviewer.client.render;

import uk.co.thinkofdeath.mapviewer.client.MapViewer;
import uk.co.thinkofdeath.mapviewer.client.world.ClientChunk;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.SendableModel;

public class PositionedModel {
    private final int x;
    private final int y;
    private final int z;
    private final Model model;
    private final Block block;
    private final ClientChunk chunk;

    public PositionedModel(MapViewer mapViewer, ClientChunk chunk, SendableModel sendableModel) {
        model = new Model(sendableModel, mapViewer);
        x = sendableModel.getX();
        y = sendableModel.getY();
        z = sendableModel.getZ();
        block = sendableModel.getOwner(mapViewer);
        this.chunk = chunk;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Model getModel() {
        return model;
    }

    public Block getBlock() {
        return block;
    }

    public ClientChunk getChunk() {
        return chunk;
    }
}
