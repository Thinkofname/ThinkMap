package uk.co.thinkofdeath.mapviewer.client.input;

import uk.co.thinkofdeath.mapviewer.client.MapViewer;
import uk.co.thinkofdeath.mapviewer.client.render.Camera;

public class InputManager {

    private final MapViewer mapViewer;

    private int movingDirection = 0;

    /**
     * Creates a new input manager
     *
     * @param mapViewer
     *         The map viewer that owns it
     */
    public InputManager(MapViewer mapViewer) {
        this.mapViewer = mapViewer;
    }

    /**
     * Updates the input manager
     *
     * @param delta
     */
    public void update(double delta) {
        Camera camera = mapViewer.getCamera();
        if (movingDirection != 0) {
            camera.setX((float) (camera.getX() + 0.3 * Math.sin(camera.getRotationY()) *
                    Math.cos(camera.getRotationX()) * delta * movingDirection));
            camera.setZ((float) (camera.getZ() - 0.3 * Math.cos(camera.getRotationY()) *
                    Math.cos(camera.getRotationX()) * delta * movingDirection));
            camera.setY((float) (camera.getY() - 0.3 * Math.sin(camera.getRotationX()) * delta *
                    movingDirection));
        }
    }

    // This is where things get nasty.
    // Supporting multiple browsers and devices can be a pain since some browsers may use
    // prefixed events, some may use different events and others will support the event but
    // not the same variables as other browsers.
    // TODO: Touch events
    // TODO: Drag controls

    /**
     * Causes the input manager to create its hooks into the browsers events
     */
    public void hook() {
        registerNativeEvents();
    }

    private void onCanvasClick(int x, int y) {
        if (!isPointerLocked()) {
            requestPointerLock();
        }
    }

    private void onMouseLockMove(int movementX, int movementY) {
        if (!isPointerLocked()) {
            return;
        }

        Camera camera = mapViewer.getCamera();
        camera.setRotationY(camera.getRotationY() + movementX / 300f);
        camera.setRotationX(camera.getRotationX() + movementY / 300f);
        if (camera.getRotationX() > Math.PI / 2) camera.setRotationX((float) (Math.PI / 2));
        if (camera.getRotationX() < -Math.PI / 2) camera.setRotationX((float) (-Math.PI / 2));
    }

    private void onMouseMove(int x, int y) {

    }

    private boolean onKeyDown(char keyCode) {
        switch (keyCode) {
            case 'W':
                movingDirection = 1;
                return true;
            case 'S':
                movingDirection = -1;
                return true;
        }
        return false;
    }

    private boolean onKeyUp(char keyCode) {
        switch (keyCode) {
            case 'W':
                if (movingDirection == 1) {
                    movingDirection = 0;
                }
                return true;
            case 'S':
                if (movingDirection == -1) {
                    movingDirection = 0;
                }
                return true;
        }
        return false;
    }

    private native boolean isPointerLocked()/*-{
        return ($doc.pointerLockElement
            || $doc.webkitPointerLockElement
            || $doc.mozPointerLockElement) != null;
    }-*/;

    private native boolean requestPointerLock()/*-{
        var canvas = $doc.getElementById("main");
        var f = (canvas.requestPointerLock
            || canvas.webkitRequestPointerLock
            || canvas.mozRequestPointerLock);
        if (f == null) {
            return false;
        }
        f.bind(canvas)();
        return true;
    }-*/;

    private native void registerNativeEvents()/*-{
        var that = this;
        $doc.getElementById("main").onclick = function (e) {
            e.preventDefault();
            that.@uk.co.thinkofdeath.mapviewer.client.input.InputManager::onCanvasClick(II)(e.clientX, e.clientY);
        };
        $doc.onmousemove = function (e) {
            var mx = (e.movementX || e.webkitMovementX || e.mozMovementX);
            var my = (e.movementY || e.webkitMovementY || e.mozMovementY);
            if (mx == undefined || my == undefined) {
                that.@uk.co.thinkofdeath.mapviewer.client.input.InputManager::onMouseMove(II)(e.clientX, e.clientY);
            } else {
                that.@uk.co.thinkofdeath.mapviewer.client.input.InputManager::onMouseLockMove(II)(mx, my);
            }
        };
        $doc.onkeydown = function (e) {
            var key = e.keyCode || e.which;
            if (that.@uk.co.thinkofdeath.mapviewer.client.input.InputManager::onKeyDown(C)(key)) {
                e.preventDefault();
            }
        };
        $doc.onkeyup = function (e) {
            var key = e.keyCode || e.which;
            if (that.@uk.co.thinkofdeath.mapviewer.client.input.InputManager::onKeyUp(C)(key)) {
                e.preventDefault();
            }
        };
    }-*/;
}
