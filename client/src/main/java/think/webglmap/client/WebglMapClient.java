package think.webglmap.client;

import com.google.gwt.core.client.EntryPoint;
import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.CanvasElement;
import elemental.html.WebGLRenderingContext;
import elemental.html.Window;

import static elemental.html.WebGLRenderingContext.*;

public class WebglMapClient implements EntryPoint, EventListener {

    public static WebGLRenderingContext gl;

    private CanvasElement canvas;

    public void onModuleLoad() {
        canvas = Browser.getDocument().createCanvasElement();
        Window window = Browser.getWindow();
        canvas.setWidth(window.getInnerWidth());
        canvas.setHeight(window.getInnerHeight());
        canvas.getStyle().setPosition("absolute");
        canvas.getStyle().setTop("0");
        canvas.getStyle().setLeft("0");

        Browser.getDocument().getBody().appendChild(canvas);

        gl = (WebGLRenderingContext) canvas.getContext("webgl");
        if (gl==null) {
            gl = (WebGLRenderingContext) canvas.getContext("experimental");
            if (gl == null) {
                throw new UnsupportedOperationException("Couldn't create gl context");
            }
        }

        window.setOnresize(this);

        requestAnimationFrame();
    }

    //Resize event
    public void handleEvent(Event evt) {
        Window window = Browser.getWindow();
        canvas.setWidth(window.getInnerWidth());
        canvas.setHeight(window.getInnerHeight());
    }

    private void redraw() {
        gl.viewport(0, 0, canvas.getWidth(), canvas.getHeight());
        gl.clearColor(0, 1f, 1f, 1f);
        gl.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

        requestAnimationFrame();
    }

    private native void requestAnimationFrame()/*-{
        ($wnd.requestAnimationFrame || $wnd.mozRequestAnimationFrame ||
            $wnd.webkitRequestAnimationFrame || $wnd.msRequestAnimationFrame)($entry(
            this.@think.webglmap.client.WebglMapClient::redraw()
        ));
    }-*/;
}
