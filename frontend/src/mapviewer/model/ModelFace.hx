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
import js.html.Uint16Array;
import js.html.Uint8Array;
import mapviewer.block.Face;

class ModelFace {
	
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
	
	public var texture : String;
	public var vertices : Array<ModelVertex>;
	public var face : Face;
	public var r : Int = 255;
	public var g : Int = 255;
	public var b : Int = 255;
	public var cullable : Bool = false;
	public var width : Float = 16;
	public var height : Float = 16;
	
	public function new(face : Face) {
		vertices = new Array();
		this.face = face;
	}
	
	public static function create(face : Face, texture : String, 
			x : Float, y : Float, w : Float, h : Float,
			off : Float, ?cullable : Bool = false) : ModelFace {
		var f = new ModelFace(face);
		f.texture = texture;
		for (vert in defaultFaces[face.name]) {
			f.vertices.push(vert.clone());
		}
		f.cullable = cullable;
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
		width = w;
		height = h;
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
	
	public function forEach(it : ModelVertex -> Void) : ModelFace {
		for (v in vertices) {
			it(v);
		}
		return this;
	}
}