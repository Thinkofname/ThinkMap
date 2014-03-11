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

class BlockHopper {

	private static var _model : Model;
	public static var model(get, never) : Model;

	static function get_model() : Model {
		if (_model != null) return _model;

		_model = new Model();

		_model.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("hopper_top").ret()
			.moveY(16));
		_model.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("hopper_inside").ret()
			.moveY(10));

		// Inside

		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveX(2)
			.moveX(2, true)
			.sizeX(-4)
			.sizeX(-4, true)
			.moveZ(2));

		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveX(2)
			.moveX(2, true)
			.sizeX(-4)
			.sizeX(-4, true)
			.moveZ(14));

		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveZ(2)
			.moveX(2, true)
			.sizeZ(-4)
			.sizeX(-4, true)
			.moveX(2));

		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveZ(2)
			.moveX(2, true)
			.sizeZ(-4)
			.sizeX(-4, true)
			.moveX(14));

		// Outside - top

		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true));
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveZ(16));

		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveX(0));
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveX(16));

		_model.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(10));

		// Outside - middle

		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(4)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveY(6, true)
			.moveZ(4)
			.moveX(4)
			.moveX(4, true)
			.sizeX(-8)
			.sizeX(-8, true));
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(4)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveY(6, true)
			.moveZ(12)
			.moveX(4)
			.moveX(4, true)
			.sizeX(-8)
			.sizeX(-8, true));

		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(4)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveY(6, true)
			.moveX(4)
			.moveZ(4)
			.moveX(4, true)
			.sizeZ(-8)
			.sizeX(-8, true));
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("hopper_outside").ret()
			.moveY(4)
			.sizeY(-10)
			.sizeY(-10, true)
			.moveY(6, true)
			.moveX(12)
			.moveZ(4)
			.moveX(4, true)
			.sizeZ(-8)
			.sizeX(-8, true));

		_model.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("hopper_outside").ret()
			.moveX(4)
			.moveX(4, true)
			.sizeX(-8)
			.sizeX(-8, true)
			.moveZ(4)
			.moveY(4, true)
			.sizeZ(-8)
			.sizeY(-8, true)
			.moveY(4));

		return _model;
	}

	private static var _spout : Model;
	public static var spout(get, never) : Model;

	static function get_spout() : Model {
		if (_spout != null) return _spout;

		_spout = new Model();

		_spout.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("hopper_outside").ret()
			.sizeY(-12)
			.sizeY(-12, true)
			.moveY(12, true)
			.moveX(6, true)
			.sizeX(-12)
			.sizeX(-12, true));
		_spout.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("hopper_outside").ret()
			.sizeY(-12)
			.sizeY(-12, true)
			.moveY(12, true)
			.moveZ(4)
			.moveX(6, true)
			.sizeX(-12)
			.sizeX(-12, true));

		_spout.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("hopper_outside").ret()
			.sizeY(-12)
			.sizeY(-12, true)
			.moveY(12, true)
			.moveX(6, true)
			.sizeZ(-12)
			.sizeX(-12, true));
		_spout.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("hopper_outside").ret()
			.sizeY(-12)
			.sizeY(-12, true)
			.moveY(12, true)
			.moveX(4)
			.moveX(6, true)
			.sizeZ(-12)
			.sizeX(-12, true));

		_spout.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("hopper_outside").ret()
			.moveX(6, true)
			.sizeX(-12)
			.sizeX(-12, true)
			.moveY(6, true)
			.sizeZ(-12)
			.sizeY(-12, true));
		_spout.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("hopper_outside").ret()
			.moveX(6, true)
			.sizeX(-12)
			.sizeX(-12, true)
			.moveY(6, true)
			.sizeZ(-12)
			.sizeY(-12, true)
			.moveY(4));

		return _spout;
	}
}