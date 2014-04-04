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
import mapviewer.model.ModelFace;
import mapviewer.world.World;
import mapviewer.model.Model;

class BlockDoor extends Block {
	
	private var top : Bool;
	private var flag : Bool;
	private var dir : Int;

	public function new(top : Bool, flag : Bool, ?dir : Int = 0) {
		super();
		this.top = top;
		this.flag = flag;
		this.dir = dir;
	}	
	
	override public function getModel(x : Int, y : Int, z : Int, world : World) : Model {
		var t : BlockDoor;
		var b : BlockDoor;
		if (top) {
			t = this;
			var temp = world.getBlock(x, y - 1, z);
			b = Std.is(temp, BlockDoor) ? cast temp : null;
		} else {			
			b = this;
			var temp = world.getBlock(x, y + 1, z);
			t = Std.is(temp, BlockDoor) ? cast temp : null;
		}
		var model = new Model();
		if (t == null || b == null) return model;
		
		var texture = this.texture + (top ? "_upper" : "_lower");
		
		model.faces.push(ModelFace.create(Face.FRONT, texture, 0, 0, 16, 16, 3, true).forEach(function(v) {
			v.textureX = 1 - v.textureX;			
		}));
		model.faces.push(ModelFace.create(Face.BACK, texture, 0, 0, 16, 16, 0, false));
		model.faces.push(ModelFace.create(Face.TOP, texture, 0, 0, 16, 3, 16, true));
		model.faces.push(ModelFace.create(Face.BOTTOM, texture, 0, 0, 16, 3, 0, true));
		model.faces.push(ModelFace.create(Face.LEFT, texture, 0, 0, 3, 16, 16, true));
		model.faces.push(ModelFace.create(Face.RIGHT, texture, 0, 0, 3, 16, 0, true));
		
		if (t.flag) {
			for (face in model.faces) {
				for (v in face.vertices) {
					v.textureX = 1 - v.textureX;
				}
			}
		}
		
		return model.rotateY(b.dir * 90 + 270 + (b.flag ? (t.flag ? -90 : 90) : 0));
	}
}