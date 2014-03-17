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

class BlockHopper {

	private static var _model : Model;
	public static var model(get, never) : Model;

	static function get_model() : Model {
		if (_model != null) return _model;

		_model = new Model();

		_model.faces.push(ModelFace.create(Face.TOP, "hopper_top", 0, 0, 16, 16, 16, true));
		_model.faces.push(ModelFace.create(Face.TOP, "hopper_inside", 0, 0, 16, 16, 10));

		// Inside

		_model.faces.push(ModelFace.create(Face.FRONT, "hopper_outside", 2, 10, 12, 6, 2)
			.textureSize(2, 0, 12, 6));
		_model.faces.push(ModelFace.create(Face.BACK, "hopper_outside", 2, 10, 12, 6, 14)
			.textureSize(2, 0, 12, 6));
		_model.faces.push(ModelFace.create(Face.LEFT, "hopper_outside", 2, 10, 12, 6, 2)
			.textureSize(2, 0, 12, 6));
		_model.faces.push(ModelFace.create(Face.RIGHT, "hopper_outside", 2, 10, 12, 6, 14)
			.textureSize(2, 0, 12, 6));

		// Outside - top

		_model.faces.push(ModelFace.create(Face.BACK, "hopper_outside", 0, 10, 16, 6, 0)
			.textureSize(0, 0, 16, 6)); 
		_model.faces.push(ModelFace.create(Face.FRONT, "hopper_outside", 0, 10, 16, 6, 16)
			.textureSize(0, 0, 16, 6)); 
		_model.faces.push(ModelFace.create(Face.RIGHT, "hopper_outside", 0, 10, 16, 6, 0)
			.textureSize(0, 0, 16, 6)); 
		_model.faces.push(ModelFace.create(Face.LEFT, "hopper_outside", 0, 10, 16, 6, 16)
			.textureSize(0, 0, 16, 6));
		_model.faces.push(ModelFace.create(Face.BOTTOM, "hopper_outside", 0, 0, 16, 16, 10));

		// Outside - middle

		_model.faces.push(ModelFace.create(Face.BACK, "hopper_outside", 4, 4, 8, 6, 4)
			.textureSize(4, 6, 8, 6));
		_model.faces.push(ModelFace.create(Face.FRONT, "hopper_outside", 4, 4, 8, 6, 12)
			.textureSize(4, 6, 8, 6));
		_model.faces.push(ModelFace.create(Face.RIGHT, "hopper_outside", 4, 4, 8, 6, 4)
			.textureSize(4, 6, 8, 6));
		_model.faces.push(ModelFace.create(Face.LEFT, "hopper_outside", 4, 4, 8, 6, 12)
			.textureSize(4, 6, 8, 6));
		_model.faces.push(ModelFace.create(Face.BOTTOM, "hopper_outside", 4, 4, 8, 8, 4));

		return _model;
	}

	private static var _spout : Model;
	public static var spout(get, never) : Model;

	static function get_spout() : Model {
		if (_spout != null) return _spout;

		_spout = new Model();

		_spout.faces.push(ModelFace.create(Face.BACK, "hopper_outside", 0, 0, 4, 4, 0)
			.textureSize(6, 12, 4, 4));
		_spout.faces.push(ModelFace.create(Face.FRONT, "hopper_outside", 0, 0, 4, 4, 4)
			.textureSize(6, 12, 4, 4));
		_spout.faces.push(ModelFace.create(Face.RIGHT, "hopper_outside", 0, 0, 4, 4, 0)
			.textureSize(6, 12, 4, 4));
		_spout.faces.push(ModelFace.create(Face.LEFT, "hopper_outside", 0, 0, 4, 4, 4)
			.textureSize(6, 12, 4, 4));
		_spout.faces.push(ModelFace.create(Face.BOTTOM, "hopper_outside", 0, 0, 4, 4, 0)
			.textureSize(6, 6, 4, 4));
		_spout.faces.push(ModelFace.create(Face.TOP, "hopper_outside", 0, 0, 4, 4, 4)
			.textureSize(6, 6, 4, 4));

		return _spout;
	}
}