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

package uk.co.thinkofdeath.thinkcraft.html.client.render;

import elemental.client.Browser;
import elemental.html.*;
import uk.co.thinkofdeath.thinkcraft.html.client.MapViewer;
import uk.co.thinkofdeath.thinkcraft.html.client.render.shaders.ChunkShader;
import uk.co.thinkofdeath.thinkcraft.html.client.texture.VirtualTexture;
import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.support.JsUtils;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;
import uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4;

import java.util.ArrayList;

import static elemental.html.WebGLRenderingContext.*;

public class Renderer implements RendererUtils.ResizeHandler, Runnable {

    private static final int TRANSPARENT_UPDATES_LIMIT = 5;

    private final MapViewer mapViewer;

    private final CanvasElement canvas;
    private final WebGLRenderingContext gl;

    private final Camera camera = new Camera();
    private int cx = Integer.MAX_VALUE;
    private int cy = Integer.MAX_VALUE;
    private int cz = Integer.MAX_VALUE;

    // Matrices
    private final Matrix4 perspectiveMatrix = new Matrix4();
    private final Matrix4 viewMatrix = new Matrix4();

    // Textures
    private final WebGLTexture[] blockTextures;
    private final Int32Array textureLocations = RendererUtils.createInt32(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
    // Objects
    private final ChunkShader chunkShader;
    private final ChunkShader chunkShaderAlpha;
    private final ArrayList<ChunkRenderObject> renderObjectList = new ArrayList<>();

    private final ArrayList<SortableRenderObject> sortableRenderObjects = new ArrayList<>();

    private double lastFrame;
    private double currentFrame;

    // Sorters
    private final ChunkSorter chunkSorter = new ChunkSorter(camera);
    private final SortableSorter sortableSorter = new SortableSorter(camera);

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
        RendererUtils.setResizeHandler(this);
        viewMatrix.identity();


        gl = RendererUtils.getContext(canvas);

        // TODO: Give a nicer error
        if (gl == null) throw new UnsupportedOperationException("WebGL not supported");

        onResize(); // Setup canvas

        blockTextures = new WebGLTexture[mapViewer.getNumberOfTextures()];

        gl.enable(DEPTH_TEST);
        gl.enable(CULL_FACE);
        gl.cullFace(BACK);
        gl.frontFace(CCW);
        chunkShader = new ChunkShader(false);
        chunkShaderAlpha = new ChunkShader(true);

        chunkShader.setup(gl);
        chunkShaderAlpha.setup(gl);

        for (int i = 0; i < blockTextures.length; i++) {
            gl.activeTexture(TEXTURE0 + i);
            blockTextures[i] = loadTexture(mapViewer.getVirtualTextures()[i]);
            gl.bindTexture(TEXTURE_2D, blockTextures[i]);
        }

        RendererUtils.requestAnimationFrame(this);
    }

    @Override
    public void run() {
        double diff = RendererUtils.currentTime() - lastFrame;
        double delta = Math.min(diff / (1000d / 60d), 3.0);
        lastFrame = RendererUtils.currentTime();
        mapViewer.tick(delta);

        currentFrame += (1d / 3d) * delta;
        if (currentFrame > 0xFFFFFFF) {
            currentFrame -= 0xFFFFFFF;
        }
        // Update textures
        for (VirtualTexture texture : mapViewer.getVirtualTextures()) {
            texture.update((int) currentFrame);
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

        viewMatrix.identity();
        viewMatrix.translate(-camera.getX(), -camera.getY(), -camera.getZ());
        viewMatrix.rotateY(-camera.getRotationY());
        viewMatrix.rotateX(-camera.getRotationX());


        chunkShader.use();

        chunkShader.setPerspectiveMatrix(perspectiveMatrix);
        chunkShader.setViewMatrix(viewMatrix);
        chunkShader.setBlockTexture(textureLocations);
        chunkShader.setScale(timeScale);
        chunkShader.setFrame((int) currentFrame);

        // TODO: Think about grouping objects from the same chunk to save setOffset calls
        JsUtils.sort(renderObjectList, chunkSorter);
        for (int i = 0, renderObjectListSize = renderObjectList.size(); i < renderObjectListSize; i++) {
            ChunkRenderObject renderObject = renderObjectList.get(i);
            if (renderObject.data != null) {
                if (renderObject.buffer == null) {
                    renderObject.buffer = gl.createBuffer();
                }
                gl.bindBuffer(ARRAY_BUFFER, renderObject.buffer);
                gl.bufferData(ARRAY_BUFFER, (ArrayBufferView) renderObject.data, STATIC_DRAW);

                mapViewer.getWorkerPool().sendMessage(renderObject.sender, "pool:free", renderObject.data,
                        new Object[]{renderObject.data.getBuffer()}, false);
                renderObject.data = null;
            }
            chunkShader.setOffset(renderObject.x, renderObject.z);

            gl.bindBuffer(ARRAY_BUFFER, renderObject.buffer);
            gl.vertexAttribPointer(chunkShader.getPosition(), 3, UNSIGNED_SHORT, false, 22, 0);
            gl.vertexAttribPointer(chunkShader.getColour(), 4, UNSIGNED_BYTE, true, 22, 6);
            gl.vertexAttribPointer(chunkShader.getTexturePosition(), 2, UNSIGNED_SHORT, false, 22, 10);
            gl.vertexAttribPointer(chunkShader.getTextureDetails(), 3, UNSIGNED_SHORT, false, 22, 14);
            gl.vertexAttribPointer(chunkShader.getLighting(), 2, UNSIGNED_BYTE, false, 22, 20);
            gl.drawArrays(TRIANGLES, 0, renderObject.triangleCount);
        }
        chunkShader.disable();

        gl.enable(BLEND);
        gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);
        chunkShaderAlpha.use();
        chunkShaderAlpha.setPerspectiveMatrix(perspectiveMatrix);
        chunkShaderAlpha.setViewMatrix(viewMatrix);
        chunkShaderAlpha.setBlockTexture(textureLocations);
        chunkShaderAlpha.setScale(timeScale);
        chunkShaderAlpha.setFrame((int) currentFrame);

        int nx = (int) camera.getX();
        int ny = (int) camera.getY();
        int nz = (int) camera.getZ();

        boolean moved = false;
        if (cx != nx || cy != ny || cz != nz) {
            cx = nx;
            cy = ny;
            cz = nz;
            moved = true;
        }
        int updates = 0;

        JsUtils.sort(sortableRenderObjects, sortableSorter);
        for (int i = 0, sortableRenderObjectsSize = sortableRenderObjects.size(); i < sortableRenderObjectsSize; i++) {
            SortableRenderObject sortableRenderObject = sortableRenderObjects.get(i);

            if (moved) {
                sortableRenderObject.needResort = true;
            }

            boolean forceUpdate = false;

            if (sortableRenderObject.buffer == null) {
                sortableRenderObject.buffer = gl.createBuffer();
                sortableRenderObject.needResort = true;
                forceUpdate = true;
            }

            boolean update = sortableRenderObject.needResort && updates < TRANSPARENT_UPDATES_LIMIT;

            if (update || forceUpdate) {
                updates++;
                sortableRenderObject.needResort = false;

                ArrayList<PositionedModel> models = sortableRenderObject.getModels();
                JsUtils.sort(models, new ModelSorter(
                        sortableRenderObject.getX(),
                        sortableRenderObject.getZ(),
                        camera));

                int offset = 0;
                TUint8Array temp = sortableRenderObject.tempArray;
                TUint8Array data = sortableRenderObject.getData();
                for (PositionedModel model : models) {
                    temp.set(offset, data.subarray(model.getStart(),
                            model.getStart() + model.getLength()));
                    offset += model.getLength();
                }

                gl.bindBuffer(ARRAY_BUFFER, sortableRenderObject.buffer);
                gl.bufferData(ARRAY_BUFFER, (ArrayBufferView) temp,
                        DYNAMIC_DRAW);
                sortableRenderObject.count = temp.length() / 22;
            }

            if (updates >= TRANSPARENT_UPDATES_LIMIT && !moved) {
                break;
            }
        }

        for (int i = sortableRenderObjects.size() - 1; i >= 0; i--) {
            SortableRenderObject sortableRenderObject = sortableRenderObjects.get(i);
            if (sortableRenderObject.count == 0 || sortableRenderObject.buffer == null) continue;
            gl.bindBuffer(ARRAY_BUFFER, sortableRenderObject.buffer);
            chunkShaderAlpha.setOffset(sortableRenderObject.getX(), sortableRenderObject.getZ());
            gl.vertexAttribPointer(chunkShaderAlpha.getPosition(), 3, UNSIGNED_SHORT, false, 22, 0);
            gl.vertexAttribPointer(chunkShaderAlpha.getColour(), 4, UNSIGNED_BYTE, true, 22, 6);
            gl.vertexAttribPointer(chunkShaderAlpha.getTexturePosition(), 2, UNSIGNED_SHORT, false, 22, 10);
            gl.vertexAttribPointer(chunkShaderAlpha.getTextureDetails(), 4, UNSIGNED_SHORT, false, 22, 14);
            gl.vertexAttribPointer(chunkShaderAlpha.getLighting(), 2, UNSIGNED_BYTE, false, 22, 20);
            gl.drawArrays(TRIANGLES, 0, sortableRenderObject.count);
        }

        chunkShaderAlpha.disable();
        gl.disable(BLEND);

        RendererUtils.requestAnimationFrame(this);
    }

    /**
     * Adds a sortable object to the renderer
     *
     * @param sortableRenderObject
     *         The object to render
     */
    public void postSortable(SortableRenderObject sortableRenderObject) {
        sortableRenderObjects.add(sortableRenderObject);
    }

    /**
     * Removes a sortable object from the renderer
     *
     * @param sortableRenderObject
     *         The object to remove
     */
    public void removeSortable(SortableRenderObject sortableRenderObject) {
        sortableRenderObjects.remove(sortableRenderObject);
        gl.deleteBuffer(sortableRenderObject.buffer);
    }

    /**
     * Adds a chunk object to the renderer
     *
     * @param renderObject
     *         The object to render
     */
    public void postChunkObject(ChunkRenderObject renderObject) {
        renderObjectList.add(renderObject);
    }

    /**
     * Removes a chunk object from the renderer
     *
     * @param renderObject
     *         The object to remove
     */
    public void removeChunkObject(ChunkRenderObject renderObject) {
        renderObjectList.remove(renderObject);
        gl.deleteBuffer(renderObject.buffer);
        renderObject.buffer = null;
    }

    /**
     * Creates a WebGL texture from an ImageElement
     *
     * @param vt
     *         The texture to load
     * @return The created WebGL texture
     */
    private WebGLTexture loadTexture(VirtualTexture vt) {
        WebGLTexture texture = gl.createTexture();
        gl.bindTexture(TEXTURE_2D, texture);
        gl.pixelStorei(UNPACK_FLIP_Y_WEBGL, 0);
        gl.pixelStorei(UNPACK_PREMULTIPLY_ALPHA_WEBGL, 0);
        gl.texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, vt.getTexture());
        // Nearest filtering gives a Minecrafty look
        gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST);
        gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST);
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE);
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE);
        gl.bindTexture(TEXTURE_2D, null);
        return texture;
    }

    public void updateTexture(int id) {
        gl.bindTexture(TEXTURE_2D, blockTextures[id]);
        gl.pixelStorei(UNPACK_FLIP_Y_WEBGL, 0);
        gl.texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, mapViewer.getVirtualTextures()[id].getTexture());
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

    /**
     * Returns the camera used by the renderer
     *
     * @return the camera
     */
    public Camera getCamera() {
        return camera;
    }
}
