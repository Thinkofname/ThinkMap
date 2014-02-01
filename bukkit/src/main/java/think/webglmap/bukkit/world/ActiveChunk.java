package think.webglmap.bukkit.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActiveChunk {

    private final int x, z;

    @Getter
    private final short[][] blocks = new short[16][];
    @Getter
    private final byte[][] blockData = new byte[16][];
    @Getter
    private final byte[][] blockLight = new byte[16][];
    @Getter
    private final byte[][] skyLight = new byte[16][];

    private final int[] counters = new int[16];

    public void setBlock(int x, int y, int z, short block) {
        int section = y >> 4;
        if (blocks[section] == null) {
            if (block == 0) {
                return;
            }
            blocks[section] = new short[16 * 16 * 16];
        }
        int idx = x | (z << 4) | ((y&0xF) << 8);
        short oldBlock = blocks[section][idx];
        blocks[section][idx] = block;

        if (oldBlock == 0 && block != 0) {
            counters[section]++;
        } else if (oldBlock != 0 && block == 0) {
            counters[section]--;
        }

        if (counters[section] == 0) {
            blocks[section] = null;
        }
    }

    public short getBlock(int x, int y, int z) {
        int section = y >> 4;
        if (blocks[section] == null) {
            return 0;
        }
        return blocks[section][x | (z << 4) | ((y&0xF) << 8)];
    }


    public void setData(int x, int y, int z, byte data) {
        int section = y >> 4;
        if (blockData[section] == null) {
            if (counters[section] == 0) {
                return;
            } else {
                blockData[section] = new byte[16 * 16 * 16 / 2];
            }
        }
        int idx = (x << 1) | (z << 3) | ((y&0xF) << 7);
        blockData[section][idx] = (byte) (((blockData[section][idx]
                & (0xF << (((x + 1) & 1) * 4))) >> ((x & 1) * 4))
                | data << ((x & 1) * 4));

    }

    public byte getData(int x, int y, int z) {
        int section = y >> 4;
        if (blocks[section] == null) {
            return 0;
        }
        return (byte) ((blockData[section][(x << 1) | (z << 3) | ((y&0xF) << 7)]
                & (0xF << ((x & 1) * 4))) >> (((x + 1) & 1) * 4));
    }

    //TODO: Counter checking
    public void setBlockLight(int x, int y, int z, byte data) {
        int section = y >> 4;
        if (blockLight[section] == null) {
            if (counters[section] == 0) {
                return;
            } else {
                blockLight[section] = new byte[16 * 16 * 16 / 2];
            }
        }
        int idx = (x << 1) | (z << 3) | ((y&0xF) << 7);
        blockLight[section][idx] = (byte) (((blockLight[section][idx]
                & (0xF << (((x + 1) & 1) * 4))) >> ((x & 1) * 4))
                | data << ((x & 1) * 4));

    }

    public byte getBlockLight(int x, int y, int z) {
        int section = y >> 4;
        if (blockLight[section] == null) {
            return 0;
        }
        return (byte) ((blockLight[section][(x << 1) | (z << 3) | ((y&0xF) << 7)]
                & (0xF << ((x & 1) * 4))) >> (((x + 1) & 1) * 4));
    }

    //TODO: Counter checking
    public void setSkyLight(int x, int y, int z, byte data) {
        int section = y >> 4;
        if (skyLight[section] == null) {
            if (counters[section] == 0) {
                return;
            } else {
                skyLight[section] = new byte[16 * 16 * 16 / 2];
            }
        }
        int idx = (x << 1) | (z << 3) | ((y&0xF) << 7);
        skyLight[section][idx] = (byte) (((skyLight[section][idx]
                & (0xF << (((x + 1) & 1) * 4))) >> ((x & 1) * 4))
                | data << ((x & 1) * 4));

    }

    public byte getSkyLight(int x, int y, int z) {
        int section = y >> 4;
        if (skyLight[section] == null) {
            return 0;
        }
        return (byte) ((skyLight[section][(x << 1) | (z << 3) | ((y&0xF) << 7)]
                & (0xF << ((x & 1) * 4))) >> (((x + 1) & 1) * 4));
    }
}
