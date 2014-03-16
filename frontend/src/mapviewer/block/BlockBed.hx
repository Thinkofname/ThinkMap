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
import mapviewer.block.Face;

using Lambda;

class BlockBed {
	
	private static var _modelBottom : Model;
	public static var modelBottom(get, never) : Model;
	
	static function get_modelBottom() : Model {
		if (_modelBottom != null) return _modelBottom;

		var _modelBottom = new Model();
		
		_modelBottom.faces.push(ModelFace.create(Face.TOP, "bed_feet_top", 0, 0, 16, 16, 9));		
		_modelBottom.faces.push(ModelFace.create(Face.RIGHT, "bed_feet_end", 0, 0, 16, 9, 0)
			.textureSize(0, 7, 16, 9));			
		_modelBottom.faces.push(ModelFace.create(Face.FRONT, "bed_feet_side", 0, 0, 16, 9, 16)
			.textureSize(0, 7, 16, 9));
		var face = ModelFace.create(Face.BACK, "bed_feet_side", 0, 0, 16, 9, 0)
			.textureSize(0, 7, 16, 9);
		for (v in face.vertices) {
			v.textureX = 1 - v.textureX;
		}
		_modelBottom.faces.push(face);
		return _modelBottom;
	}
	
	private static var _modelTop : Model;
	public static var modelTop(get, never) : Model;
	
	static function get_modelTop() : Model {
		if (_modelTop != null) return _modelTop;

		var _modelTop = new Model();
		
		_modelTop.faces.push(ModelFace.create(Face.TOP, "bed_head_top", 0, 0, 16, 16, 9));		
		_modelTop.faces.push(ModelFace.create(Face.LEFT, "bed_head_end", 0, 0, 16, 9, 16)
			.textureSize(0, 7, 16, 9));			
		_modelTop.faces.push(ModelFace.create(Face.FRONT, "bed_head_side", 0, 0, 16, 9, 16)
			.textureSize(0, 7, 16, 9));
		var face = ModelFace.create(Face.BACK, "bed_head_side", 0, 0, 16, 9, 0)
			.textureSize(0, 7, 16, 9);
		for (v in face.vertices) {
			v.textureX = 1 - v.textureX;
		}
		_modelTop.faces.push(face);
		return _modelTop;
	}
}