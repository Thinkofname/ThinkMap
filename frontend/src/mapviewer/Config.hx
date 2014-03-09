package mapviewer;

@:native("MapViewerConfig")
extern class Config {
	
	public static var hostname : String;
	public static var port(get, never) : Int;
	
	@:extern inline static function get_port() : Int untyped {
		if (MapViewerConfig.port == "%SERVERPORT%") {
			// Running on a different server and hasn't changed the 
			// port number
			return 23333;
		}
		return Std.parseInt(MapViewerConfig.port);
	}
}