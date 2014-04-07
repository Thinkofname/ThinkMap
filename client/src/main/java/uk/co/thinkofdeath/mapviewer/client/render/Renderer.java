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

import elemental.client.Browser;
import elemental.html.CanvasElement;
import elemental.html.ImageElement;
import elemental.html.WebGLRenderingContext;
import elemental.html.WebGLTexture;
import uk.co.thinkofdeath.mapviewer.client.MapViewer;
import uk.co.thinkofdeath.mapviewer.client.render.glmatrix.Mat4;

import static uk.co.thinkofdeath.mapviewer.client.render.RendererUtils.*;

public class Renderer implements ResizeHandler {

    private final MapViewer mapViewer;

    private final CanvasElement canvas;
    private final WebGLRenderingContext gl;

    // Matrices
    private final Mat4 perspectiveMatrix = Mat4.create();
    private final Mat4 viewMatrix = Mat4.create();
    // Used to save creating a new matrix each time
    private final Mat4 tempMatrix1 = Mat4.create();
    private final Mat4 tempMatrix2 = Mat4.create();

    // Textures
    private final WebGLTexture blockTexture;

    /**
     * Creates a Renderer that handles almost anything that is displayed
     * to the user
     *
     * @param mapViewer     The MapViewer that owns this renderer
     * @param canvasElement The canvas to render to
     */
    public Renderer(MapViewer mapViewer, CanvasElement canvasElement) {
        this.mapViewer = mapViewer;

        canvas = canvasElement;
        setResizeHandler(this);
        viewMatrix.identity();


        gl = getContext(canvas);

        // TODO: Give a nicer error
        if (gl == null) throw new UnsupportedOperationException("WebGL not supported");

        onResize(); // Setup canvas

        blockTexture = loadTexture(mapViewer.getBlockTexture());

        gl.enable(WebGLRenderingContext.DEPTH_TEST);
        gl.enable(WebGLRenderingContext.CULL_FACE);
        gl.cullFace(WebGLRenderingContext.BACK);
        gl.frontFace(WebGLRenderingContext.CCW);

        // TODO: Controls
    }

    /**
     * Creates a WebGL texture from an ImageElement
     * @param imageElement The image element to load
     * @return The created WebGL texture
     */
    private WebGLTexture loadTexture(ImageElement imageElement) {
        WebGLTexture texture = gl.createTexture();
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
        // Flip the Y to be like we used to
        gl.pixelStorei(WebGLRenderingContext.UNPACK_FLIP_Y_WEBGL, 0);
        gl.pixelStorei(WebGLRenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, 0);
        gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.RGBA,
                WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, imageElement);
        // Nearest filtering gives a Minecrafty look
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER,
                WebGLRenderingContext.NEAREST);
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER,
                WebGLRenderingContext.NEAREST);
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_S,
                WebGLRenderingContext.CLAMP_TO_EDGE);
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_T,
                WebGLRenderingContext.CLAMP_TO_EDGE);
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, null);
        return texture;
    }

    @Override
    public void onResize() {
        // Fill the window
        canvas.setWidth(Browser.getWindow().getInnerWidth());
        canvas.setHeight(Browser.getWindow().getInnerHeight());

        // Reset the perspective matrix
        perspectiveMatrix.identity();
        perspectiveMatrix.perspective((float) Math.toRadians(80), (float) canvas.getWidth() / canvas.getHeight(), 0.1f, 500f);
        // TODO: toggle update
    }
}
