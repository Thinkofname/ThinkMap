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
import mapviewer.model.Model;
import mapviewer.model.ModelFace;
import mapviewer.world.Chunk;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.block.Face;
import mapviewer.renderer.TextureInfo;
import mapviewer.collision.Box;
import mapviewer.world.World;

using mapviewer.renderer.webgl.BuilderUtils;

/**
 * ...
 * @author Thinkofdeath
 */
class BlockSlab extends Block {
	
	@:chain public var top : Bool = false;
	@:chain public var textures : Map<String, String>;
	
	public function new() {
		super();
	}
	
	override public function collidesWith(box : Box, x : Int, y : Int, z : Int) : Bool {
		if (!collidable) return false;
		return box.checkBox(x, y + (top ? 0.5 : 0), z, 1.0, 0.5, 1.0);
	}	
	
	override public function getTexture(face : Face) : String {
		return texture == null ? textures[face.name] : texture;
	}
	
	override public function shouldRenderAgainst(block : Block) : Bool {
		return !block.solid;
	}
	
	override public function getModel(x : Int, y : Int, z : Int, world : World) : Model {
		if (model == null) {
			model = new Model();
			var offset : Float = top ? 8 : 0;	
			var i = 0;
			for (face in [Face.TOP, Face.BOTTOM, Face.LEFT, Face.RIGHT, Face.FRONT, Face.BACK]) {
				var colour = getColour(this, face);
				var r = (colour >> 16) & 0xFF;
				var g = (colour >> 8) & 0xFF;
				var b = colour & 0xFF;	
				model.faces.push(ModelFace.create(face, getTexture(face), 0, 
					face != Face.TOP && face != Face.BOTTOM ? offset : 0, 
					16, 
					face != Face.TOP && face != Face.BOTTOM ? 8 : 16, 
					i & 1 == 0 ? 
						(face != Face.TOP && face != Face.BOTTOM ? 16 : offset + 8) :
						(face != Face.TOP && face != Face.BOTTOM ? 0 : offset), 
						(top && face != Face.BOTTOM) || (!top && face != Face.TOP)) 
					.colour(r, g, b));
				i++;
			}
		}
		return super.getModel(x, y, z, world);
	}
	
	
}