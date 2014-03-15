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
import mapviewer.block.Block.Face;

class BlockChest {
	
	private static var _model : Model;
	public static var model(get, never) : Model;
	
	private static var r(get, never) : Int;
	static function get_r() : Int return Std.int(256);
	private static var g(get, never) : Int;
	static function get_g() : Int return Std.int(256);
	private static var b(get, never) : Int;
	static function get_b() : Int return Std.int(256);
	
	static function get_model() : Model {
		if (_model != null) return _model;

		var modelBottom = new Model();
		
		//Bottom
		modelBottom.faces.push(ModelFace.create(Face.FRONT, "chest_side_front", 1, 0, 14, 10, 15)
			.textureSize(0, 0, 14, 10));
		modelBottom.faces.push(ModelFace.create(Face.BACK, "chest_side", 1, 0, 14, 10, 1)
			.textureSize(0, 0, 14, 10));
		modelBottom.faces.push(ModelFace.create(Face.LEFT, "chest_side", 1, 0, 14, 10, 15)
			.textureSize(0, 0, 14, 10));
		modelBottom.faces.push(ModelFace.create(Face.RIGHT, "chest_side", 1, 0, 14, 10, 1)
			.textureSize(0, 0, 14, 10));
		modelBottom.faces.push(ModelFace.create(Face.TOP, "chest_bottom_top", 1, 1, 14, 14, 10)
			.textureSize(0, 0, 14, 14));
		modelBottom.faces.push(ModelFace.create(Face.BOTTOM, "chest_top", 1, 1, 14, 14, 0)
			.textureSize(0, 0, 14, 14));

		var modelLid = new Model();

		//Top
		modelLid.faces.push(ModelFace.create(Face.FRONT, "chest_side_front", 1, 0, 14, 5, 15)
			.textureSize(0, 10, 14, 5));
		modelLid.faces.push(ModelFace.create(Face.BACK, "chest_side", 1, 0, 14, 5, 1)
			.textureSize(0, 10, 14, 5));
		modelLid.faces.push(ModelFace.create(Face.LEFT, "chest_side", 1, 0, 14, 5, 15)
			.textureSize(0, 10, 14, 5));
		modelLid.faces.push(ModelFace.create(Face.RIGHT, "chest_side", 1, 0, 14, 5, 1)
			.textureSize(0, 10, 14, 5));
		modelLid.faces.push(ModelFace.create(Face.TOP, "chest_top", 1, 1, 14, 14, 5)
			.textureSize(0, 0, 14, 14));
		modelLid.faces.push(ModelFace.create(Face.BOTTOM, "chest_top_bottom", 1, 1, 14, 14, 0)
			.textureSize(0, 0, 14, 14));

		//Lock
		modelLid.faces.push(ModelFace.create(Face.FRONT, "chest_lock", 7, -2, 2, 4, 16)
			.textureSize(1, 1, 2, 4));
		modelLid.faces.push(ModelFace.create(Face.LEFT, "chest_lock", 15, -2, 1, 4, 9)
			.textureSize(0, 1, 1, 4));
		modelLid.faces.push(ModelFace.create(Face.RIGHT, "chest_lock", 15, -2, 1, 4, 7)
			.textureSize(0, 1, 1, 4));
		modelLid.faces.push(ModelFace.create(Face.TOP, "chest_lock", 7, 15, 2, 1, 2)
			.textureSize(1, 0, 2, 1));
		modelLid.faces.push(ModelFace.create(Face.BOTTOM, "chest_lock", 7, 15, 2, 1, -2)
			.textureSize(3, 0, 2, 1));
			
		_model = modelBottom.join(modelLid, 0, 9, 0);
		return _model;
	}
}