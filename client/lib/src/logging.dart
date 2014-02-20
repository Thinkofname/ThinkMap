part of map_viewer;

JsObject jsConsole = new JsObject.fromBrowserObject(window)["console"];

const bool DEBUG_MODE = true;

class Logger {
  final String name;

  Logger(this.name);

  info(String txt) {
    if (DEBUG_MODE) {
      jsConsole.callMethod("log", ["%c[$name]: $txt", "color:#00FFFF; background: black;"]);
    }
  }

  debug(String txt) {
    if (DEBUG_MODE) {
      jsConsole.callMethod("log", ["%c[$name]: $txt", "color:yellow; background: black;"]);
    }
  }

  warn(String txt) {
    jsConsole.callMethod("log", ["%c[$name]: $txt", "color:#F27900; background: black; font-weight: bold;"]);
  }

  error(String txt) {
    jsConsole.callMethod("log", ["%c[$name]: $txt", "color:black; background: red; font-weight: bold;"]);
  }
}