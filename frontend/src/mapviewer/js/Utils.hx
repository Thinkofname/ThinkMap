package mapviewer.js;

class Utils {

    inline public static function now() : Int {
        return untyped __js__('Date.now()');
    }
}