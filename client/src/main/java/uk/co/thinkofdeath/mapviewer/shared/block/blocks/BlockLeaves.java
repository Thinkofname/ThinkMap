package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockLeaves extends BlockFactory {

    public static final String VARIANT = "variant";
    public static final String CHECK_DECAY = "check_decay";
    public static final String DECAYABLE = "decayable";

    public BlockLeaves() {
        addState(CHECK_DECAY, new BooleanState());
        addState(DECAYABLE, new BooleanState());
        addState(VARIANT, new EnumState(Variant.class));
    }

    public static enum Variant {
        OAK,
        SPRUCE,
        BIRCH,
        JUNGLE;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(this, states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(BlockLeaves factory, StateMap states) {
            super(factory, states);
        }

        @Override
        public String getTexture(Face face) {
            return "leaves_" + getState(VARIANT);
        }

        @Override
        public int getLegacyData() {
            int val = this.<Variant>getState(VARIANT).ordinal();
            if (!this.<Boolean>getState(DECAYABLE)) {
                val |= 0x4;
            }
            if (this.<Boolean>getState(CHECK_DECAY)) {
                val |= 0x8;
            }
            return val;
        }

        @Override
        public int getColour(Face face) {
            return 0xA7D389;
        }
    }
}
