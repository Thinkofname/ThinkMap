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