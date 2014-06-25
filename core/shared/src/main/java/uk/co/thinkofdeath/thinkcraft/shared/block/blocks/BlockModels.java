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

import uk.co.thinkofdeath.thinkcraft.shared.Face;
import uk.co.thinkofdeath.thinkcraft.shared.ForEachIterator;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;

public class BlockModels {

    public static Model createCross(Texture texture) {
        return createCross(texture, 0xFFFFFF);
    }

    public static Model createCross(Texture texture, int colour) {
        Model model = new Model();
        int r = (colour >> 16) & 0xFF;
        int g = (colour >> 8) & 0xFF;
        int b = (colour) & 0xFF;

        model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 0)
                .colour(r, g, b)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        if (v.getX() == 1) {
                            v.setZ(1);
                        }
                    }
                }));
        model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 0)
                .colour(r, g, b)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        if (v.getX() == 1) {
                            v.setZ(1);
                        }
                    }
                }));
        model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 0)
                .colour(r, g, b)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        if (v.getX() == 0) {
                            v.setZ(1);
                        }
                    }
                }));
        model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 0)
                .colour(r, g, b)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        if (v.getX() == 0) {
                            v.setZ(1);
                        }
                    }
                }));
        return model;
    }

    public static Model createTorch(Texture texture) {
        Model model = new Model();
        model.addFace(new ModelFace(Face.LEFT, texture, 6, 0, 4, 11, 9)
                .setTextureSize(6, 5, 4, 11));
        model.addFace(new ModelFace(Face.RIGHT, texture, 6, 0, 4, 11, 7)
                .setTextureSize(6, 5, 4, 11));
        model.addFace(new ModelFace(Face.FRONT, texture, 6, 0, 4, 11, 9)
                .setTextureSize(6, 5, 4, 11));
        model.addFace(new ModelFace(Face.BACK, texture, 6, 0, 4, 11, 7)
                .setTextureSize(6, 5, 4, 11));
        model.addFace(new ModelFace(Face.TOP, texture, 7, 7, 2, 2, 10)
                .setTextureSize(7, 6, 2, 2));
        return model;
    }

    public static Model createFlat(Texture texture, int colour) {
        Model model = new Model();
        int r = (colour >> 16) & 0xFF;
        int g = (colour >> 8) & 0xFF;
        int b = colour & 0xFF;
        model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 16, 0)
                .colour(r, g, b));
        model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 16, 0)
                .colour(r, g, b));
        return model;
    }
}
