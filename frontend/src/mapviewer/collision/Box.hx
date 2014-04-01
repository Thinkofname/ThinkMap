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
package mapviewer.collision;
import mapviewer.block.Face;
import mapviewer.model.Model;

class Box {
	
	public var x : Float;
	public var y : Float;
	public var z : Float;
	public var w : Float;
	public var h : Float;
	public var d : Float;

	public function new(x : Float, y : Float, z : Float, w : Float, h : Float, d : Float) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.d = d;
	}	
	
    /**
     * Checks whether this box collides with the box created from the
     * parameters.
     */
	public function checkBox(ox : Float, oy : Float, oz : Float, ow : Float, oh : Float, od : Float) : Bool {
		var rx = x - (w / 2.0);
		var ry = y;
		var rz = z - (d / 2.0);
		return !(rx + w < ox || rx > ox + ow || ry + h < oy || ry > oy + oh || rz +
			d < oz || rz > oz + od);
	}
	
	public function checkModel(ox : Int, oy : Int, oz : Int, model : Model) : Bool {
		for (face in model.faces) {
			var x = Math.POSITIVE_INFINITY;
			var y = Math.POSITIVE_INFINITY;
			var z = Math.POSITIVE_INFINITY;
			var w = Math.NEGATIVE_INFINITY;
			var h = Math.NEGATIVE_INFINITY;
			var d = Math.NEGATIVE_INFINITY;
			for (v in face.vertices) {
				if (v.x < x) x = v.x;
				if (v.y < y) y = v.y;
				if (v.z < z) z = v.z;
				if (v.x > w) w = v.x;
				if (v.y > h) h = v.y;
				if (v.z > d) d = v.z;
			}
			if (x == w) {
				x -= 1 / 32;
				w += 1 / 32;
			} else if (y == h) {
				y -= 1 / 32;
				h += 1 / 32;
			} else if (z == d) {
				z -= 1 / 32;
				d += 1 / 32;
			}
			if (checkBox(ox + x, oy + y, oz + z, w-x, h-y, d-z)) return true;
		}
		return false;
	}
}