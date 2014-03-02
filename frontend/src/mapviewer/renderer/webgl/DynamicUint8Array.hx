package mapviewer.renderer.webgl;
import js.html.DataView;
import js.html.Uint16Array;
import js.html.Uint8Array;

/**
 * DynamicUint8Array is a resizable Uint8Array with support
 * for other types (e.g. Float and Unsigned Short). The 
 * Uint8Arrays are pooled to prevent unnecessary allocations.
 * This must be freed after use.
 */
class DynamicUint8Array {
	
	private static var endianness : Bool =
	(new DataView(new Uint16Array([1]).buffer).getInt8(0) == 1 ? true : false);
	
	private var buffer : Uint8Array;
	private var data : DataView;
	private var offset : Int = 0;

	/**
	 * Create a DynamicUint8Array with a starting size. If
	 * the size is less than 16 then it will be set to 16.
	 */
	public function new(size : Int) {
		if (size < 16) size = 16;
		buffer = Uint8ArrayPool.getArray(size);
		data = new DataView(buffer.buffer);
	}	
	
	/**
	 * Append one unsigned byte to the array
	 */
	public function add(val : Int) {
		if (offset >= buffer.length) {
			resize();
		}
		buffer[offset++] = val;
	}
	
	/**
	 * Append one unsigned short to the array
	 */
	public function addUnsignedShort(val : Int) {
		if (offset + 1 >= buffer.length) {
			resize();
		}
		data.setUint16(offset, val, endianness);
		offset += 2;
	}
	
	/**
	 * Append one 32 bit float to the array
	 */
	public function addFloat(val : Float) {
		if (offset + 3 >= buffer.length) {
			resize();
		}
		data.setFloat32(offset, val, endianness);
		offset += 4;
	}
	
	private function resize() {
		var newList = Uint8ArrayPool.getArray(buffer.length * 2);
		newList.set(buffer);
		Uint8ArrayPool.freeArray(buffer);
		buffer = newList;
		data = new DataView(buffer.buffer);
	}
	
	/**
	 * Release the internal buffer used by this array
	 */
	public function free() {
		Uint8ArrayPool.freeArray(buffer);
		buffer = null;
		data = null;
	}
	
	/**
	 * Returns a Uint8Array containing the data created by
	 * this array
	 */
	public function getArray() : Uint8Array {
		var ret = new Uint8Array(offset);
		ret.set(buffer.subarray(0, offset), 0);
		return ret;
	}
}