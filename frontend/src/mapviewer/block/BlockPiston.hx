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

class BlockPiston {	

	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model {
		if (_model != null) return _model;

		_model = new Model();
		
		_model.faces.push(ModelFace.create(Face.BACK, "piston_bottom", 0, 0, 16, 16, 0, true));
		_model.faces.push(ModelFace.create(Face.FRONT, "piston_inner", 0, 0, 16, 16, 12, false));
		_model.faces.push(ModelFace.create(Face.LEFT, "piston_side", 0, 0, 12, 16, 16, true).forEach(function(v) {
			var x = v.textureX;
			v.textureX = v.textureY;
			v.textureY = x + 4/16;
		}));
		_model.faces.push(ModelFace.create(Face.RIGHT, "piston_side", 0, 0, 12, 16, 0, true).forEach(function(v) {
			var x = v.textureX;
			v.textureX = v.textureY;
			v.textureY = x + 4/16;
		}));
		_model.faces.push(ModelFace.create(Face.TOP, "piston_side", 0, 0, 16, 12, 16, true).forEach(function(v) {
			v.textureY = 1 - v.textureY;
		}));
		_model.faces.push(ModelFace.create(Face.BOTTOM, "piston_side", 0, 0, 16, 12, 0, true).forEach(function(v) {
			v.textureY = 1 - v.textureY;
		}));
		
		return _model;
	}	

	private static var _head : Model;
	public static var head(get, never) : Model;
	
	static function get_head() : Model {
		if (_head != null) return _head;

		_head = new Model();
		
		_head.faces.push(ModelFace.create(Face.BACK, "piston_top_normal", 0, 0, 16, 16, 0, false));
		_head.faces.push(ModelFace.create(Face.FRONT, "piston_top_%type%", 0, 0, 16, 16, 4, true));
		_head.faces.push(ModelFace.create(Face.LEFT, "piston_side", 0, 0, 4, 16, 16, true).forEach(function(v) {
			var x = v.textureX;
			v.textureX = v.textureY;
			v.textureY = x;
		}));
		_head.faces.push(ModelFace.create(Face.RIGHT, "piston_side", 0, 0, 4, 16, 0, true).forEach(function(v) {
			var x = v.textureX;
			v.textureX = v.textureY;
			v.textureY = x;
		}));
		_head.faces.push(ModelFace.create(Face.TOP, "piston_side", 0, 0, 16, 4, 16, true).forEach(function(v) {
			v.textureY = 1 - v.textureY;
			v.textureY -= 12 / 16;
		}));
		_head.faces.push(ModelFace.create(Face.BOTTOM, "piston_side", 0, 0, 16, 4, 0, true).forEach(function(v) {
			v.textureY = 1 - v.textureY;
			v.textureY -= 12 / 16;
		}));
		
		return _head;
	}
}