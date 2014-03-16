/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mapviewer.block;

import mapviewer.block.Face;
import mapviewer.collision.Box;
import mapviewer.model.Model;
import mapviewer.model.ModelFace;
import mapviewer.renderer.LightInfo;
import mapviewer.renderer.TextureInfo;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.utils.Chainable;
import mapviewer.block.BlockRegistry.BlockRegistrationEntry;
import mapviewer.world.Chunk;

using mapviewer.renderer.webgl.BuilderUtils;

class Block implements Chainable {

    /// The registration entry for this block
    public var regBlock : BlockRegistrationEntry;

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
    @:chain public var model : mapviewer.model.Model;

    public function new() {
		
    }
	
	/**
	 * Returns whether the block at the coordinates collides with
	 * the box
	 */
	public function collidesWith(box : Box, x : Int, y : Int, z : Int) : Bool {
		if (!collidable) return false;
		return box.checkBox(x, y, z, 1.0, 1.0, 1.0);
	}	

    /**
     * Returns whether this should render its side against the block
     */
	public function shouldRenderAgainst(block : Block) : Bool {
		return !block.solid && (allowSelf || block != this);
	}
	
	private static var defModel : Model;
	private var cachedShouldRenderAgainst : Block -> Bool;  // For performance
	
	public function getModel() : Model {
		if (model == null) {
			model = new Model();
			var i = 0;
			for (face in [Face.TOP, Face.BOTTOM, Face.LEFT, Face.RIGHT, Face.FRONT, Face.BACK]) {
				var colour = getColour(this, face);
				var r = (colour >> 16) & 0xFF;
				var g = (colour >> 8) & 0xFF;
				var b = colour & 0xFF;	
				model.faces.push(ModelFace.create(face, getTexture(face), 0, 0, 16, 16, i & 1 == 0 ? 16 : 0, true)
					.colour(r, g, b));
				i++;
			}
			cachedShouldRenderAgainst = shouldRenderAgainst;
		}
		return model;
	}
	
    /**
     * Renders the block at the coordinates relative to the
     * chunk
     */
	public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {
		if (model == null) {
			model = getModel();
		}		
		model.render(builder, x, y, z, chunk, cachedShouldRenderAgainst);
	}
	
	/**
	 * Returns the texture for the face
	 */
	public function getTexture(face : Face) : String {
		return texture;
	}
	
	@:chain public var getColour : Block -> Face -> Int = function(self : Block, face : Face) : Int {
		return self.forceColour ? self.colour : 0xFFFFFF;
	};

    public function toString() : String {
        return regBlock.toString();
    }
}