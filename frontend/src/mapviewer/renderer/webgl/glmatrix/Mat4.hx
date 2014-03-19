package mapviewer.renderer.webgl.glmatrix;
import js.html.Float32Array;

extern class Mat4 extends Float32Array {
	
	@:extern inline public static function create() : Mat4 {
		return untyped mat4.create();
	}
	
	@:extern inline public function identity() : Void {
		untyped mat4.identity(this);
	}
	
	@:extern inline public function perspective(fovy : Float, aspect : Float, near : Float, far : Float) : Void {
		untyped mat4.perspective(this, fovy, aspect, near, far);
	}
	
	@:extern inline public function scale(x : Float, y : Float, z : Float) : Void {
		untyped mat4.scale(this, this, [x, y, z]);
	}
	
	@:extern inline public function rotateX(rad : Float) : Void {
		untyped mat4.rotateX(this, this, rad);
	}
	
	@:extern inline public function rotateY(rad : Float) : Void {
		untyped mat4.rotateY(this, this, rad);
	}
	
	@:extern inline public function rotateZ(rad : Float) : Void {
		untyped mat4.rotateZ(this, this, rad);
	}
	
	@:extern inline public function rotate(rad : Float, axis : Array<Float>) : Void {
		untyped mat4.rotate(this, this, rad, axis);
	}
	
	@:extern inline public function translate(t : Array<Float>) : Void {
		untyped mat4.translate(this, this, t);
	}
	
	@:extern inline public function multiply(other : Mat4, ?o : Mat4) : Mat4 untyped {
		if (o == null) o = mat4.create();
		mat4.multiply(o, this, other);
		return o;
	}
}