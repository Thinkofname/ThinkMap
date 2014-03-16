package mapviewer.block;

class Face {
	
	public static var TOP    = new Face(0, "top", 0, 1, 0);
	public static var BOTTOM = new Face(1, "bottom", 0, -1, 0);
	public static var RIGHT  = new Face(2, "right", -1, 0, 0);
	public static var LEFT   = new Face(3, "left", 1, 0, 0);
	public static var BACK   = new Face(4, "back", 0, 0, -1);
	public static var FRONT  = new Face(5, "front", 0, 0, 1);
	
	public var id : Int;
	public var name : String;
	public var offsetX : Int;
	public var offsetY : Int;
	public var offsetZ : Int;
	
	private function new(id : Int, name : String, offsetX : Int, offsetY : Int, offsetZ : Int) {
		this.id = id;
		this.name = name;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		if (nameMap == null) nameMap = new Map<String, Face>();
		nameMap[name] = this;
	}
	
	private static var nameMap;
	
	public static function fromName(name : String) : Face {
		return nameMap[name];
	}
}