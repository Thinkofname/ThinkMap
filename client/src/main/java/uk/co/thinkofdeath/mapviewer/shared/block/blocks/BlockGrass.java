package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;

public class BlockGrass extends BlockFactory {

    private final Texture grassTop;
    private final Texture grassSide;
    private final Texture dirt;

    public BlockGrass(IMapViewer iMapViewer) {
        super(iMapViewer);

        grassTop = iMapViewer.getTexture("grass_top");
        grassSide = iMapViewer.getTexture("grass_side");
        dirt = iMapViewer.getTexture("dirt");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockGrass.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
            switch (face) {
                case TOP:
                    return grassTop;
                case BOTTOM:
                    return dirt;
                default:
                    return grassSide;
            }
        }

        @Override
        public int getColour(Face face) {
            return face == Face.TOP ? 0xA7D389 : 0xFFFFFF;
        }
    }
}
