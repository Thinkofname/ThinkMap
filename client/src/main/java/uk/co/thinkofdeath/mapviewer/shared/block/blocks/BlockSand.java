package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockSand extends BlockFactory {

    public static final String VARIANT = "variant";

    private final Texture sand;
    private final Texture redSand;

    public BlockSand(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(VARIANT, new EnumState(Variant.class));
        sand = iMapViewer.getTexture("sand");
        redSand = iMapViewer.getTexture("red_sand");
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
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockSand.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
            return getState(VARIANT) == Variant.RED ? redSand : sand;
        }

        @Override
        public int getLegacyData() {
            return this.<Variant>getState(VARIANT).ordinal();
        }
    }
}
