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
import mapviewer.renderer.LightInfo;
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
			
			// Slab part
			model.faces.push(ModelFace.create(Face.TOP, null, 0, 0, 16, 16, 8));
			model.faces.push(ModelFace.create(Face.BOTTOM, null, 0, 0, 16, 16, 0));
			model.faces.push(ModelFace.create(Face.LEFT, null, 0, 0, 16, 8, 16));
			model.faces.push(ModelFace.create(Face.RIGHT, null, 0, 0, 16, 8, 0));
			model.faces.push(ModelFace.create(Face.FRONT, null, 0, 0, 16, 8, 16));
			model.faces.push(ModelFace.create(Face.BACK, null, 0, 0, 16, 8, 0));
				
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
		// Temp: Hack lighting
		var l = chunk.getLight(x, y, z);
		var s = chunk.getSky(x, y, z);
		var li : LightInfo = blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x + 2, y + 2, z + 2, 0, 0);
		chunk.setLight(x, y, z, li.light);
		chunk.setSky(x, y, z, li.sky);
		model.render(builder, x, y, z, chunk);
		chunk.setLight(x, y, z, l);
		chunk.setSky(x, y, z, s);
	}
	
	public static function blockLightingRegion(chunk : Chunk, self : Block, x1 : Int, y1 : Int, z1 : Int, 
		x2 : Int, y2 : Int, z2 : Int, ?faceLight : Int = 0, ?faceSkyLight : Int = 0) {
		var light = 15;
		var sky = 15;
		var count = 0;
		var valSolid : Int = Std.int((15 * Math.pow(0.85, faceLight + 1)));
		var valSkySolid : Int = Std.int((15 * Math.pow(0.85, faceSkyLight + 1)));
		for (y in y1 ... y2) {
			if (y < 0 || y > 255) continue;
			for (x in x1 ... x2) {
				for (z in z1 ... z2) {
					var px = (chunk.x << 4) + x;
					var pz = (chunk.z << 4) + z;
					if (!chunk.world.isLoaded(px, y, pz)) continue;
					count++;
					var valSky = 6;
					var val = 6;
					
					var block = chunk.world.getBlock(px, y, pz);
					if (block.shade && (block.solid || block == self)) {
						val -= valSolid;
						valSky -= valSkySolid;
					} else {
						valSky = chunk.world.getSky(px, y, pz);
						val = chunk.world.getLight(px, y, pz);
					}
					light += val;
					sky += valSky;
				}
			}
		}
		light += 11 * count;
		sky += 11 * count;
		if (count == 0) return new LightInfo(15, 15);
		return new LightInfo(Std.int(light / count), Std.int(sky / count));
	}
}