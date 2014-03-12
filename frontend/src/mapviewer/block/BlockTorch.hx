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

import mapviewer.block.Block.Face;
import mapviewer.model.Model;

class BlockTorch {

	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model { //torch_on
		if (_model != null) return _model;
		_model = new Model();
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("torch_on").ret()
			.moveX(9)
			.sizeY(-6)
			.sizeY( -6, true)
			.moveY( 6, true)
			.sizeZ( -14)
			.sizeX( -14, true)
			.moveZ(7)
			.moveX(7, true));
		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("torch_on").ret()
			.moveX(7)
			.sizeY(-6)
			.sizeY(-6, true)
			.moveY( 6, true)
			.sizeZ( -14)
			.sizeX( -14, true)
			.moveZ(7)
			.moveX(7, true));
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("torch_on").ret()
			.moveZ(9)
			.sizeY(-6)
			.sizeY(-6, true)
			.moveY( 6, true)
			.sizeX( -14)
			.sizeX( -14, true)
			.moveX(7)
			.moveX(7, true));
		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("torch_on").ret()
			.moveZ(7)
			.sizeY(-6)
			.sizeY(-6, true)
			.moveY( 6, true)
			.sizeX( -14)
			.sizeX( -14, true)
			.moveX(7)
			.moveX(7, true));
		_model.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("torch_on").ret()
			.moveY(10)
			.sizeX( -14)
			.sizeX( -14, true)
			.moveX(7)
			.moveX(7, true)
			.sizeZ( -14)
			.sizeY( -14, true)
			.moveZ(7)
			.moveY(6, true));
		return _model;
	}
}