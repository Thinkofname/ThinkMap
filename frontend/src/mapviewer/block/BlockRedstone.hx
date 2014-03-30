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
import mapviewer.model.ModelVertex;
import mapviewer.world.World;

class BlockRedstone extends Block {

	private var level : Int;
	
	public function new(level : Int) {
		super();
		this.level = level;
	}	
	
	inline private static function requireRedstone(x : Int, y : Int, z : Int, world : World) : Bool {
		var block = world.getBlock(x, y, z);
		return Std.is(block, BlockRedstone);
	}
	
	private static function swap(v : ModelVertex) {
		var x = v.textureX;
		v.textureX = v.textureY;
		v.textureY = x;
	}
	
	override public function getModel(x : Int, y : Int, z : Int, world : World) : Model {
		//TODO: Cache the model like stairs
		var model = new Model();
		
		var ufront = requireRedstone(x, y + 1, z + 1, world);
		var uback = requireRedstone(x, y + 1, z - 1, world);
		var uleft = requireRedstone(x + 1, y + 1, z, world);
		var uright = requireRedstone(x - 1, y + 1, z, world);
		
		var front = requireRedstone(x, y, z + 1, world) || ufront || requireRedstone(x, y - 1, z + 1, world);
		var back = requireRedstone(x, y, z - 1, world) || uback || requireRedstone(x, y - 1, z - 1, world);
		var left = requireRedstone(x + 1, y, z, world) || uleft || requireRedstone(x + 1, y - 1, z, world);
		var right = requireRedstone(x - 1, y, z, world) || uright || requireRedstone(x - 1, y - 1, z, world);
		
		
		if ((left || right) && !front && !back) {
			model.faces.push(ModelFace.create(Face.TOP, "redstone_dust_line", 0, 0, 16, 16, 0.5)
				.colour(Std.int((255 / 16) * (level + 1)), 0, 0));
		} else if (!left && !right && (front || back)) {
			model.faces.push(ModelFace.create(Face.TOP, "redstone_dust_line", 0, 0, 16, 16, 0.5)
				.colour(Std.int((255 / 16) * (level + 1)), 0, 0).forEach(swap));			
		} else {
			var x = 5;
			var y = 5;
			var w = 6;
			var h = 6;
			if (right) { x = 0; w += 5; }
			if (left) w += 5;
			if (back) { y = 0; h += 5; }
			if (front) h += 5;
			model.faces.push(ModelFace.create(Face.TOP, "redstone_dust_cross", x, y, w, h, 0.5)
				.colour(Std.int((255 / 16) * (level + 1)), 0, 0));
		}
		
		if (uleft) {
			model.faces.push(ModelFace.create(Face.RIGHT, "redstone_dust_line", 0, 0, 16, 16, 15.5)
				.colour(Std.int((255 / 16) * (level + 1)), 0, 0).forEach(swap));
		}
		if (uright) {
			model.faces.push(ModelFace.create(Face.LEFT, "redstone_dust_line", 0, 0, 16, 16, 0.5)
				.colour(Std.int((255 / 16) * (level + 1)), 0, 0).forEach(swap));
		}
		if (ufront) {
			model.faces.push(ModelFace.create(Face.BACK, "redstone_dust_line", 0, 0, 16, 16, 15.5)
				.colour(Std.int((255 / 16) * (level + 1)), 0, 0).forEach(swap));
		}
		if (uback) {
			model.faces.push(ModelFace.create(Face.FRONT, "redstone_dust_line", 0, 0, 16, 16, 0.5)
				.colour(Std.int((255 / 16) * (level + 1)), 0, 0).forEach(swap));
		}
		
		return model;
	}
}