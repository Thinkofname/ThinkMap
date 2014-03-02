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