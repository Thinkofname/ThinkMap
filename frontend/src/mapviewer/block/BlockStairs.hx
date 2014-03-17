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
import mapviewer.renderer.LightInfo;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.world.Chunk;
import mapviewer.block.Face;
import mapviewer.world.World;

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
	
	function isMe(world : World, x : Int, y : Int, z : Int) : Bool {
		var block = world.getBlock(x, y, z);
		if (Std.is(block, BlockStairs)) {
			var stair : BlockStairs = cast block;
			return stair.dir == dir && stair.isTop == isTop;
		}
		return false;
	}
	
	override public function getModel(x : Int, y : Int, z : Int, world : World) : Model {
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
		var other : Block = world.getBlock(x + d[0], y, z + d[1]);
		if (Std.is(other, BlockStairs)) {
			var otherStair : BlockStairs = cast other;
			var idx = allowedFlags[dir].indexOf(otherStair.dir);
			var p = preventers[otherStair.dir];
			if (otherStair.isTop == isTop && idx != -1 && !isMe(world, x + p[0], y, z + p[1])) {
				id |= 8;
				id |= idx << 5;
				set = true;
			}
		} 
		if (!set) {			
			other = world.getBlock(x - d[0], y, z - d[1]);
			if (Std.is(other, BlockStairs)) {
				var otherStair : BlockStairs = cast other;
				var idx = allowedFlags[dir].indexOf(otherStair.dir);
				var p = preventers[otherStair.dir];
				if (otherStair.isTop == isTop && idx != -1 && !isMe(world, x - p[0], y, z - p[1])) {
					id |= 16;
					id |= idx << 5;
				}
			} 
		}
		
		model = mmap[id];
		if (model == null) {
			// Create the model
			model = new Model();
			
			// Slab part
			model.faces.push(ModelFace.create(Face.TOP, null, 0, 0, 16, 16, 8));
			model.faces.push(ModelFace.create(Face.BOTTOM, null, 0, 0, 16, 16, 0, true));
			model.faces.push(ModelFace.create(Face.LEFT, null, 0, 0, 16, 8, 16, true));
			model.faces.push(ModelFace.create(Face.RIGHT, null, 0, 0, 16, 8, 0, true));
			model.faces.push(ModelFace.create(Face.FRONT, null, 0, 0, 16, 8, 16, true));
			model.faces.push(ModelFace.create(Face.BACK, null, 0, 0, 16, 8, 0, true));
				
			// Top part
			
			if ((id & 8) == 0 && (id & 16) == 0) {
				// Not special
				var section = new Model();
				section.faces.push(ModelFace.create(Face.TOP, null, 0, 0, 8, 16, 16));
				section.faces.push(ModelFace.create(Face.LEFT, null, 0, 8, 16, 8, 8));
				section.faces.push(ModelFace.create(Face.RIGHT, null, 0, 8, 16, 8, 0));
				section.faces.push(ModelFace.create(Face.FRONT, null, 0, 8, 8, 8, 16));
				section.faces.push(ModelFace.create(Face.BACK, null, 0, 8, 8, 8, 0));					
				model.join(section.rotateY(rots[dir] * 90));
			} else if (id & 8 == 8) {	
				// Corner
				var section = new Model();
				section.faces.push(ModelFace.create(Face.TOP, null, 0, 8, 8, 8, 16));
				section.faces.push(ModelFace.create(Face.LEFT, null, 8, 8, 8, 8, 8));
				section.faces.push(ModelFace.create(Face.RIGHT, null, 8, 8, 8, 8, 0));
				section.faces.push(ModelFace.create(Face.FRONT, null, 0, 8, 8, 8, 16));
				section.faces.push(ModelFace.create(Face.BACK, null, 0, 8, 8, 8, 8));
					
				model.join(new Model().join(section, 0, 0,  
						((dir == 0 || dir == 3) && id & 32 == 32) ||
							((dir == 1 || dir == 2) && id & 32 == 0) ? -8 : 0 ).rotateY(rots[dir] * 90));
				
			} else {				
				// L Shape
				var section = new Model();
				section.faces.push(ModelFace.create(Face.TOP, null, 0, 8, 16, 8, 16));
				section.faces.push(ModelFace.create(Face.LEFT, null, 8, 8, 8, 8, 16));
				section.faces.push(ModelFace.create(Face.RIGHT, null, 8, 8, 8, 8, 0));
				section.faces.push(ModelFace.create(Face.FRONT, null, 0, 8, 16, 8, 16));
				section.faces.push(ModelFace.create(Face.BACK, null, 0, 8, 16, 8, 8));
					
				var oz = ((dir == 0 || dir == 3) && id & 32 == 32) ||
							((dir == 1 || dir == 2) && id & 32 == 0) ? 16 : 0;
				section.faces.push(ModelFace.create(Face.TOP, null, 0, oz, 8, 8, 16));
				section.faces.push(ModelFace.create(Face.LEFT, null, oz, 8, 8, 8, 8));
				section.faces.push(ModelFace.create(Face.RIGHT, null, oz, 8, 8, 8, 0));
				section.faces.push(ModelFace.create(Face.FRONT, null, 0, 8, 8, 8, oz + 8));
				section.faces.push(ModelFace.create(Face.BACK, null, 0, 8, 8, 8, oz));
					
				model.join(new Model().join(section, 0, 0,  
						((dir == 0 || dir == 3) && id & 32 == 32) ||
							((dir == 1 || dir == 2) && id & 32 == 0) ? -8 : 0 ).rotateY(rots[dir] * 90));
							
			}
			
			// Fix model/textures
			for (face in model.faces) {				
				if (face.texture == null)
					face.texture = texture;
			}
			if (isTop) model.flipModel();
			model.realignTextures();
				
			// Store
			mmap[id] = model;
		}
		return super.getModel(x, y, z, world);
	}
}