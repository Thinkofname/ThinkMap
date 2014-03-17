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

class BlockAnvil {

	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model {
		if (_model != null) return _model;

		_model = new Model();
		_model.faces.push(ModelFace.create(Face.TOP, "anvil_top_damaged_0", 3, 0, 10, 16, 16,true));
		_model.faces.push(ModelFace.create(Face.TOP, "anvil_base", 4, 3, 8, 10, 5));
		_model.faces.push(ModelFace.create(Face.TOP, "anvil_base", 2, 2, 12, 12, 4));
		
		_model.faces.push(ModelFace.create(Face.LEFT, "anvil_base", 0, 10, 16, 6, 13)); 
		_model.faces.push(ModelFace.create(Face.LEFT, "anvil_base", 4, 5, 8, 5, 10));
		_model.faces.push(ModelFace.create(Face.LEFT, "anvil_base", 3, 4, 10, 1, 12));
		_model.faces.push(ModelFace.create(Face.LEFT, "anvil_base", 2, 0, 12, 4, 14));
		
		_model.faces.push(ModelFace.create(Face.FRONT, "anvil_base", 3, 10, 10, 6, 16));
		_model.faces.push(ModelFace.create(Face.FRONT, "anvil_base", 6, 5, 4, 5, 12));
		_model.faces.push(ModelFace.create(Face.FRONT, "anvil_base", 4, 4, 8, 1, 13));
		_model.faces.push(ModelFace.create(Face.FRONT, "anvil_base", 2, 0, 12, 4, 14));
			
		_model.faces.push(ModelFace.create(Face.RIGHT, "anvil_base", 0, 10, 16, 6, 3));
		_model.faces.push(ModelFace.create(Face.RIGHT, "anvil_base", 4, 5, 8, 5, 6));
		_model.faces.push(ModelFace.create(Face.RIGHT, "anvil_base", 3, 4, 10, 1, 4));
		_model.faces.push(ModelFace.create(Face.RIGHT, "anvil_base", 2, 0, 12, 4, 2));
			
		_model.faces.push(ModelFace.create(Face.BACK, "anvil_base", 3, 10, 10, 6, 0));
		_model.faces.push(ModelFace.create(Face.BACK, "anvil_base", 6, 5, 4, 5, 4));
		_model.faces.push(ModelFace.create(Face.BACK, "anvil_base", 4, 4, 8, 1, 3));
		_model.faces.push(ModelFace.create(Face.BACK, "anvil_base", 2, 0, 12, 4, 2));
			
		_model.faces.push(ModelFace.create(Face.BOTTOM, "anvil_base", 2, 2, 12, 12, 0, true));
		_model.faces.push(ModelFace.create(Face.BOTTOM, "anvil_base", 3, 0, 10, 16, 10));
		return _model;
	}
}
