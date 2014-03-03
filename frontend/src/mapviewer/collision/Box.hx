package mapviewer.collision;

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
}