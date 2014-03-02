package mapviewer.block;

import mapviewer.utils.Chainable;
import mapviewer.block.BlockRegistry.BlockRegistrationEntry;

class Block implements Chainable {

    /// The registration entry for this block
    private var regBlock : BlockRegistrationEntry;

    /// Whether the block can actually be rendered
    @:chain public var renderable : Bool = true;
    /// Whether the block needs to be rendereed with other transparent objects
    @:chain public var transparent : Bool = false;
    /// Whether the block is solid
    @:chain public var solid : Bool = true;
    /// Whether the block has collisions
    @:chain public var collidable : Bool = true;
    /// Whether the block gives a shadow (AO)
    @:chain public var shade : Bool = true;
    /// The colour (tint) of the block
    @:chain public var colour : Int = 0xFFFFFF;
    /// Wether to force tinting for the block
    @:chain public var forceColour : Bool = false;
    /// The name of the texture for this block
    @:chain public var texture : String;
    /// Whether the block can render against itself
    @:chain public var allowSelf : Bool = false;
    /// The model the block should use (if any)
    @:chain public var model : Dynamic;

    public function new() {

    }

    public function toString() : String {
        return regBlock.toString();
    }
}