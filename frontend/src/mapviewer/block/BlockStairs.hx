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
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.world.Chunk;
import mapviewer.block.Block.Face;

class BlockStairs extends Block {
	
	private var isTop : Bool;
	private var dir : Int;
	
	private static var models : Map<String, Map<Int, Model>> = new Map();
	private static var dirs : Array<Array<Int>> = [
		[1, 0],
		[-1, 0],
		[0, 1],
		[0, -1],
	];
	private static var allowedFlags : Array<Array<Int>> = [
		[3, 2],
		[3, 2],
		[1, 0],
		[1, 0]
	];
	private static var preventers : Array<Array<Int>> = [
		[-1, 0],
		[1, 0],
		[0, -1],
		[0, 1],
	];
	private static var rots : Array<Int> = [
		2, 0, 3, 1
	];

	public function new(isTop : Bool, dir : Int) {
		super();
		this.isTop = isTop;
		this.dir = dir;
	}
	
	function isMe(chunk : Chunk, x : Int, y : Int, z : Int) : Bool {
		var block = chunk.world.getBlock(x, y, z);
		if (Std.is(block, BlockStairs)) {
			var stair : BlockStairs = cast block;
			return stair.dir == dir && stair.isTop == isTop;
		}
		return false;
	}
	
	override public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {
		var mmap = models[texture];
		if (mmap == null) {
			mmap = new Map();
			models[texture] = mmap;
		}
		// 0x1 Upside down
		// 0x2/0x4 Dir		
		// 0x8 Corner (small)
		// 0x16 L shape (big)
		// 0x32 Side
		var id = isTop ? 1 : 0; 
		id |= dir << 1;
		
		var d = dirs[dir];
		
		var set = false;
		var other : Block = chunk.world.getBlock((chunk.x << 4) + x + d[0], y, (chunk.z << 4) + z + d[1]);
		if (Std.is(other, BlockStairs)) {
			var otherStair : BlockStairs = cast other;
			var idx = allowedFlags[dir].indexOf(otherStair.dir);
			var p = preventers[otherStair.dir];
			if (otherStair.isTop == isTop && idx != -1 && !isMe(chunk, (chunk.x << 4) + x + p[0], y, (chunk.z << 4) + z + p[1])) {
				id |= 8;
				id |= idx << 5;
				set = true;
			}
		} 
		if (!set) {			
			other = chunk.world.getBlock((chunk.x << 4) + x - d[0], y, (chunk.z << 4) + z - d[1]);
			if (Std.is(other, BlockStairs)) {
				var otherStair : BlockStairs = cast other;
				var idx = allowedFlags[dir].indexOf(otherStair.dir);
				var p = preventers[otherStair.dir];
				if (otherStair.isTop == isTop && idx != -1 && !isMe(chunk, (chunk.x << 4) + x - p[0], y, (chunk.z << 4) + z - p[1])) {
					id |= 16;
					id |= idx << 5;
				}
			} 
		}
		
		var model = mmap[id];
		if (model == null) {
			// Create the model
			model = new Model();
			
			//TODO: Culling on sides
			//TODO: Fix texture scales
			
			// Slab part
			model.faces.push(ModelFace.fromFace(Face.TOP)
				.moveY(8));
			model.faces.push(ModelFace.fromFace(Face.BOTTOM));
			model.faces.push(ModelFace.fromFace(Face.LEFT)
				.sizeY( -8)
				.moveX(16));
			model.faces.push(ModelFace.fromFace(Face.RIGHT)
				.sizeY( -8));
			model.faces.push(ModelFace.fromFace(Face.FRONT)
				.sizeY( -8)
				.moveZ(16));
			model.faces.push(ModelFace.fromFace(Face.BACK)
				.sizeY( -8));
				
			// Top part
			
			if ((id & 8) == 0 && (id & 16) == 0) {
				// Not special
				var section = new Model();
				section.faces.push(ModelFace.fromFace(Face.TOP)
					.moveY(16)
					.sizeX( -8));
				section.faces.push(ModelFace.fromFace(Face.LEFT)
					.moveY(8)
					.sizeY( -8)
					.moveX(8));
				section.faces.push(ModelFace.fromFace(Face.RIGHT)
					.moveY(8)
					.sizeY( -8));
				section.faces.push(ModelFace.fromFace(Face.FRONT)
					.moveY(8)
					.sizeY( -8)
					.sizeX( -8)
					.moveZ(16));
				section.faces.push(ModelFace.fromFace(Face.BACK)
					.moveY(8)
					.sizeX( -8)
					.sizeY( -8));
					
				model.join(section.rotateY(rots[dir] * 90));
			} else if (id & 8 == 8) {	
				// Corner
				var section = new Model();
				var t = "gold_block";
				section.faces.push(ModelFace.fromFace(Face.TOP)
					.moveY(16)
					.sizeX( -8)
					.sizeZ( -8)
					.moveZ(8));					
				section.faces.push(ModelFace.fromFace(Face.LEFT)
					.moveY(8)
					.sizeY( -8)
					.sizeZ( -8)
					.moveX(8)
					.moveZ(8));
				section.faces.push(ModelFace.fromFace(Face.RIGHT)
					.moveY(8)
					.sizeZ( -8)
					.sizeY( -8)
					.moveZ(8));
				section.faces.push(ModelFace.fromFace(Face.FRONT)
					.moveY(8)
					.sizeY( -8)
					.sizeX( -8)
					.moveZ(8)
					.moveZ(8));
				section.faces.push(ModelFace.fromFace(Face.BACK)
					.moveY(8)
					.sizeX( -8)
					.sizeY( -8)
					.moveZ(8));
					
				model.join(new Model().join(section, 0, 0,  
						((dir == 0 || dir == 3) && id & 32 == 32) ||
							((dir == 1 || dir == 2) && id & 32 == 0) ? -8 : 0 ).rotateY(rots[dir] * 90));
				
			} else {				
				// Corner
				var section = new Model();
				var t = "coal_block";
				section.faces.push(ModelFace.fromFace(Face.TOP)
					.moveY(16)
					.sizeZ( -8)
					.moveZ(8));
				section.faces.push(ModelFace.fromFace(Face.LEFT)
					.moveY(8)
					.sizeY( -8)
					.sizeZ( -8)
					.moveX(16)
					.moveZ(8));
				section.faces.push(ModelFace.fromFace(Face.RIGHT)
					.moveY(8)
					.sizeY( -8)
					.sizeZ( -8)
					.moveZ(8));
				section.faces.push(ModelFace.fromFace(Face.FRONT)
					.moveY(8)
					.sizeY( -8)
					.moveZ(8)
					.moveZ(8));
				section.faces.push(ModelFace.fromFace(Face.BACK)
					.moveY(8)
					.sizeY( -8)
					.moveZ(8));
					
				var oz = ((dir == 0 || dir == 3) && id & 32 == 32) ||
							((dir == 1 || dir == 2) && id & 32 == 0) ? 16 : 0;
				section.faces.push(ModelFace.fromFace(Face.TOP)
					.moveY(16)
					.sizeX( -8)
					.sizeZ( -8)
					.moveZ(oz));				
				section.faces.push(ModelFace.fromFace(Face.LEFT)
					.moveY(8)
					.sizeY( -8)
					.sizeZ( -8)
					.moveX(8)
					.moveZ(oz));
				section.faces.push(ModelFace.fromFace(Face.RIGHT)
					.moveY(8)
					.sizeZ( -8)
					.sizeY( -8)
					.moveZ(oz));
				section.faces.push(ModelFace.fromFace(Face.FRONT)
					.moveY(8)
					.sizeY( -8)
					.sizeX( -8)
					.moveZ(8)
					.moveZ(oz));
				section.faces.push(ModelFace.fromFace(Face.BACK)
					.moveY(8)
					.sizeX( -8)
					.sizeY( -8)
					.moveZ(oz));
					
				model.join(new Model().join(section, 0, 0,  
						((dir == 0 || dir == 3) && id & 32 == 32) ||
							((dir == 1 || dir == 2) && id & 32 == 0) ? -8 : 0 ).rotateY(rots[dir] * 90));
							
			}
			
			// Fix model/textures
			for (face in model.faces) {				
				if (face.texture == null)
					face.texture = texture;
				// Correct texture positions
				for (vert in face.vertices) {
					if (isTop) // Flip model
						vert.y = 1 - vert.y;
						
					if (face.face != Face.LEFT && face.face != Face.RIGHT) {
						vert.textureX = vert.x;
					}
					if (face.face == Face.LEFT || face.face == Face.RIGHT) {			
						vert.textureX = vert.z;				
					} else if (face.face == Face.TOP || face.face == Face.BOTTOM) {					
						vert.textureY = 1 - vert.z;				
					}
					if (face.face != Face.TOP && face.face != Face.BOTTOM) {			
						vert.textureY = 1 - vert.y;						
					}
				}
				if (isTop) { // Flip model
					if (face.face == Face.TOP) face.face = Face.BOTTOM;
					if (face.face == Face.BOTTOM) face.face = Face.TOP;
					var temp = face.vertices[2];
					face.vertices[2] = face.vertices[1];
					face.vertices[1] = temp;
					var temp = face.vertices[5];
					face.vertices[5] = face.vertices[4];
					face.vertices[4] = temp;
				}
			}
				
			// Store
			mmap[id] = model;
		}
		// Temp: Hack lighting
		chunk.setLight(x, y, z, 11);
		chunk.setSky(x, y, z, 11);
		model.render(builder, x, y, z, chunk);
	}
}