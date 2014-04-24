package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.ForEachIterator;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;

public class BlockBed extends BlockFactory {

    private static final String FACING = "facing";
    private static final String OCCUPIED = "occupied";
    private static final String PART = "part";

    private final Texture bedHeadTop;
    private final Texture bedHeadEnd;
    private final Texture bedHeadSide;

    private final Texture bedFeetTop;
    private final Texture bedFeetEnd;
    private final Texture bedFeetSide;


    public BlockBed(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(FACING, new EnumState(Facing.class));
        addState(OCCUPIED, new BooleanState());
        addState(PART, new EnumState(Part.class));

        bedHeadTop = iMapViewer.getTexture("bed_head_top");
        bedHeadEnd = iMapViewer.getTexture("bed_head_end");
        bedHeadSide = iMapViewer.getTexture("bed_head_side");

        bedFeetTop = iMapViewer.getTexture("bed_feet_top");
        bedFeetEnd = iMapViewer.getTexture("bed_feet_end");
        bedFeetSide = iMapViewer.getTexture("bed_feet_side");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Facing {
        SOUTH,
        WEST,
        NORTH,
        EAST;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static enum Part {
        HEAD,
        FOOT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockBed.this, states);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();
                if (getState(PART) == Part.HEAD) {
                    model.addFace(new ModelFace(Face.TOP, bedHeadTop, 0, 0, 16, 16,
                            9));
                    model.addFace(new ModelFace(Face.LEFT, bedHeadEnd, 0, 0, 16, 9, 16, true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.FRONT, bedHeadSide, 0, 0, 16, 9, 16,
                            true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.BACK, bedHeadSide, 0, 0, 16, 9, 0, true)
                            .setTextureSize(0, 7, 16, 9)
                            .forEach(new ForEachIterator<ModelVertex>() {
                                @Override
                                public void run(ModelVertex v) {
                                    v.setTextureX(1 - v.getTextureX());
                                }
                            }));

                } else {
                    model.addFace(new ModelFace(Face.TOP, bedFeetTop, 0, 0, 16, 16, 9));
                    model.addFace(new ModelFace(Face.RIGHT, bedFeetEnd, 0, 0, 16, 9, 0, true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.FRONT, bedFeetSide, 0, 0, 16, 9, 16,
                            true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.BACK, bedFeetSide, 0, 0, 16, 9, 0, true)
                            .setTextureSize(0, 7, 16, 9)
                            .forEach(new ForEachIterator<ModelVertex>() {
                                @Override
                                public void run(ModelVertex v) {
                                    v.setTextureX(1 - v.getTextureX());
                                }
                            }));
                }
                model.rotateY(90 + this.<Facing>getState(FACING).ordinal() * 90);
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = this.<Facing>getState(FACING).ordinal();
            if (this.<Boolean>getState(OCCUPIED)) {
                val |= 0x4;
            }
            if (getState(PART) == Part.HEAD) {
                val |= 0x8;
            }
            return val;
        }
    }
}
