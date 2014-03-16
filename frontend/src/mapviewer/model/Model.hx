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
package mapviewer.model;
import js.html.Float32Array;
import mapviewer.block.Face;
import mapviewer.model.ModelFace;
import mapviewer.model.ModelVertex;
import mapviewer.renderer.LightInfo;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.renderer.webgl.glmatrix.Quat;
import mapviewer.utils.Chainable;
import mapviewer.world.Chunk;

class Model {
	public var faces : Array<ModelFace>;
	
	public function new() {
		faces = new Array();
	}
	
	public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {
		var light = new LightInfo(chunk.getLight(x, y, z), chunk.getSky(x, y, z));
		for (face in faces) {
			var texture = Main.blockTextureInfo[face.texture];
			for (i in 0 ... 3) {
				var vert = face.vertices[i];
				builder
					.position(x + vert.x, y + vert.y, z + vert.z)
					.colour(face.r, face.g, face.b)
					.tex(vert.textureX, vert.textureY)
					.texId(texture.start, texture.end)
					.lighting(light.light, light.sky);
			}
			for (i in 0 ... 3) {				
				var vert = face.vertices[3 - i];
				builder
					.position(x + vert.x, y + vert.y, z + vert.z)
					.colour(face.r, face.g, face.b)
					.tex(vert.textureX, vert.textureY)
					.texId(texture.start, texture.end)
					.lighting(light.light, light.sky);
			}
		}
	}
	
	private static var rotHelper = [Face.LEFT, Face.FRONT, Face.RIGHT, Face.BACK];
	
	public function rotateY(deg : Float) : Model {
		rotate(deg, [0.0, 1.0, 0.0]);		
		for (face in faces) {
			var idx = rotHelper.indexOf(face.face);
			if (idx != -1) {
				face.face = rotHelper[(idx + Std.int(deg/90)) % rotHelper.length];
			}
		}
		return this;
	}
	
	public function rotateX(deg : Float) : Model {
		rotate(deg, [1.0, 0.0, 0.0]);
		return this;
	}
	
	public function rotateZ(deg : Float) : Model {
		rotate(deg, [0.0, 0.0, 1.0]);	
		return this;	
	}
	
	private function rotate(deg : Float, axis : Array<Float>) {
		var q = Quat.create();
		var t1 = Quat.create();
		var t2 = Quat.create();
		q.setAxisAngle(axis, Math.PI / 180 * deg);
		untyped quat.conjugate(t1, q);
		for (face in faces) {
			for (vert in face.vertices) {
				var vec = [vert.x - 0.5, vert.y - 0.5, vert.z - 0.5, 0];
				untyped quat.multiply(t2, quat.multiply(t2, t1, vec), q);
				vert.x = t2[0] + 0.5;
				vert.y = t2[1] + 0.5;
				vert.z = t2[2] + 0.5;
			}
		}
	}
	
	/**
	 * Resets the texture coordinates for the model based on the 
	 * vertex's position
	 */
	public function realignTextures() {		
		for (face in faces) {			
			// Correct texture positions
			for (vert in face.vertices) {					
				if (face.face != Face.LEFT && face.face != Face.RIGHT) {
					vert.textureX = vert.x;
				}
				if (face.face == Face.LEFT || face.face == Face.RIGHT) {			
					vert.textureX = vert.z;				
				} else if (face.face == Face.TOP || face.face == Face.BOTTOM) {					
					vert.textureY = 1 - vert.z;				
				}
				if (face.face != Face.TOP && face.face != Face.BOTTOM) {			
					vert.textureY = 1 - vert.y;						
				}
			}
		}
	}
	
	/**
	 * Flips the model upside down
	 */
	public function flipModel() {
		for (face in faces) {		
			for (vert in face.vertices) {
				vert.y = 1 - vert.y;
			}
			if (face.face == Face.TOP) face.face = Face.BOTTOM;
			if (face.face == Face.BOTTOM) face.face = Face.TOP;
			var temp = face.vertices[2];
			face.vertices[2] = face.vertices[1];
			face.vertices[1] = temp;
		}
	}
	
	/**
	 * Joins this model with the other models optionally offset.
	 * @param	other The other model
	 * @param	?ox   The amount to offset by on the x axis
	 * @param	?oy   The amount to offset by on the y axis
	 * @param	?oz   The amount to offset by on the z axis
	 * @return  This model
	 */
	public function join(other : Model, ?ox : Float = 0, ?oy : Float = 0, ?oz : Float = 0) : Model {
		for (face in other.faces) {
			var newFace = new ModelFace(face.face);
			newFace.texture = face.texture;
			newFace.r = face.r;
			newFace.g = face.g;
			newFace.b = face.b;
			faces.push(newFace);
			for (vert in face.vertices) {
				var newVert = vert.clone();
				newVert.x += ox / 16;
				newVert.y += oy / 16;
				newVert.z += oz / 16;
				newFace.vertices.push(newVert);
			}
		}
		return this;
	}
	
	// Does nothing - used by clone as a default getter
	private static function noopTextureGetter(texture : String) : String return texture;
	
	/**
	 * Creates a copy of this model optionally passing the texture of 
	 * each face to getTexture and replacing it with the result
	 * @param	?getTexture The texture getter to use
	 * @return  The new model
	 */
	public function clone(?getTexture : String -> String) : Model {
		if (getTexture == null) { getTexture = noopTextureGetter; }
		var out = new Model();
		for (face in faces) {
			var newFace = new ModelFace(face.face);
			newFace.texture = getTexture(face.texture);
			newFace.r = face.r;
			newFace.g = face.g;
			newFace.b = face.b;
			out.faces.push(newFace);
			for (vert in face.vertices) {
				newFace.vertices.push(vert.clone());
			}
		}
		return out;
	}
	
}