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
import mapviewer.block.Block.Face;
import mapviewer.model.Model.ModelFace;
import mapviewer.model.Model.ModelVertex;
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

class ModelFace implements Chainable {
	
	private static var defaultFaces: Map <String, Array<ModelVertex>> = [
		"top" => [
		  new ModelVertex(0, 0, 0, 0, 0),
		  new ModelVertex(1, 0, 0, 1, 0),
		  new ModelVertex(0, 0, 1, 0, 1),
		  new ModelVertex(1, 0, 1, 1, 1)
		],
		"bottom" => [
		  new ModelVertex(0, 0, 0, 0, 0),
		  new ModelVertex(0, 0, 1, 0, 1),
		  new ModelVertex(1, 0, 0, 1, 0),
		  new ModelVertex(1, 0, 1, 1, 1)
		],
		"left" => [
		  new ModelVertex(0, 0, 0, 1, 1),
		  new ModelVertex(0, 0, 1, 0, 1),
		  new ModelVertex(0, 1, 0, 1, 0),
		  new ModelVertex(0, 1, 1, 0, 0)
		],
		"right" => [
		  new ModelVertex(0, 0, 0, 0, 1),
		  new ModelVertex(0, 1, 0, 0, 0),
		  new ModelVertex(0, 0, 1, 1, 1),
		  new ModelVertex(0, 1, 1, 1, 0)	
		],
		"front" => [
		  new ModelVertex(0, 0, 0, 0, 1),
		  new ModelVertex(0, 1, 0, 0, 0),
		  new ModelVertex(1, 0, 0, 1, 1),
		  new ModelVertex(1, 1, 0, 1, 0)
		],
		"back" => [
		  new ModelVertex(0, 0, 0, 1, 1),
		  new ModelVertex(1, 0, 0, 0, 1),
		  new ModelVertex(0, 1, 0, 1, 0),
		  new ModelVertex(1, 1, 0, 0, 0)
		]
	];
	
	@:chain public var texture : String;
	public var vertices : Array<ModelVertex>;
	public var face : Face;
	@:chain public var r : Int = 255;
	@:chain public var g : Int = 255;
	@:chain public var b : Int = 255;
	
	public function new(face : Face) {
		vertices = new Array();
		this.face = face;
	}
	
	@:deprecated("Use ModelFace.create instead")
	public static function fromFace(face : Face) : ModelFace {
		var f = new ModelFace(face);
		for (vert in defaultFaces[face.name]) {
			f.vertices.push(vert.clone());
		}
		return f;
	}
	
	public static function create(face : Face, texture : String, 
			x : Float, y : Float, w : Float, h : Float,
			off : Float) : ModelFace {
		var f = new ModelFace(face);
		f.texture = texture;
		for (vert in defaultFaces[face.name]) {
			f.vertices.push(vert.clone());
		}
		f.offset(off);
		f.size(x, y, w, h);
		return f;		
	}
	
	public function offset(off : Float) {
		// What gets changed depends on the face's face
		if (face == Face.TOP || face == Face.BOTTOM) {
			// X, Z
			for (vert in vertices) vert.y = off / 16;
		} else if (face == Face.LEFT || face == Face.RIGHT) {
			// Z, Y
			for (vert in vertices) vert.x = off / 16;
		} else if (face == Face.FRONT || face == Face.BACK) {
			// X, Y
			for (vert in vertices) vert.z = off / 16;
		}		
	}
	
	/**
	 * Resize this face. Also updates the texture position.
	 */
	public function size(x : Float, y : Float, w : Float, h : Float) : ModelFace {		
		// What gets changed depends on the face's face
		if (face == Face.TOP || face == Face.BOTTOM) {
			// X, Z
			sizeIndex(0, 2, x, y, w, h);
		} else if (face == Face.LEFT || face == Face.RIGHT) {
			// Z, Y
			sizeIndex(2, 1, x, y, w, h);
		} else if (face == Face.FRONT || face == Face.BACK) {
			// X, Y
			sizeIndex(0, 1, x, y, w, h);
		}
		return textureSize(x, y, w, h);
	}
	
	public function textureSize(x : Float, y : Float, w : Float, h : Float) : ModelFace {
		sizeIndex(3, 4, x, y, w, h);
		return this;
	}
	
	private function sizeIndex(i1 : Int, i2 : Int, x : Float, y : Float, w : Float, h : Float) {
		var sx : Float = 16;
		var sy : Float = 16;
		var lx : Float = -16;
		var ly : Float = -16;
		// Calculate the min and max values
		for (vert in vertices) {
			if (vert[i1] < sx) sx = vert[i1];
			if (vert[i1] > lx) lx = vert[i1];
			if (vert[i2] < sy) sy = vert[i2];
			if (vert[i2] > ly) ly = vert[i2];
		}
		// Update the values
		for (vert in vertices) {
			if (vert[i1] == sx) vert[i1] = x / 16;
			if (vert[i1] == lx) vert[i1] = (x + w) / 16;
			if (vert[i2] == sy) vert[i2] = y / 16;
			if (vert[i2] == ly) vert[i2] = (y + h) / 16;
		}
	}
	
	public function colour(r : Int, g : Int, b : Int) : ModelFace {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}

	@:deprecated("Use ModelFace.create or .size/.textureSize instead")
	public function moveY(a : Float, ?tex : Bool = false) : ModelFace {
		for (vert in vertices) {
			if (!tex)
				vert.y += a / 16;
			else
				vert.textureY += a / 16;
		}
		return this;
	}

	@:deprecated("Use ModelFace.create or .size/.textureSize instead")
	public function moveX(a : Float, ?tex : Bool = false) : ModelFace {
		for (vert in vertices) {
			if (!tex)
				vert.x += a / 16;
			else
				vert.textureX += a / 16;
		}
		return this;
	}

	@:deprecated("Use ModelFace.create or .size/.textureSize instead")
	public function moveZ(a : Float, ?tex : Bool = false) : ModelFace {
		for (vert in vertices) {
			vert.z += a / 16;
		}
		return this;
	}

	@:deprecated("Use ModelFace.create or .size/.textureSize instead")
	public function sizeY(a : Float, ?tex : Bool = false) : ModelFace {
		var largest : Float = 0;
		if (!tex) {
			for (vert in vertices) {
				if (vert.y > largest) largest = vert.y;
			}
			for (vert in vertices) {
				if (vert.y == largest) {
					vert.y += a / 16;
				}
			}
		} else {
			for (vert in vertices) {
				if (vert.textureY > largest) largest = vert.textureY;
			}
			for (vert in vertices) {
				if (vert.textureY == largest) {
					vert.textureY += a / 16;
				}
			}
		}
		return this;
	}

	@:deprecated("Use ModelFace.create or .size/.textureSize instead")
	public function sizeX(a : Float, ?tex : Bool = false) : ModelFace {
		var largest : Float = 0;
		if (!tex) {
			for (vert in vertices) {
				if (vert.x > largest) largest = vert.x;
			}
			for (vert in vertices) {
				if (vert.x == largest) {
					vert.x += a / 16;
				}
			}
		} else {
			for (vert in vertices) {
				if (vert.textureX > largest) largest = vert.textureX;
			}
			for (vert in vertices) {
				if (vert.textureX == largest) {
					vert.textureX += a / 16;
				}
			}
		}
		return this;
	}

	@:deprecated("Use ModelFace.create or .size/.textureSize instead")
	public function sizeZ(a : Float, ?tex : Bool = false) : ModelFace {
		var largest : Float = 0;
		for (vert in vertices) {
			if (vert.z > largest) largest = vert.z;
		}
		for (vert in vertices) {
			if (vert.z == largest) {
				vert.z += a / 16;
			}
		}
		return this;
	}
}

abstract ModelVertex(Float32Array) {
	
	public var x(get, set) : Float;	
	inline function get_x() : Float { return this[0]; }	
	inline function set_x(v : Float) : Float { return this[0] = v; }
	
	public var y(get, set) : Float;	
	inline function get_y() : Float { return this[1]; }	
	inline function set_y(v : Float) : Float { return this[1] = v; }
	
	public var z(get, set) : Float;	
	inline function get_z() : Float { return this[2]; }	
	inline function set_z(v : Float) : Float { return this[2] = v; }
	
	public var textureX(get, set) : Float;	
	inline function get_textureX() : Float { return this[3]; }	
	inline function set_textureX(v : Float) : Float { return this[3] = v; }
	
	public var textureY(get, set) : Float;	
	inline function get_textureY() : Float { return this[4]; }	
	inline function set_textureY(v : Float) : Float { return this[4] = v; }
	
	@:arrayAccess public inline function arrayAccess(i : Int) : Float { return this[i]; }
	@:arrayAccess public inline function arrayWrite(i : Int, v : Float) : Float { return this[i] = v; }
	
	inline public function new(x : Float, y : Float, z : Float, textureX : Float, textureY : Float) {
		this = new Float32Array([x, y, z, textureX, textureY]);
	}
	
	inline public function clone() : ModelVertex {
		return cast new Float32Array(this);
	}
}