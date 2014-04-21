package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;

import java.util.Map;

public class BlockGrass extends BlockFactory {

    public BlockGrass() {

    }

    @Override
    protected Block createBlock(Map<String, Object> states) {
        return new BlockImpl(this, states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(BlockGrass factory, Map<String, Object> states) {
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
