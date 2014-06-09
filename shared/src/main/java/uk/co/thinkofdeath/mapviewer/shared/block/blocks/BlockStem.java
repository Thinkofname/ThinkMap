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

package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.ForEachIterator;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.IntegerState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateKey;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

public class BlockStem<T extends Block> extends BlockFactory {

    public final StateKey<Integer> GROWTH = stateAllocator.alloc("growth", new IntegerState(0, 7));
    public final StateKey<Connection> CONNECTED = stateAllocator.alloc("connected", new EnumState<>(Connection.class));

    private final Texture connected;
    private final Texture disconnected;

    private String target;

    public BlockStem(IMapViewer iMapViewer, String type, String target) {
        super(iMapViewer);
        this.target = target;

        connected = mapViewer.getTexture(type + "_stem_connected");
        disconnected = mapViewer.getTexture(type + "_stem_disconnected");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private static enum Connection {
        NO_CONNECTION(null, -1),
        WEST(Face.WEST, 0),
        EAST(Face.EAST, 2),
        NORTH(Face.NORTH, 1),
        SOUTH(Face.SOUTH, 3);

        private final Face facing;
        private final int rotation;

        Connection(Face face, int rotation) {
            facing = face;
            this.rotation = rotation;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockStem.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Connection connection = getState(CONNECTED);

                int height = 2 * (getState(GROWTH) + 1);

                int colour = 0x9DBA27;
                if (connection != Connection.NO_CONNECTION) {
                    height = 7;
                    colour = 0xF2FF00;
                }

                int r = (colour >> 16) & 0xFF;
                int g = (colour >> 8) & 0xFF;
                int b = (colour) & 0xFF;

                final int finalHeight = height;
                model.addFace(new ModelFace(Face.FRONT, disconnected, 0, 0, 16, height, 0)
                        .colour(r, g, b)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getX() == 1) {
                                    v.setZ(1);
                                }
                                v.setTextureY(v.getTextureY() + finalHeight / 16f);
                            }
                        }));
                model.addFace(new ModelFace(Face.BACK, disconnected, 0, 0, 16, height, 0)
                        .colour(r, g, b)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getX() == 1) {
                                    v.setZ(1);
                                }
                                v.setTextureY(v.getTextureY() + finalHeight / 16f);
                            }
                        }));
                model.addFace(new ModelFace(Face.FRONT, disconnected, 0, 0, 16, height, 0)
                        .colour(r, g, b)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getX() == 0) {
                                    v.setZ(1);
                                }
                                v.setTextureY(v.getTextureY() + finalHeight / 16f);
                            }
                        }));
                model.addFace(new ModelFace(Face.BACK, disconnected, 0, 0, 16, height, 0)
                        .colour(r, g, b)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getX() == 0) {
                                    v.setZ(1);
                                }
                                v.setTextureY(v.getTextureY() + finalHeight / 16f);
                            }
                        }));

                if (connection != Connection.NO_CONNECTION) {
                    model.addFace(new ModelFace(Face.FRONT, connected, 0, 0, 16, 16, 8)
                            .colour(r, g, b));
                    model.addFace(new ModelFace(Face.BACK, connected, 0, 0, 16, 16, 8)
                            .colour(r, g, b)
                            .forEach(new ForEachIterator<ModelVertex>() {
                                @Override
                                public void run(ModelVertex v) {
                                    v.setTextureX(1 - v.getTextureX());
                                }
                            }));

                    model.rotateY(connection.rotation * 90);
                }
            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap stateMap = new StateMap(state);
            stateMap.set(CONNECTED, Connection.NO_CONNECTION);
            for (Connection connection : Connection.values()) {
                if (connection.facing == null) continue;
                Face face = connection.facing;
                Block block = world.getBlock(
                        x + face.getOffsetX(),
                        y + face.getOffsetY(),
                        z + face.getOffsetZ());
                if (block.getFullName().equals(target)) {
                    stateMap.set(CONNECTED, connection);
                    break;
                }
            }
            return mapViewer.getBlockRegistry().get(fullName, stateMap);
        }

        @Override
        public int getLegacyData() {
            return getState(CONNECTED) != Connection.NO_CONNECTION ? -1 : getState(GROWTH);
        }
    }
}
