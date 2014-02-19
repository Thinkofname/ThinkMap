part of map_viewer;

// This contains functions that give access to prefixed
// javascript which dart fails to prefix itself

/*
    Pointer Lock
 */

@Deprecated("Remove once this is fixed in dart")
String _pointerPrefix = null;

@Deprecated("Remove once this is fixed in dart")
_getPointerPrefix() {
    if (_pointerPrefix != null) return;
    var js = new JsObject.fromBrowserObject(document);
    if (js.hasProperty("pointerLockElement")) {
        _pointerPrefix = "";
    } else if (js.hasProperty("webkitPointerLockElement")) {
        _pointerPrefix = "webkit";
    } else if (js.hasProperty("mozPointerLockElement")) {
        _pointerPrefix = "moz";
    }
}

@Deprecated("Remove once this is fixed in dart")
String _setPointerPrefix(String prop) {
    _getPointerPrefix();
    if (_pointerPrefix == "") {
        return prop;
    }
    return _pointerPrefix + prop[0].toUpperCase() + prop.substring(1);
}

@Deprecated("Remove once this is fixed in dart")
requestPointerLock(Element target) {
    var js = new JsObject.fromBrowserObject(target);
    js.callMethod(_setPointerPrefix("requestPointerLock"));
}

@Deprecated("Remove once this is fixed in dart")
int movementX(MouseEvent event) {
    var js = new JsObject.fromBrowserObject(event);
    return js[_setPointerPrefix("movementX")];
}

@Deprecated("Remove once this is fixed in dart")
int movementY(MouseEvent event) {
    var js = new JsObject.fromBrowserObject(event);
    return js[_setPointerPrefix("movementY")];
}

Element pointerLockElement() {
    return new JsObject.fromBrowserObject(document)[_setPointerPrefix("pointerLockElement")];
}

/*
    Full screen
 */

@Deprecated("Remove once this is fixed in dart")
String _fullscreenFunction = null;

@Deprecated("Remove once this is fixed in dart")
_getFullscreenPrefix() {
    if (_fullscreenFunction != null) return;
    var js = new JsObject.fromBrowserObject(document.body);
    if (js.hasProperty("requestFullscreen")) {
        _fullscreenFunction = "";
    } else if (js.hasProperty("webkitRequestFullscreen")) {
        _fullscreenFunction = "webkitRequestFullscreen";
    } else if (js.hasProperty("mozRequestFullScreen")) {
        _fullscreenFunction = "mozRequestFullScreen";
    }
}

@Deprecated("Remove once this is fixed in dart")
String _setFullscreenPrefix(String prop) {
    _getFullscreenPrefix();
    if (_fullscreenFunction == "") {
        return prop;
    }
    return _fullscreenFunction;
}

@Deprecated("Remove once this is fixed in dart")
requestFullScreen(Element target) {
    print("Requesting fullscreen");
    var js = new JsObject.fromBrowserObject(target);
    print("for $js");
    js.callMethod(_setFullscreenPrefix("requestFullscreen"));
}