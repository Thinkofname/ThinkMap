package mapviewer.renderer.webgl.glmatrix;

extern class Mat4 {
	
	@:extern inline public static function create() : Mat4 {
		return untyped mat4.create();
	}
	
	@:extern inline public function identity() : Void {
		untyped mat4.identity(this);
	}
}