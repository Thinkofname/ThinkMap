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

class BlockAnvil {

	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model {
		if (_model != null) return _model;

		_model = new Model();
		_model.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("anvil_top_damaged_0").ret()
			.moveX(3)
			.moveY(16)
			.sizeX(-6)
			.moveX(3, true)
			.sizeX(-6, true));
		_model.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("anvil_base").ret()
			.moveX(4)
			.moveZ(3)
			.moveY(5)
			.sizeX(-8)
			.sizeZ(-6)
			.moveX(4, true)
			.moveY(3, true)
			.sizeX(-8, true)
			.sizeY(-6, true));
		_model.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("anvil_base").ret()
			.moveX(2)
			.moveZ(2)
			.moveY(4)
			.sizeX(-4)
			.sizeZ(-4)
			.moveX(2, true)
			.moveY(2, true)
			.sizeX(-4, true)
			.sizeY(-4, true));
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(10)
			.moveX(13)
			.sizeY(-10)
			.moveY(10, true)
			.sizeY(-10, true));
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(5)
			.moveZ(4)
			.moveX(10)
			.sizeY(-11)
			.sizeZ(-8)
			.moveX(4, true)
			.moveY(5, true)
			.sizeX(-8, true)
			.sizeY(-11, true));
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(4)
			.moveZ(3)
			.moveX(12)
			.sizeY(-15)
			.sizeZ(-6)
			.moveX(3, true)
			.moveY(4, true)
			.sizeX(-6, true)
			.sizeY(-15, true));
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("anvil_base").ret()
			.moveZ(2)
			.moveX(14)
			.sizeY(-12)
			.sizeZ(-4)
			.moveX(2, true)
			.sizeX(-4, true)
			.sizeY(-12, true));
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(10)
			.moveX(3)
			.moveZ(16)
			.sizeY(-10)
			.sizeX(-6)
			.moveX(3, true)
			.moveY(10, true)
			.sizeX(-6, true)
			.sizeY(-10, true));
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(5)
			.moveX(6)
			.moveZ(12)
			.sizeY(-11)
			.sizeX(-12)
			.moveX(6, true)
			.moveY(5, true)
			.sizeX(-12, true)
			.sizeY(-11, true));
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(4)
			.moveX(4)
			.moveZ(13)
			.sizeY(-15)
			.sizeX(-8)
			.moveX(4, true)
			.moveY(4, true)
			.sizeX(-8, true)
			.sizeY(-15, true));
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("anvil_base").ret()
			.moveX(2)
			.moveZ(14)
			.sizeY(-12)
			.sizeX(-4)
			.moveX(2, true)
			.sizeX(-4, true)
			.sizeY(-12, true));
		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(10)
			.moveX(3)
			.sizeY(-10)
			.moveY(10, true)
			.sizeY(-10, true));
		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(5)
			.moveZ(4)
			.moveX(6)
			.sizeY(-11)
			.sizeZ(-8)
			.moveX(4, true)
			.moveY(5, true)
			.sizeX(-8, true)
			.sizeY(-11, true));
		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(4)
			.moveZ(3)
			.moveX(4)
			.sizeY(-15)
			.sizeZ(-6)
			.moveX(3, true)
			.moveY(4, true)
			.sizeX(-6, true)
			.sizeY(-15, true));
		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("anvil_base").ret()
			.moveZ(2)
			.moveX(2)
			.sizeY(-12)
			.sizeZ(-4)
			.moveX(2, true)
			.sizeX(-4, true)
			.sizeY(-12, true));
		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(10)
			.moveX(3)
			.sizeY(-10)
			.sizeX(-6)
			.moveX(3, true)
			.moveY(10, true)
			.sizeX(-6, true)
			.sizeY(-10, true));
		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(5)
			.moveX(6)
			.moveZ(4)
			.sizeY(-11)
			.sizeX(-12)
			.moveX(6, true)
			.moveY(5, true)
			.sizeX(-12, true)
			.sizeY(-11, true));
		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("anvil_base").ret()
			.moveY(4)
			.moveX(4)
			.moveZ(3)
			.sizeY(-15)
			.sizeX(-8)
			.moveX(4, true)
			.moveY(4, true)
			.sizeX(-8, true)
			.sizeY(-15, true));
		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("anvil_base").ret()
			.moveX(2)
			.moveZ(2)
			.sizeY(-12)
			.sizeX(-4)
			.moveX(2, true)
			.sizeX(-4, true)
			.sizeY(-12, true));
		_model.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("anvil_base").ret()
			.moveX(2)
			.moveZ(2)
			.sizeX(-4)
			.sizeZ(-4)
			.moveX(2, true)
			.moveY(2, true)
			.sizeX(-4, true)
			.sizeY(-4, true));
		_model.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("anvil_base").ret()
			.moveX(3)
			.moveY(10)
			.sizeX(-6)
			.moveX(3, true)
			.sizeX(-6, true));
		return _model;
	}
}
