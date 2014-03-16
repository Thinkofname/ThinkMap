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
import mapviewer.world.Chunk;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.block.Face;
import mapviewer.block.Block.blockLightingRegion;
import mapviewer.renderer.TextureInfo;
import mapviewer.collision.Box;

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
	
	override public function getTexture(face : Face) : TextureInfo {
		return Main.blockTextureInfo[texture == null ? textures[face.name] :texture];
	}
	
	override public function shouldRenderAgainst(block : Block) : Bool {
		return !block.solid;
	}
	
	override public function render(builder:BlockBuilder, x:Int, y:Int, z:Int, chunk:Chunk) {		
		var offset : Float = top ? 0.5 : 0;		
			
		if (!top || shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y + 1, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.TOP);
			var colour = getColour(this, Face.TOP);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x, y + 1, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y + 1, (chunk.z << 4) + z);
			
			builder.addFaceTop(x, y + offset + 0.5, z, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x    , y + 1, z - 1, x + 2, y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x    , y + 1, z    , x + 2, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y + 1, z    , x + 1, y + 2, z + 2, light, sky),
				texture);
		}
		
		if (top || shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y - 1, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.BOTTOM);
			var colour = getColour(this, Face.BOTTOM);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x, y - 1, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y - 1, (chunk.z << 4) + z);
			
			builder.addFaceBottom(x, y + offset, z, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x    , y - 1, z - 1, x + 2, y    , z + 1, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y    , z + 1, light, sky),
				blockLightingRegion(chunk, this, x    , y - 1, z    , x + 2, y    , z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z    , x + 1, y    , z + 2, light, sky),
				texture);
		}
		
		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x + 1, y, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.LEFT);
			var colour = getColour(this, Face.LEFT);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x + 1, y, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x + 1, y, (chunk.z << 4) + z);
			
			builder.addFaceLeft(x + 1, y + offset, z, 1, 0.5, r, g, b,
				blockLightingRegion(chunk, this, x + 1, y    , z - 1, x + 2, y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x + 1, y    , z    , x + 2, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1, light, sky),
				blockLightingRegion(chunk, this, x + 1, y - 1, z    , x + 2, y + 1, z + 2, light, sky),
				texture);
		}
		
		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x - 1, y, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.RIGHT);
			var colour = getColour(this, Face.RIGHT);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x - 1, y, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x - 1, y, (chunk.z << 4) + z);
			
			builder.addFaceRight(x, y + offset, z, 1, 0.5, r, g, b,
				blockLightingRegion(chunk, this, x - 1, y    , z    , x    , y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y    , z - 1, x    , y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z    , x    , y + 1, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x    , y + 1, z + 1, light, sky),
				texture);
		}

		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y, (chunk.z << 4) + z + 1))) {
			var texture = getTexture(Face.FRONT);
			var colour = getColour(this, Face.FRONT);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		

			var light = chunk.world.getLight((chunk.x << 4) + x, y, (chunk.z << 4) + z + 1);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y, (chunk.z << 4) + z + 1);

			builder.addFaceFront(x, y + offset, z + 1, 1, 0.5, r, g, b,
				blockLightingRegion(chunk, this, x    , y    , z + 1, x + 2, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y    , z + 1, x + 1, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x    , y - 1, z + 1, x + 2, y + 1, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2, light, sky),
				texture);
		}

		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y, (chunk.z << 4) + z - 1))) {
			var texture = getTexture(Face.BACK);
			var colour = getColour(this, Face.BACK);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		

			var light = chunk.world.getLight((chunk.x << 4) + x, y, (chunk.z << 4) + z - 1);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y, (chunk.z << 4) + z - 1);

			builder.addFaceBack(x, y + offset, z, 1, 0.5, r, g, b,
				blockLightingRegion(chunk, this, x - 1, y    , z - 1, x + 1, y + 2, z, light, sky),
				blockLightingRegion(chunk, this, x    , y    , z - 1, x + 2, y + 2, z, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y + 1, z, light, sky),
				blockLightingRegion(chunk, this, x    , y - 1, z - 1, x + 2, y + 1, z, light, sky),
				texture);
		}
	}
	
}