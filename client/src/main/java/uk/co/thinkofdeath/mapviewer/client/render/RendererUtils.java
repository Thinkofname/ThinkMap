/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.mapviewer.client.render;

import elemental.html.CanvasElement;
import elemental.html.WebGLRenderingContext;

public class RendererUtils {

    /**
     * Set the handler to be called when the window is resized
     *
     * @param resizeHandler
     *         The handler to be called
     */
    static native void setResizeHandler(ResizeHandler resizeHandler)/*-{
        $wnd.onresize = function () {
            resizeHandler.@uk.co.thinkofdeath.mapviewer.client.render.RendererUtils.ResizeHandler::onResize()();
        };
    }-*/;

    /**
     * Returns a WebGL context setup with attributes or returns null if WebGL isn't supported
     *
     * @param canvas
     *         The canvas to get the context of
     * @return The WebGL context
     */
    static native WebGLRenderingContext getContext(CanvasElement canvas)/*-{
        var attrs = {alpha: false, premultipliedAlpha: false, antialias: false};
        return canvas.getContext("webgl", attrs) || canvas.getContext("experimental-webgl", attrs);
    }-*/;

    /**
     * Requests the passed runnable to be run when the browser next redraws
     *
     * @param runnable
     *         The Runnable to run
     */
    static native void requestAnimationFrame(Runnable runnable)/*-{
        $wnd.requestAnimationFrame(function () {
            runnable.@java.lang.Runnable::run()();
        });
    }-*/;

    /**
     * Returns the current time in milliseconds
     *
     * @return The current time
     */
    static native double currentTime()/*-{
        return $wnd.Date.now();
    }-*/;

    static interface ResizeHandler {
        void onResize();
    }
}
