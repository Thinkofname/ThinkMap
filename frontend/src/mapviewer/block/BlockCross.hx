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
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.world.Chunk;
import mapviewer.block.Block.Face;
import mapviewer.model.Model;
import mapviewer.model.Model.ModelFace;

using Lambda;

class BlockCross extends Block {

	public function new() {	
		super();
	}
	
	override public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {		
		if (model == null) {
			var r = 255;
			var g = 255;
			var b = 255;
			if (forceColour) {
				r = (colour >> 16) & 0xFF;
				g = (colour >> 8) & 0xFF;
				b = colour & 0xFF;
			}
			model = new Model();
			var face = ModelFace.create(Face.FRONT, texture, 0, 0, 16, 16, 0)
				.colour(r, g, b);
			for (e in face.vertices) {
				if (e.x == 1) e.z = 1;
			};
			model.faces.push(face);
			
			face = ModelFace.create(Face.BACK, texture, 0, 0, 16, 16, 0)
				.colour(r, g, b);
			for (e in face.vertices) {
				if (e.x == 1) e.z = 1;
			};
			model.faces.push(face);
			
			face = ModelFace.create(Face.FRONT, texture, 0, 0, 16, 16, 0)
				.colour(r, g, b);
			for (e in face.vertices) {
				if (e.x == 0) e.z = 1;
			};
			model.faces.push(face);
			
			face = ModelFace.create(Face.BACK, texture, 0, 0, 16, 16, 0)
				.colour(r, g, b);
			for (e in face.vertices) {
				if (e.x == 0) e.z = 1;
			};
			model.faces.push(face);
		}
		super.render(builder, x, y, z, chunk);	
	}
	
}