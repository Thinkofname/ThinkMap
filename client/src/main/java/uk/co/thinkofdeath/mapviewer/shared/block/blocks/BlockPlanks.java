package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

import java.util.Map;

public class BlockPlanks extends BlockFactory {

    public static final String TYPE = "type";

    public BlockPlanks() {
        states.put(TYPE, new EnumState(Type.class));
    }

    public static enum Type {
        OAK,
        SPRUCE,
        BIRCH,
        JUNGLE,
        ACACIA,
        BIG_OAK;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    @Override
    protected Block createBlock(Map<String, Object> states) {
        return new BlockImpl(this, states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(BlockPlanks factory, Map<String, Object> states) {
            super(factory, states);
        }

        @Override
        public String getTexture(Face face) {
            return "planks_" + getState(TYPE);
        }

        @Override
        public int getLegacyData() {
            return this.<Type>getState(TYPE).ordinal();
        }
    }
}
