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
import elemental.html.*;
import uk.co.thinkofdeath.mapviewer.client.MapViewer;
import uk.co.thinkofdeath.mapviewer.client.render.shaders.ChunkShader;
import uk.co.thinkofdeath.mapviewer.shared.glmatrix.Mat4;

import java.util.ArrayList;
import java.util.List;

import static elemental.html.WebGLRenderingContext.*;
import static uk.co.thinkofdeath.mapviewer.client.render.RendererUtils.*;

public class Renderer implements ResizeHandler, Runnable {

    private final MapViewer mapViewer;

    private final CanvasElement canvas;
    private final WebGLRenderingContext gl;

    private final Camera camera = new Camera();

    // Matrices
    private final Mat4 perspectiveMatrix = Mat4.create();
    private final Mat4 viewMatrix = Mat4.create();
    // Used to save creating a new matrix each time
    private final Mat4 tempMatrix1 = Mat4.create();
    private final Mat4 tempMatrix2 = Mat4.create();

    // Textures
    private final WebGLTexture blockTexture;
    // Objects
    private final ChunkShader chunkShader;
    private final List<ChunkRenderObject> renderObjectList = new ArrayList<>();

    private double lastFrame;
    private double currentFrame;

    /**
     * Creates a Renderer that handles almost anything that is displayed to the user
     *
     * @param mapViewer
     *         The MapViewer that owns this renderer
     * @param canvasElement
     *         The canvas to render to
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

        gl.enable(DEPTH_TEST);
        gl.enable(CULL_FACE);
        gl.cullFace(BACK);
        gl.frontFace(CCW);
        chunkShader = new ChunkShader(false);

        chunkShader.setup(gl);

        run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        double diff = currentTime() - lastFrame;
        double delta = Math.min(diff / (1000d / 60d), 3.0);
        lastFrame = currentTime();
        mapViewer.tick(delta);

        currentFrame += (1d / 3d) * delta;
        if (currentFrame > 0xFFFFFFF) {
            currentFrame -= 0xFFFFFFF;
        }

        float timeScale = (mapViewer.getWorld().getTimeOfDay() - 6000f) / 12000f;
        if (timeScale > 1) {
            timeScale = 2 - timeScale;
        } else if (timeScale < 0) {
            timeScale = -timeScale;
        }
        timeScale = 1 - timeScale;

        gl.viewport(0, 0, canvas.getWidth(), canvas.getHeight());
        gl.clearColor((122f / 255f) * timeScale,
                (165f / 255f) * timeScale,
                (247f / 255f) * timeScale,
                1.0f);
        gl.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

        // Camera -> Matrix
        viewMatrix.identity();
        tempMatrix1.identity();
        tempMatrix2.identity();
        tempMatrix1.scale(-1, -1, 1);
        tempMatrix1.rotateX((float) (-camera.getRotationX() - Math.PI));
        tempMatrix1.rotateY((float) (camera.getRotationY() + Math.PI));
        tempMatrix2.translate(-camera.getX(), -camera.getY(), -camera.getZ());
        tempMatrix1.multiply(tempMatrix2, viewMatrix);

        chunkShader.use();

        chunkShader.setPerspectiveMatrix(perspectiveMatrix);
        chunkShader.setViewMatrix(viewMatrix);
        gl.activeTexture(TEXTURE0);
        gl.bindTexture(TEXTURE_2D, blockTexture);
        chunkShader.setBlockTexture(0);
        chunkShader.setScale(timeScale);
        chunkShader.setFrame((int) currentFrame);

        // TODO: Think about grouping objects from the same chunk to save setOffset calls
        for (ChunkRenderObject renderObject : renderObjectList) {
            if (renderObject.data != null) {
                if (renderObject.buffer == null) {
                    renderObject.buffer = gl.createBuffer();
                }
                gl.bindBuffer(ARRAY_BUFFER, renderObject.buffer);
                gl.bufferData(ARRAY_BUFFER, (ArrayBufferView) renderObject.data, STATIC_DRAW);
                renderObject.data = null;
            }
            chunkShader.setOffset(renderObject.x, renderObject.z);

            gl.bindBuffer(ARRAY_BUFFER, renderObject.buffer);
            gl.vertexAttribPointer(chunkShader.getPosition(), 3, UNSIGNED_SHORT, false, 20, 0);
            gl.vertexAttribPointer(chunkShader.getColour(), 4, UNSIGNED_BYTE, true, 20, 6);
            gl.vertexAttribPointer(chunkShader.getTexturePosition(), 2, UNSIGNED_SHORT, false, 20,
                    10);
            gl.vertexAttribPointer(chunkShader.getTextureId(), 2, UNSIGNED_SHORT, false, 20, 14);
            gl.vertexAttribPointer(chunkShader.getLighting(), 2, UNSIGNED_BYTE, false, 20, 18);
            gl.drawArrays(TRIANGLES, 0, renderObject.triangleCount);
        }
        chunkShader.disable();

        requestAnimationFrame(this);
    }

    public void postChunkObject(ChunkRenderObject renderObject) {
        renderObjectList.add(renderObject);
    }

    public void removeChunkObject(ChunkRenderObject renderObject) {
        renderObjectList.remove(renderObject);
        gl.deleteBuffer(renderObject.buffer);
        renderObject.buffer = null;
    }

    /**
     * Creates a WebGL texture from an ImageElement
     *
     * @param imageElement
     *         The image element to load
     * @return The created WebGL texture
     */
    private WebGLTexture loadTexture(ImageElement imageElement) {
        WebGLTexture texture = gl.createTexture();
        gl.bindTexture(TEXTURE_2D, texture);
        // Flip the Y to be like we used to
        gl.pixelStorei(UNPACK_FLIP_Y_WEBGL, 0);
        gl.pixelStorei(UNPACK_PREMULTIPLY_ALPHA_WEBGL, 0);
        gl.texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, imageElement);
        // Nearest filtering gives a Minecrafty look
        gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST);
        gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST);
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE);
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE);
        gl.bindTexture(TEXTURE_2D, null);
        return texture;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Returns the camera used by the renderer
     *
     * @return the camera
     */
    public Camera getCamera() {
        return camera;
    }

}
