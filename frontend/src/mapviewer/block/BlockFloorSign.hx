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

class BlockFloorSign {
	
	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model {
		if (_model != null) return _model;
		var _model = new Model();		
		//Post
		var post = new Model();
		post.faces.push(ModelFace.create(Face.FRONT, "log_oak", 0, 0, 1, 9, 1)
			.textureSize(0, 0, 2, 12));
		post.faces.push(ModelFace.create(Face.BACK, "log_oak", 0, 0, 1, 9, 0)
			.textureSize(0, 0, 2, 12));
		post.faces.push(ModelFace.create(Face.LEFT, "log_oak", 0, 0, 1, 9, 1)
			.textureSize(0, 0, 2, 12));
		post.faces.push(ModelFace.create(Face.RIGHT, "log_oak", 0, 0, 1, 9, 0)
			.textureSize(0, 0, 2, 12));
			
		return _model.join(BlockWallSign.model, 0, 5, 7.5).join(post, 7.5, 0, 7.5);
	}
}