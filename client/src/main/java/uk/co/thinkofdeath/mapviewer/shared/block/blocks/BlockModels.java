package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.ForEachIterator;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;

class BlockModels {

    public static Model createCross(String texture) {
        return createCross(texture, 0xFFFFFF);
    }

    public static Model createCross(String texture, int colour) {
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
}
