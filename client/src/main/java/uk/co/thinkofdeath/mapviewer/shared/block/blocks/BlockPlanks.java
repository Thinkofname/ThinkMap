package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockPlanks extends BlockFactory {

    public static final String TYPE = "type";

    private final Texture[] textures;

    public BlockPlanks(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(TYPE, new EnumState(Type.class));

        textures = new Texture[Type.values().length];
        for (Type type : Type.values()) {
            textures[type.ordinal()] = iMapViewer.getTexture("leaves_" + type);
        }
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
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockPlanks.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[this.<Type>getState(TYPE).ordinal()];
        }

        @Override
        public int getLegacyData() {
            return this.<Type>getState(TYPE).ordinal();
        }
    }
}
