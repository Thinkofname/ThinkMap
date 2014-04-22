package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.IntegerState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;

public class BlockSapling extends BlockFactory {

    public static final String TYPE = "type";
    public static final String STAGE = "stage";

    public BlockSapling() {
        addState(TYPE, new EnumState(Type.class));
        addState(STAGE, new IntegerState(0, 1));
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
        return new BlockImpl(this, states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(BlockSapling factory, StateMap states) {
            super(factory, states);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = BlockModels.createCross(getTexture(Face.FRONT));
            }
            return model;
        }

        @Override
        public String getTexture(Face face) {
            return "sapling_" + getState(TYPE);
        }

        @Override
        public int getLegacyData() {
            return this.<Type>getState(TYPE).ordinal() | (this.<Integer>getState(STAGE) * 8);
        }
    }
}
