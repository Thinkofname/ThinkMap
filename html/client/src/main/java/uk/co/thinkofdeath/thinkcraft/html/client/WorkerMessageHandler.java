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

package uk.co.thinkofdeath.thinkcraft.html.client;

import uk.co.thinkofdeath.thinkcraft.html.client.world.ClientChunk;
import uk.co.thinkofdeath.thinkcraft.html.client.world.ClientWorld;
import uk.co.thinkofdeath.thinkcraft.shared.worker.*;

public class WorkerMessageHandler implements MessageHandler {
    private final MapViewer mapViewer;

    public WorkerMessageHandler(MapViewer mapViewer) {
        this.mapViewer = mapViewer;
    }

    @Override
    public void handle(ChunkBuildMessage chunkBuildMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(ChunkUnloadMessage chunkUnloadMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(ChunkBuildReply chunkBuildReply) {
        ClientChunk chunk = (ClientChunk) mapViewer.getWorld().getChunk(chunkBuildReply.getX(), chunkBuildReply.getZ());
        if (chunk != null) {
            if (chunk.checkAndSetBuildNumber(chunkBuildReply.getBuildNumber(),
                    chunkBuildReply.getSectionNumber())) {
                chunk.updateAccess(
                        chunkBuildReply.getSectionNumber(),
                        chunkBuildReply.getAccessData());
                chunk.fillBuffer(
                        chunkBuildReply.getSectionNumber(),
                        chunkBuildReply.getData(),
                        chunkBuildReply.getSender());

                chunk.setTransparentModels(
                        chunkBuildReply.getSectionNumber(),
                        chunkBuildReply.getTrans(),
                        chunkBuildReply.getTransData(),
                        chunkBuildReply.getSender());

            }
            for (String usedTexture : chunkBuildReply.getUsedTextures()) {
                mapViewer.getTextureLoader().loadBlockTexture(mapViewer.getBlockTexture(usedTexture));
            }
        }
    }

    @Override
    public void handle(ChunkLoadMessage chunkLoadMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(ChunkLoadedMessage chunkLoadedMessage) {
        ClientWorld world = (ClientWorld) mapViewer.getWorld();
        world.addChunk(new ClientChunk(world, chunkLoadedMessage));
    }

    @Override
    public void handle(ClientSettingsMessage clientSettingsMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(TextureMessage textureMessage) {
        throw new UnsupportedOperationException();
    }
}
