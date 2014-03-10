package mapviewer.renderer;

/**
 * Stores the start and end index of a texture in the
 * texture map
 */
#if js
@:native("TI") // Save a bit of space
#end
class TextureInfo {
    /// Start index
    public var start : Int;
    /// End index
    public var end : Int;

    public function new(start : Int, end : Int) {
        this.start = start;
        this.end = end;
    }
}