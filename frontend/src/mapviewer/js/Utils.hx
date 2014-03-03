package mapviewer.js;
import js.Browser;
import js.html.Element;
import js.html.MouseEvent;

class Utils {

    inline public static function now() : Int {
        return untyped __js__('Date.now()');
    }
	
	private static var pointerPrefix = null;
	
	private static function getPointerPrefix() untyped {
		if (pointerPrefix != null) return;
		if (__js__("'pointerLockElement' in document")) {
			pointerPrefix = "";
		} else if (__js__("'webkitPointerLockElement' in document")) {
			pointerPrefix = "webkit";
		} else if (__js__("'mozPointerLockElement' in document")) {
			pointerPrefix = "moz";
		}
	}
	
	private static function setPointerPrefix(prop : String) untyped {
		getPointerPrefix();
		if (pointerPrefix == "") return prop;
		return pointerPrefix + prop.charAt(0).toUpperCase() + prop.substr(1);
	}
	
	public static function requestPointerLock(target : Element) untyped {
		target[setPointerPrefix("requestPointerLock")]();
	}
	
	public static function movementX(target : MouseEvent) : Int untyped {
		return target[setPointerPrefix("movementX")];
	}
	
	public static function movementY(target : MouseEvent) : Int untyped {
		return target[setPointerPrefix("movementY")];
	}
	
	public static function pointerLockElement() : Element untyped {
		return Browser.document[setPointerPrefix("pointerLockElement")];
	}
}