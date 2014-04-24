package uk.co.thinkofdeath.mapviewer.shared;

public class Texture {

    private final int start;
    private final int end;
    private final String name;

    public Texture(String name, int start, int end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the name of the texture
     *
     * @return The texture's name
     */
    public String getName() {
        return name;
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
