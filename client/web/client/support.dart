part of mapViewer;

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
String _fullscreenPrefix = null;

@Deprecated("Remove once this is fixed in dart")
_getFullscreenPrefix() {
    if (_fullscreenPrefix != null) return;
    var js = new JsObject.fromBrowserObject(document.body);
    if (js.hasProperty("requestFullScreen")) {
        _fullscreenPrefix = "";
    } else if (js.hasProperty("webkitRequestFullScreen")) {
        _fullscreenPrefix = "webkit";
    } else if (js.hasProperty("mozRequestFullScreen")) {
        _fullscreenPrefix = "moz";
    }
}

@Deprecated("Remove once this is fixed in dart")
String _setFullscreenPrefix(String prop) {
    _getFullscreenPrefix();
    if (_pointerPrefix == "") {
        return prop;
    }
    return _pointerPrefix + prop[0].toUpperCase() + prop.substring(1);
}

@Deprecated("Remove once this is fixed in dart")
requestFullScreen(Element target) {
    var js = new JsObject.fromBrowserObject(target);
    js.callMethod(_setPointerPrefix("requestFullScreen"));
}