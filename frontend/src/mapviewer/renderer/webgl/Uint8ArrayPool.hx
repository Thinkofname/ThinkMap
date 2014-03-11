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
package mapviewer.renderer.webgl;
import js.html.Uint8Array;
import mapviewer.logging.Logger;

/**
 * A simple pool of Uint8Arrays
 */
class Uint8ArrayPool {
	private static var freeArrays = new List<Uint8Array>();
	private static var logger = new Logger("Uint8ArrayPool");
	
	/**
	 * Gets or creates a Uint8Array that has at least size bytes
	 * capacity
	 */
	public static function getArray(size : Int) : Uint8Array {
		var ret : Uint8Array = null;
		for (arr in freeArrays) {
			if (arr.byteLength >= size) {
				ret = arr;
				break;
			}
		}
		if (ret != null) {
			freeArrays.remove(ret);
		} else {
			ret = new Uint8Array(size);
			logger.debug('Alloc $size');
		}
		return ret;
	}
	
	/**
	 * Returns the array to the pool
	 */
	public static function freeArray(array : Uint8Array) {
		freeArrays.add(array);
	}
}