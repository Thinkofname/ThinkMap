package mapviewer.logging;

import js.Browser;
class Logger {

    inline public static var DEBUG_MODE : Bool = true;
    public static var CAN_LOG : Bool;

    private var name : String;

    public function new(name : String) {
        this.name = name;
    }


    @:extern
    inline public function info(txt : String) {
        if (DEBUG_MODE && CAN_LOG)
            log(txt, "color:#00FFFF; background: black;");
    }

    @:extern
    inline public function debug(txt : String) {
        if (DEBUG_MODE && CAN_LOG)
            log(txt, "color:yellow; background: black;");
    }

    @:extern
    inline public function warn(txt : String) {
        if (CAN_LOG)
            log(txt, "color:#F27900; background: black; font-weight: bold;");
    }

    @:extern
    inline public function error(txt : String) {
        if (CAN_LOG)
            log(txt, "color:black; background: red; font-weight: bold;");
    }

    @:extern
    inline private function log(msg : String, format : String) {
        untyped console.log('%c[$name]: $msg', format);
    }
}