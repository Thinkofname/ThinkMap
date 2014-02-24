part of map_viewer;

JsObject jsConsole = new JsObject.fromBrowserObject(window)["console"];

const bool DEBUG_MODE = true;

class Logger {

  static bool canLog = true;

  final String name;

  Logger._(this.name);

  factory Logger(String name) {
    if (canLog) {
      return new Logger._(name);
    }
    return new NullLogger();
  }

  void info(String txt) {
    if (DEBUG_MODE) {
      jsConsole.callMethod("log", ["%c[$name]: $txt", "color:#00FFFF; background: black;"]);
    }
  }

  void debug(String txt) {
    if (DEBUG_MODE) {
      jsConsole.callMethod("log", ["%c[$name]: $txt", "color:yellow; background: black;"]);
    }
  }

  void warn(String txt) {
    jsConsole.callMethod("log", ["%c[$name]: $txt", "color:#F27900; background: black; font-weight: bold;"]);
  }

  void error(String txt) {
    jsConsole.callMethod("log", ["%c[$name]: $txt", "color:black; background: red; font-weight: bold;"]);
  }
}

class NullLogger implements Logger {

  final String name = null;

  void info(String txt) {
  }

  void debug(String txt) {
  }

  void warn(String txt) {
  }

  void error(String txt) {
  }
}
