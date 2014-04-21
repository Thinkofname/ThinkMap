package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

import java.util.Map;

public class BlockSand extends BlockFactory {

    public static final String VARIANT = "variant";

    public BlockSand() {
        states.put(VARIANT, new EnumState(Variant.class));
    }

    public static enum Variant {
        DEFAULT,
        RED;

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
        public BlockImpl(BlockSand factory, Map<String, Object> states) {
            super(factory, states);
        }

        @Override
        public String getTexture(Face face) {
            return getState(VARIANT) == Variant.RED ? "red_sand" : "sand";
        }

        @Override
        public int getLegacyData() {
            return this.<Variant>getState(VARIANT).ordinal();
        }
    }
}
