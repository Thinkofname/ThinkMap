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
	
	static function get_model() : Model {
		if (_model != null) return _model;

		var modelBottom = new Model();
		//Bottom
		modelBottom.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("chest_side_front").ret()
			.moveZ(15)
			.sizeX(-2, true)
			.sizeX(-2)
			.sizeY(-6, true)
			.sizeY(-6)
			.moveX(1));
		modelBottom.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("chest_side").ret()
			.moveZ(1)
			.sizeX(-2, true)
			.sizeX(-2)
			.sizeY(-6, true)
			.sizeY(-6)
			.moveX(1));
		modelBottom.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("chest_side").ret()
			.sizeX(-2, true)
			.sizeZ(-2)
			.sizeY(-6, true)
			.sizeY(-6)
			.moveX(15)
			.moveZ(1));
		modelBottom.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("chest_side").ret()
			.sizeX(-2, true)
			.sizeZ(-2)
			.sizeY(-6, true)
			.sizeY(-6)
			.moveX(1)
			.moveZ(1));
		modelBottom.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("chest_bottom_top").ret()
			.moveY(10)
			.sizeX(-2)
			.sizeZ(-2)
			.sizeX(-2, true)
			.sizeY(-2, true)
			.moveX(1)
			.moveZ(1));
		modelBottom.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("chest_top").ret()
			.sizeX(-2)
			.sizeZ(-2)
			.sizeX(-2, true)
			.sizeY(-2, true)
			.moveX(1)
			.moveZ(1));

		var modelLid = new Model();

		//Top
		modelLid.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("chest_side_front").ret()
			.moveZ(15)
			.sizeX(-2, true)
			.sizeX(-2)
			.sizeY(-11, true)
			.sizeY(-11)
			.moveX(1)
			.moveY(10, true));
		modelLid.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("chest_side").ret()
			.moveZ(1)
			.sizeX(-2, true)
			.sizeX(-2)
			.sizeY(-11, true)
			.sizeY(-11)
			.moveX(1)
			.moveY(10, true));
		modelLid.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("chest_side").ret()
			.sizeX(-2, true)
			.sizeZ(-2)
			.sizeY(-11, true)
			.sizeY(-11)
			.moveX(15)
			.moveZ(1)
			.moveY(10, true));
		modelLid.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("chest_side").ret()
			.sizeX(-2, true)
			.sizeZ(-2)
			.sizeY(-11, true)
			.sizeY(-11)
			.moveX(1)
			.moveZ(1)
			.moveY(10, true));
		modelLid.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("chest_top").ret()
			.moveY(5)
			.sizeX(-2)
			.sizeZ(-2)
			.sizeX(-2, true)
			.sizeY(-2, true)
			.moveX(1)
			.moveZ(1));
		modelLid.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("chest_top_bottom").ret()
			.sizeX(-2)
			.sizeZ(-2)
			.sizeX(-2, true)
			.sizeY(-2, true)
			.moveX(1)
			.moveZ(1));

		//Lock
		modelLid.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("chest_lock").ret()
			.moveZ(1)
			.sizeX(-14, true)
			.sizeX(-14)
			.sizeY(-12, true)
			.sizeY(-12)
			.moveX(7)
			.moveY(-2)
			.moveX(1, true)
			.moveY(1, true)
			.moveZ(15));
		modelLid.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("chest_lock").ret()
			.sizeX(-15, true)
			.sizeZ(-15)
			.sizeY(-12, true)
			.sizeY(-12)
			.moveX(9)
			.moveY(-2)
			.moveY(1, true)
			.moveZ(15));
		modelLid.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("chest_lock").ret()
			.sizeX(-15, true)
			.sizeZ(-15)
			.sizeY(-12, true)
			.sizeY(-12)
			.moveX(7)
			.moveY(-2)
			.moveY(1, true)
			.moveZ(15));
		modelLid.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("chest_lock").ret()
			.moveY(4)
			.sizeX(-14)
			.sizeZ(-15)
			.sizeX(-14, true)
			.sizeY(-15, true)
			.moveX(7)
			.moveY(-2)
			.moveX(1, true)
			.moveZ(15));
		modelLid.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("chest_lock").ret()
			.sizeX(-14)
			.sizeZ(-15)
			.sizeX(-14, true)
			.sizeY(-15, true)
			.moveX(7)
			.moveY(-2)
			.moveX(3, true)
			.moveZ(15));
		return modelBottom.clone().join(modelLid, 0, 9, 0);
	}
}