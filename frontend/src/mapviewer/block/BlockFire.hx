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
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.world.Chunk;
import mapviewer.block.Face;
import mapviewer.model.Model;
import mapviewer.model.ModelFace;
import mapviewer.world.World;

class BlockFire extends Block {

	public function new() {	
		super();
	}
	
	private static var models : Map<Int, Model> = new Map();
	
	override public function getModel(x : Int, y : Int, z : Int, world : World) : Model {
		model = new Model();
			
		var top = false;
		var left = false;
		var right = false;
		var front = false;
		var back = false;
		var all = false;
		
		if (world.getBlock(x, y - 1, z).solid) {
			all = true;
		} else {
			if (world.getBlock(x, y + 1, z).solid) {
				top = true;
			}
			if (world.getBlock(x + 1, y, z).solid) {
				left = true;
			}
			if (world.getBlock(x - 1, y, z).solid) {
				right = true;
			}
			if (world.getBlock(x, y, z + 1).solid) {
				front = true;
			}
			if (world.getBlock(x, y, z - 1).solid) {
				back = true;
			}
		}
		
		var id = 0;
		id |= top ? 1 : 0;
		id |= left ? 2 : 0;
		id |= right ? 4 : 0;
		id |= front ? 8 : 0;
		id |= back ? 16 : 0;
		id |= all ? 32 : 0;
		
		model = models[id];
		if (model == null) {
			model = new Model();
			if (all) {
				var face = ModelFace.create(Face.RIGHT, "fire_layer_0", 0, 0, 16, 16, 0);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.x += 10 / 16;
					}
				}
				model.faces.push(face);
				var face = ModelFace.create(Face.LEFT, "fire_layer_0", 0, 0, 16, 16, 16);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.x -= 1 / 16;
					}
				}
				model.faces.push(face);
				
				var face = ModelFace.create(Face.LEFT, "fire_layer_0", 0, 0, 16, 16, 16);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.x -= 10 / 16;
					}
				}
				model.faces.push(face);
				var face = ModelFace.create(Face.RIGHT, "fire_layer_0", 0, 0, 16, 16, 0);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.x += 1 / 16;
					}
				}
				model.faces.push(face);
				
				var face = ModelFace.create(Face.BACK, "fire_layer_0", 0, 0, 16, 16, 0);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.z += 10 / 16;
					}
				}
				model.faces.push(face);
				var face = ModelFace.create(Face.FRONT, "fire_layer_0", 0, 0, 16, 16, 16);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.z -= 1 / 16;
					}
				}
				model.faces.push(face);
				
				var face = ModelFace.create(Face.FRONT, "fire_layer_0", 0, 0, 16, 16, 16);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.z -= 10 / 16;
					}
				}
				model.faces.push(face);
				var face = ModelFace.create(Face.BACK, "fire_layer_0", 0, 0, 16, 16, 0);
				for (vert in face.vertices) {
					if (vert.y == 1) {
						vert.y += 6 / 16;
						vert.z += 1 / 16;
					}
				}
				model.faces.push(face);
			} else {
				if (top) {
					var face = ModelFace.create(Face.BOTTOM, "fire_layer_1", 0, 0, 16, 16, 16);
					for (vert in face.vertices) { 
						if (vert.z == 0) {
							vert.y -= 2 / 16;
						}
					}
					model.faces.push(face);
					var face = ModelFace.create(Face.BOTTOM, "fire_layer_1", 0, 0, 16, 16, 16);
					for (vert in face.vertices) { 
						if (vert.z == 1) {
							vert.y -= 2 / 16;
						}
						vert.textureY = 1 - vert.textureY;
					}
					model.faces.push(face);
				}
				if (left) {
					var face = ModelFace.create(Face.RIGHT, "fire_layer_0", 0, 0, 16, 16, 16);
					for (vert in face.vertices) {
						if (vert.y == 1) {
							vert.y += 6 / 16;
							vert.x -= 3 / 16;
						}
					}
					model.faces.push(face);
				}
				if (right) {
					var face = ModelFace.create(Face.LEFT, "fire_layer_0", 0, 0, 16, 16, 0);
					for (vert in face.vertices) {
						if (vert.y == 1) {
							vert.y += 6 / 16;
							vert.x += 3 / 16;
						}
					}
					model.faces.push(face);
				}
				if (front) {
					var face = ModelFace.create(Face.BACK, "fire_layer_0", 0, 0, 16, 16, 16);
					for (vert in face.vertices) {
						if (vert.y == 1) {
							vert.y += 6 / 16;
							vert.z -= 3 / 16;
						}
					}
					model.faces.push(face);
				}
				if (back) {
					var face = ModelFace.create(Face.FRONT, "fire_layer_0", 0, 0, 16, 16, 0);
					for (vert in face.vertices) {
						if (vert.y == 1) {
							vert.y += 6 / 16;
							vert.z += 3 / 16;
						}
					}
					model.faces.push(face);
				}
			}
			models[id] = model;
		}
		return super.getModel(x, y, z, world);
	}	
}