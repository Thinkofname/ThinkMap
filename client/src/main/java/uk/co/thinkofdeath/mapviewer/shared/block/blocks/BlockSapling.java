package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.IntegerState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;

public class BlockSapling extends BlockFactory {

    public static final String TYPE = "type";
    public static final String STAGE = "stage";

    private final Texture[] textures;

    public BlockSapling(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(TYPE, new EnumState(Type.class));
        addState(STAGE, new IntegerState(0, 1));

        textures = new Texture[Type.values().length];
        for (Type type : Type.values()) {
            textures[type.ordinal()] = iMapViewer.getTexture("sapling_" + type);
        }
    }

    public static enum Type {
        OAK,
        SPRUCE,
        BIRCH,
        JUNGLE,
        ACACIA,
        BIG_OAK("roofed_oak");

        private final String name;

        Type() {
            name = name().toLowerCase();
        }

        Type(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name;
        }
    }


    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockSapling.this, states);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = BlockModels.createCross(getTexture(Face.FRONT));
            }
            return model;
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[this.<Type>getState(TYPE).ordinal()];
        }

        @Override
        public int getLegacyData() {
            return this.<Type>getState(TYPE).ordinal() | (this.<Integer>getState(STAGE) * 8);
        }
    }
}
