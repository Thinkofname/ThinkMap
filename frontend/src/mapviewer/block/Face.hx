package mapviewer.block;

class Face {
	
	public static var TOP    = new Face(0, "top");
	public static var BOTTOM = new Face(1, "bottom");
	public static var RIGHT  = new Face(2, "right");
	public static var LEFT   = new Face(3, "left");
	public static var BACK   = new Face(4, "back");
	public static var FRONT  = new Face(5, "front");
	
	public var id : Int;
	public var name : String;
	
	private function new(id : Int, name : String) {
		this.id = id;
		this.name = name;
		if (nameMap == null) nameMap = new Map<String, Face>();
		nameMap[name] = this;
	}
	
	private static var nameMap;
	
	public static function fromName(name : String) : Face {
		return nameMap[name];
	}
}