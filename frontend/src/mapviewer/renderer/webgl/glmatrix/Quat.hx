package mapviewer.renderer.webgl.glmatrix;
import js.html.Float32Array;

extern class Quat extends Float32Array {
	
	@:extern inline public static function create() : Quat untyped {
		return quat.create();
	}
	
	@:extern inline public function setAxisAngle(axis : Array<Float>, rad : Float) : Void untyped {
		quat.setAxisAngle(this, axis, rad);
	}
}