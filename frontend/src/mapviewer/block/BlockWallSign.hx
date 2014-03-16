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

class BlockWallSign {
	
	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model {
		if (_model != null) return _model;
		_model = new Model();
		_model.faces.push(ModelFace.create(Face.FRONT, "planks_oak", 0, 4, 16, 8, 1)
			.textureSize(18, 4, 24, 12));
		_model.faces.push(ModelFace.create(Face.BACK, "planks_oak", 0, 4, 16, 8, 0)
			.textureSize(18, 4, 24, 12));
		_model.faces.push(ModelFace.create(Face.LEFT, "planks_oak", 0, 4, 1, 8, 16)
			.textureSize(0, 0, 2, 12));
		_model.faces.push(ModelFace.create(Face.RIGHT, "planks_oak", 0, 4, 1, 8, 0)
			.textureSize(0, 0, 2, 12));
		_model.faces.push(ModelFace.create(Face.TOP, "planks_oak", 0, 0, 16, 1, 12)
			.textureSize(18, 0, 24, 2));
		_model.faces.push(ModelFace.create(Face.BOTTOM, "planks_oak", 0, 0, 16, 1, 4)
			.textureSize(18, 0, 24, 2));
		return _model;
	}
}