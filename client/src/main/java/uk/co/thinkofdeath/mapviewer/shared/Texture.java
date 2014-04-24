package uk.co.thinkofdeath.mapviewer.shared;

public class Texture {

    private int start;
    private int end;

    public Texture(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the start position of the texture
     *
     * @return The start position
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end position of the texture
     *
     * @return The end position
     */
    public int getEnd() {
        return end;
    }
}
