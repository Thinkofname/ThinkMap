package mapviewer.ui;

/**
 * ...
 * @author Thinkofdeath
 */
class Colour {

	public static var BLACK : Colour = new Colour("black", 0x000000);
	public static var DARK_BLUE : Colour = new Colour("dark_blue", 0x0000AA);
	public static var DARK_GREEN : Colour = new Colour("dark_green", 0x00AA00);
	public static var DARK_AQUA : Colour = new Colour("dark_aqua", 0x00AAAA);
	public static var DARK_RED : Colour = new Colour("dark_red", 0xAA0000);
	public static var DARK_PURPLE : Colour = new Colour("dark_purple", 0xAA00AA);
	public static var GOLD : Colour = new Colour("gold", 0xFFAA00);
	public static var GRAY : Colour = new Colour("gray", 0xAAAAAA);
	public static var DARK_GRAY : Colour = new Colour("dark_gray", 0x555555);
	public static var BLUE : Colour = new Colour("blue", 0x5555FF);
	public static var GREEN : Colour = new Colour("green", 0x55FF55);
	public static var AQUA : Colour = new Colour("aqua", 0x55FFFF);
	public static var RED : Colour = new Colour("red", 0xFF5555);
	public static var LIGHT_PURPLE : Colour = new Colour("light_purple", 0xFF55FF);
	public static var YELLOW : Colour = new Colour("yellow", 0xFFFF55);
	public static var WHITE : Colour = new Colour("white", 0xFFFFFF);
	
	public var name : String;
	public var hex : Int;
	public var hexString : String;
	
	private function new(name : String, hex : Int) {
		this.name = name;
		this.hex = hex;
		hexString = "#" + StringTools.hex(hex, 6);
	}
	
}