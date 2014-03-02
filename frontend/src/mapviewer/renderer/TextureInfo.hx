package mapviewer.renderer;

/**
 * Stores the start and end index of a texture in the
 * texture map
 */
class TextureInfo {
    /// Start index
    private var start : Int;
    /// End index
    private var end : Int;

    public function new(start : Int, end : Int) {
        this.start = start;
        this.end = end;
    }
}