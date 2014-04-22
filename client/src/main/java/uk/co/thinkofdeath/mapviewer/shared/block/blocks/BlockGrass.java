package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;

public class BlockGrass extends BlockFactory {

    public BlockGrass() {

    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(this, states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(BlockGrass factory, StateMap states) {
            super(factory, states);
        }

        @Override
        public String getTexture(Face face) {
            switch (face) {
                case TOP:
                    return "grass_top";
                case BOTTOM:
                    return "dirt";
                default:
                    return "grass_side";
            }
        }

        @Override
        public int getColour(Face face) {
            return face == Face.TOP ? 0xA7D389 : 0xFFFFFF;
        }
    }
}
