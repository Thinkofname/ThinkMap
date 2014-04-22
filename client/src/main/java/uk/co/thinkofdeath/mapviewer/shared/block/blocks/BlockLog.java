package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

import java.util.Map;

public class BlockLog extends BlockFactory {

    public static final String VARIANT = "variant";
    public static final String AXIS = "axis";

    public BlockLog() {
        states.put(AXIS, new EnumState(Axis.class));
        states.put(VARIANT, new EnumState(Variant.class));
    }

    public static enum Axis {
        X(4),
        Y(0),
        Z(8),
        NONE(12);

        private final int legacy;

        Axis(int legacy) {
            this.legacy = legacy;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        /**
         * Returns the legacy value for this axis
         *
         * @return The legacy value
         */
        public int legacy() {
            return legacy;
        }
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
    protected Block createBlock(Map<String, Object> states) {
        return new BlockImpl(this, states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(BlockLog factory, Map<String, Object> states) {
            super(factory, states);
        }

        @Override
        public String getTexture(Face face) {
            switch (this.<Axis>getState(AXIS)) {
                case X:
                    if (face == Face.LEFT || face == Face.RIGHT) {
                        return "log_" + getState(VARIANT) + "_top";
                    }
                    break;
                case Y:
                    if (face == Face.TOP || face == Face.BOTTOM) {
                        return "log_" + getState(VARIANT) + "_top";
                    }
                    break;
                case Z:
                    if (face == Face.FRONT || face == Face.BACK) {
                        return "log_" + getState(VARIANT) + "_top";
                    }
                    break;
            }
            return "log_" + getState(VARIANT);
        }

        @Override
        public int getLegacyData() {
            return this.<Variant>getState(VARIANT).ordinal()
                    + this.<Axis>getState(AXIS).legacy();
        }
    }
}
