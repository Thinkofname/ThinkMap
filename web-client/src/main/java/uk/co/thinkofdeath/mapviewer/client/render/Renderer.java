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
import uk.co.thinkofdeath.mapviewer.shared.LightInfo;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.building.ModelBuilder;
import uk.co.thinkofdeath.mapviewer.shared.glmatrix.Mat4;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;
import uk.co.thinkofdeath.mapviewer.shared.model.SendableModel;
import uk.co.thinkofdeath.mapviewer.shared.support.JsUtils;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

import java.util.ArrayList;
import java.util.Collections;

import static elemental.html.WebGLRenderingContext.*;
import static uk.co.thinkofdeath.mapviewer.client.render.RendererUtils.*;

public class Renderer implements ResizeHandler, Runnable {

    private static final int TRANSPARENT_UPDATES_LIMIT = 1;

    private final MapViewer mapViewer;

    private final CanvasElement canvas;
    private final WebGLRenderingContext gl;

    private final Camera camera = new Camera();
    private int cx = Integer.MAX_VALUE;
    private int cy = Integer.MAX_VALUE;
    private int cz = Integer.MAX_VALUE;

    // Matrices
    private final Mat4 perspectiveMatrix = Mat4.create();
    private final Mat4 viewMatrix = Mat4.create();
    // Used to save creating a new matrix each time
    private final Mat4 tempMatrix1 = Mat4.create();
    private final Mat4 tempMatrix2 = Mat4.create();

    // Textures
    private final WebGLTexture[] blockTextures;
    private final Int32Array textureLocations = createInt32(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
    // Objects
    private final ChunkShader chunkShader;
    private final ChunkShader chunkShaderAlpha;
    private final ArrayList<ChunkRenderObject> renderObjectList = new ArrayList<>();

    private final ArrayList<SortableRenderObject> sortableRenderObjects = new ArrayList<>();
    private final ModelBuilder transparentBuilder = new ModelBuilder();

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

        ImageElement[] imageElements = mapViewer.getTextureImages();
        blockTextures = new WebGLTexture[imageElements.length];
        for (int i = 0; i < blockTextures.length; i++) {
            blockTextures[i] = loadTexture(imageElements[i]);
        }

        gl.enable(DEPTH_TEST);
        gl.enable(CULL_FACE);
        gl.cullFace(BACK);
        gl.frontFace(CCW);
        chunkShader = new ChunkShader(false);
        chunkShaderAlpha = new ChunkShader(true);

        chunkShader.setup(gl);
        chunkShaderAlpha.setup(gl);

        run();
    }

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
        for (int i = 0; i < blockTextures.length; i++) {
            gl.activeTexture(TEXTURE0 + i);
            gl.bindTexture(TEXTURE_2D, blockTextures[i]);
        }
        chunkShader.setBlockTexture(textureLocations);
        chunkShader.setScale(timeScale);
        chunkShader.setFrame((int) currentFrame);

        // TODO: Think about grouping objects from the same chunk to save setOffset calls
        JsUtils.sort(renderObjectList, new ChunkSorter(camera));
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
            gl.vertexAttribPointer(chunkShader.getPosition(), 3, UNSIGNED_SHORT, false, 26, 0);
            gl.vertexAttribPointer(chunkShader.getColour(), 4, UNSIGNED_BYTE, true, 26, 6);
            gl.vertexAttribPointer(chunkShader.getTexturePosition(), 2, UNSIGNED_SHORT, false, 26, 10);
            gl.vertexAttribPointer(chunkShader.getTextureDetails(), 4, UNSIGNED_SHORT, false, 26, 14);
            gl.vertexAttribPointer(chunkShader.getTextureFrames(), 1, UNSIGNED_SHORT, false, 26, 22);
            gl.vertexAttribPointer(chunkShader.getLighting(), 2, UNSIGNED_BYTE, false, 26, 24);
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

        JsUtils.sort(sortableRenderObjects, new SortableSorter(camera));
        for (SortableRenderObject sortableRenderObject : sortableRenderObjects) {

            if (moved) {
                sortableRenderObject.needResort = true;
            }

            if (sortableRenderObject.buffer == null) {
                sortableRenderObject.buffer = gl.createBuffer();
                sortableRenderObject.needResort = true;
            }

            boolean update = sortableRenderObject.needResort && updates < TRANSPARENT_UPDATES_LIMIT;

            if (update) {
                updates++;
                sortableRenderObject.needResort = false;
                transparentBuilder.reset();

                ArrayList<SendableModel> models = sortableRenderObject.getModels();
                JsUtils.sort(models, new ModelSorter(
                        sortableRenderObject.getX(),
                        sortableRenderObject.getZ(),
                        camera));

                for (SendableModel model : models) {
                    render(transparentBuilder, model,
                            sortableRenderObject.getX(),
                            sortableRenderObject.getZ());
                }

                TUint8Array data = transparentBuilder.toTypedArray();
                gl.bindBuffer(ARRAY_BUFFER, sortableRenderObject.buffer);
                gl.bufferData(ARRAY_BUFFER, (ArrayBufferView) data,
                        DYNAMIC_DRAW);
                sortableRenderObject.count = data.length() / 26;
            }

            if (updates >= TRANSPARENT_UPDATES_LIMIT && !moved) {
                break;
            }
        }
        Collections.reverse(sortableRenderObjects);
        for (SortableRenderObject sortableRenderObject : sortableRenderObjects) {
            if (sortableRenderObject.count == 0 || sortableRenderObject.buffer == null) continue;
            gl.bindBuffer(ARRAY_BUFFER, sortableRenderObject.buffer);
            chunkShaderAlpha.setOffset(sortableRenderObject.getX(), sortableRenderObject.getZ());
            gl.vertexAttribPointer(chunkShaderAlpha.getPosition(), 3, UNSIGNED_SHORT, false, 26, 0);
            gl.vertexAttribPointer(chunkShaderAlpha.getColour(), 4, UNSIGNED_BYTE, true, 26, 6);
            gl.vertexAttribPointer(chunkShaderAlpha.getTexturePosition(), 2, UNSIGNED_SHORT, false, 26, 10);
            gl.vertexAttribPointer(chunkShaderAlpha.getTextureDetails(), 4, UNSIGNED_SHORT, false, 26, 14);
            gl.vertexAttribPointer(chunkShaderAlpha.getTextureFrames(), 1, UNSIGNED_SHORT, false, 26, 22);
            gl.vertexAttribPointer(chunkShaderAlpha.getLighting(), 2, UNSIGNED_BYTE, false, 26, 24);
            gl.drawArrays(TRIANGLES, 0, sortableRenderObject.count);
        }

        chunkShaderAlpha.disable();
        gl.disable(BLEND);

        requestAnimationFrame(this);
    }

    private void render(ModelBuilder builder, SendableModel model, int cx, int cz) {
        Block owner = model.getOwner(mapViewer);
        int x = model.getX();
        int y = model.getY();
        int z = model.getZ();
        int len = model.getFaces().length();
        for (int fi = 0; fi < len; fi++) {
            SendableModel.Face face = model.getFaces().get(fi);
            if (face.getCullable()) {
                if (!owner.shouldRenderAgainst(mapViewer.getWorld().getBlock(
                        (cx << 4) + x + face.getFace().getOffsetX(),
                        y + face.getFace().getOffsetY(),
                        (cz << 4) + z + face.getFace().getOffsetZ()
                ))) {
                    continue;
                }
            }

            Texture texture = face.getTexture(mapViewer);

            // First triangle
            for (int i = 0; i < 3; i++) {
                ModelVertex vertex = face.getVertices().get(2 - i);
                LightInfo light = Model.calculateLight(mapViewer.getWorld(),
                        (cx << 4) + x + vertex.getX(),
                        y + vertex.getY(),
                        (cz << 4) + z + vertex.getZ(), face.getFace());
                builder
                        .position(x + vertex.getX(), y + vertex.getY(), z + vertex.getZ())
                        .colour(face.getRed(), face.getGreen(), face.getBlue())
                        .texturePosition(vertex.getTextureX(), vertex.getTextureY())
                        .textureDetails(texture.getPosX(), texture.getPosY(), texture.getSize(),
                                texture.getWidth(), texture.getFrames())
                        .lighting(light.getEmittedLight(), light.getSkyLight());
            }
            // Second triangle
            for (int i = 0; i < 3; i++) {
                ModelVertex vertex = face.getVertices().get(1 + i);
                LightInfo light = Model.calculateLight(mapViewer.getWorld(),
                        (cx << 4) + x + vertex.getX(),
                        y + vertex.getY(),
                        (cz << 4) + z + vertex.getZ(), face.getFace());
                builder
                        .position(x + vertex.getX(), y + vertex.getY(), z + vertex.getZ())
                        .colour(face.getRed(), face.getGreen(), face.getBlue())
                        .texturePosition(vertex.getTextureX(), vertex.getTextureY())
                        .textureDetails(texture.getPosX(), texture.getPosY(), texture.getSize(),
                                texture.getWidth(), texture.getFrames())
                        .lighting(light.getEmittedLight(), light.getSkyLight());
            }
        }
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
