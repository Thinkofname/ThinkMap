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

using Lambda;

class BlockBed {
	
	private static var _modelBottom : Model;
	public static var modelBottom(get, never) : Model;
	
	static function get_modelBottom() : Model {
		if (_modelBottom != null) return _modelBottom;

		var _modelBottom = new Model();
		var face = ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("bed_feet_top").ret()
			.moveY(9);
		face.vertices.foreach(function(v) {
			var y = v.textureY;
			v.textureY = v.textureX;
			v.textureX = y;
			return true;
		});
		_modelBottom.faces.push(face);
		_modelBottom.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("bed_feet_end").ret()
			.sizeY( -7)
			.sizeY( -7, true)
			.moveY(7, true));
		var face = ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("bed_feet_side").ret()
			.sizeY( -7)
			.sizeY( -7, true)
			.moveY(7, true)
			.moveX(16);
		face.vertices.foreach(function(v) {
			v.textureX = v.textureX == 1 ? 0 : 1;
			return true;
		});
		_modelBottom.faces.push(face);
		_modelBottom.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("bed_feet_side").ret()
			.sizeY( -7)
			.sizeY( -7, true)
			.moveY(7, true));
		return _modelBottom;
	}
	
	private static var _modelTop : Model;
	public static var modelTop(get, never) : Model;
	
	static function get_modelTop() : Model {
		if (_modelTop != null) return _modelTop;

		var _modelTop = new Model();
		var face = ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("bed_head_top").ret()
			.moveY(9);
		face.vertices.foreach(function(v) {
			var y = v.textureY;
			v.textureY = v.textureX;
			v.textureX = y;
			return true;
		});
		_modelTop.faces.push(face);
		_modelTop.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("bed_head_end").ret()
			.sizeY( -7)
			.sizeY( -7, true)
			.moveY(7, true)
			.moveZ(16));
		var face = ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("bed_head_side").ret()
			.sizeY( -7)
			.sizeY( -7, true)
			.moveY(7, true)
			.moveX(16);
		face.vertices.foreach(function(v) {
			v.textureX = v.textureX == 1 ? 0 : 1;
			return true;
		});
		_modelTop.faces.push(face);
		_modelTop.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("bed_head_side").ret()
			.sizeY( -7)
			.sizeY( -7, true)
			.moveY(7, true));		
		return _modelTop;
	}
}