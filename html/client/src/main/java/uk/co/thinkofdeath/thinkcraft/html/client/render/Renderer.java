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
import uk.co.thinkofdeath.thinkcraft.html.client.debug.Debug;
import uk.co.thinkofdeath.thinkcraft.html.client.render.shaders.ChunkShader;
import uk.co.thinkofdeath.thinkcraft.html.client.texture.TextureMetadata;
import uk.co.thinkofdeath.thinkcraft.html.client.texture.VirtualTexture;
import uk.co.thinkofdeath.thinkcraft.html.client.world.ClientChunk;
import uk.co.thinkofdeath.thinkcraft.html.shared.utils.JsUtils;
import uk.co.thinkofdeath.thinkcraft.shared.Face;
import uk.co.thinkofdeath.thinkcraft.shared.Position;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.util.IntMap;
import uk.co.thinkofdeath.thinkcraft.shared.util.PositionChunkSectionSet;
import uk.co.thinkofdeath.thinkcraft.shared.vector.Frustum;
import uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4;
import uk.co.thinkofdeath.thinkcraft.shared.world.ChunkSection;

import java.util.ArrayList;
import java.util.List;

import static elemental.html.WebGLRenderingContext.*;

public class Renderer implements RendererUtils.ResizeHandler, Runnable {

    private static final int TRANSPARENT_UPDATES_LIMIT = 5;
    private static final int VERTEX_SIZE = 18;

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
    private final Matrix4 combinedMatrix = new Matrix4();

    private final Frustum frustum = new Frustum();

    // Textures
    private final Int32Array textureLocations = RendererUtils.createInt32(new int[]{1, 2, 3, 4, 5});
    private final UByteBuffer textureDetails = Platform.alloc().ubyteBuffer(128 * 128 * 4);
    private final WebGLTexture glTextureDetails;
    private boolean textureDetailsDirty = true;
    private final VirtualTexture[] virtualTextures = new VirtualTexture[5];
    private final WebGLTexture[] textures = new WebGLTexture[5];
    private boolean[] textureDirty = new boolean[5];
    // Objects
    private final ChunkShader chunkShader;
    private final ChunkShader chunkShaderAlpha;

    private final ArrayList<SortableRenderObject> sortableRenderObjects = new ArrayList<>();

    private double lastFrame;
    private double currentFrame;

    // Sorters
    private final SortableSorter sortableSorter = new SortableSorter(camera);

    // Reused vars
    private final PositionChunkSectionSet visited = new PositionChunkSectionSet();
    private final IntMap<Position> toVisit = new IntMap<>();
    private int toVisitPosition = 0;
    private List<AnimatedTexture> animatedTextures = new ArrayList<>();

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

        if (gl == null) throw new UnsupportedOperationException("WebGL not supported");

        onResize(); // Setup canvas

        gl.enable(DEPTH_TEST);
        gl.enable(CULL_FACE);
        gl.cullFace(BACK);
        gl.frontFace(CCW);
        chunkShader = new ChunkShader(false);
        chunkShaderAlpha = new ChunkShader(true);

        chunkShader.setup(gl);
        chunkShaderAlpha.setup(gl);

        // Texture details
        glTextureDetails = gl.createTexture();
        gl.activeTexture(TEXTURE0);
        gl.bindTexture(TEXTURE_2D, glTextureDetails);
        gl.pixelStorei(UNPACK_FLIP_Y_WEBGL, 0);
        gl.texImage2D(TEXTURE_2D, 0, RGBA, 128, 128, 0, RGBA, UNSIGNED_BYTE, (ArrayBufferView) textureDetails);
        // Nearest filtering gives a Minecrafty look
        gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST);
        gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST);
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE);
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE);
        gl.bindTexture(TEXTURE_2D, null);

        virtualTextures[0] = new VirtualTexture(0);
        textures[0] = loadTexture(virtualTextures[0]);
        gl.activeTexture(TEXTURE1);
        gl.bindTexture(TEXTURE_2D, textures[0]);

        Debug.init(gl);
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

        float timeScale = (mapViewer.getWorld().getTimeOfDay() - 6000f) / 12000f;
        if (timeScale > 1) {
            timeScale = 2 - timeScale;
        } else if (timeScale < 0) {
            timeScale = -timeScale;
        }
        timeScale = 1 - timeScale;

        for (AnimatedTexture animatedTexture : animatedTextures) {
            animatedTexture.update((1d / 3d) * delta);
        }

        if (textureDetailsDirty) {
            textureDetailsDirty = false;
            gl.activeTexture(TEXTURE0);
            gl.bindTexture(TEXTURE_2D, glTextureDetails);
            gl.pixelStorei(UNPACK_FLIP_Y_WEBGL, 0);
            gl.texImage2D(TEXTURE_2D, 0, RGBA, 128, 128, 0, RGBA, UNSIGNED_BYTE, (ArrayBufferView) textureDetails);
        }

        for (int i = 0; i < textureDirty.length; i++) {
            if (textureDirty[i]) {
                textureDirty[i] = false;
                gl.activeTexture(TEXTURE1 + i);
                updateTexture(virtualTextures[i], textures[i]);
            }
        }


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

        combinedMatrix.identity();
        combinedMatrix.copy(viewMatrix);
        combinedMatrix.multiply(perspectiveMatrix);
        frustum.fromMatrix(combinedMatrix);


        chunkShader.use();

        chunkShader.setPerspectiveMatrix(perspectiveMatrix);
        chunkShader.setViewMatrix(viewMatrix);
        chunkShader.setBlockTexture(textureLocations);
        chunkShader.setTextureDetails(0);
        chunkShader.setScale(timeScale);

        renderChunks();

        chunkShader.disable();

        gl.enable(BLEND);
        gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);
        chunkShaderAlpha.use();
        chunkShaderAlpha.setPerspectiveMatrix(perspectiveMatrix);
        chunkShaderAlpha.setViewMatrix(viewMatrix);
        chunkShaderAlpha.setBlockTexture(textureLocations);
        chunkShaderAlpha.setTextureDetails(0);
        chunkShaderAlpha.setScale(timeScale);

        JsUtils.sort(sortableRenderObjects, sortableSorter);
        updateTransparentSections();
        renderTransparentSections();

        chunkShaderAlpha.disable();
        gl.disable(BLEND);

        // Debug hook
        Debug.render(gl, perspectiveMatrix, viewMatrix);

        RendererUtils.requestAnimationFrame(this);
    }

    private void renderTransparentSections() {
        for (int i = sortableRenderObjects.size() - 1; i >= 0; i--) {
            SortableRenderObject sortableRenderObject = sortableRenderObjects.get(i);
            if (sortableRenderObject.count == 0 || sortableRenderObject.buffer == null) continue;

            if (!frustum.isSphereInside(
                    (sortableRenderObject.getX() << 4) + 8,
                    (sortableRenderObject.getY() << 4) + 8,
                    (sortableRenderObject.getZ() << 4) + 8, 16)
                    || !visited.contains(sortableRenderObject.getX(), sortableRenderObject.getY(), sortableRenderObject.getZ())) {
                continue;
            }

            gl.bindBuffer(ARRAY_BUFFER, sortableRenderObject.buffer);
            chunkShaderAlpha.setOffset(sortableRenderObject.getX(), sortableRenderObject.getZ());
            gl.vertexAttribPointer(chunkShaderAlpha.getPosition(), 3, UNSIGNED_SHORT, false, VERTEX_SIZE, 0);
            gl.vertexAttribPointer(chunkShaderAlpha.getColour(), 4, UNSIGNED_BYTE, true, VERTEX_SIZE, 6);
            gl.vertexAttribPointer(chunkShaderAlpha.getTexturePosition(), 2, UNSIGNED_SHORT, false, VERTEX_SIZE, 10);
            gl.vertexAttribPointer(chunkShaderAlpha.getLighting(), 2, UNSIGNED_BYTE, false, VERTEX_SIZE, 14);
            gl.vertexAttribPointer(chunkShaderAlpha.getTextureID(), 1, UNSIGNED_SHORT, false, VERTEX_SIZE, 16);
            gl.drawArrays(TRIANGLES, 0, sortableRenderObject.count);
        }
    }

    private void updateTransparentSections() {
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

        for (int i = 0, sortableRenderObjectsSize = sortableRenderObjects.size(); i < sortableRenderObjectsSize; i++) {
            SortableRenderObject sortableRenderObject = sortableRenderObjects.get(i);

            if (!frustum.isSphereInside(
                    (sortableRenderObject.getX() << 4) + 8,
                    (sortableRenderObject.getY() << 4) + 8,
                    (sortableRenderObject.getZ() << 4) + 8, 16)
                    || !visited.contains(sortableRenderObject.getX(), sortableRenderObject.getY(), sortableRenderObject.getZ())) {
                continue;
            }

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
                UByteBuffer temp = sortableRenderObject.tempArray;
                UByteBuffer data = sortableRenderObject.getData();
                for (PositionedModel model : models) {
                    temp.set(offset, Platform.alloc().ubyteBuffer(data,
                            model.getStart(),
                            model.getLength()));
                    offset += model.getLength();
                }

                gl.bindBuffer(ARRAY_BUFFER, sortableRenderObject.buffer);
                gl.bufferData(ARRAY_BUFFER, (ArrayBufferView) temp,
                        DYNAMIC_DRAW);
                sortableRenderObject.count = temp.size() / VERTEX_SIZE;
            }

            if (updates >= TRANSPARENT_UPDATES_LIMIT && !moved) {
                break;
            }
        }
    }

    private void renderChunks() {
        visited.clear();
        toVisit.clear();
        Position root = new Position((int) camera.getX() >> 4, (int) camera.getY() >> 4, (int) camera.getZ() >> 4);
        toVisit.put(toVisitPosition++, root);
        visited.add(root.getX(), root.getY(), root.getZ());

        while (toVisit.size() != 0) {
            Position position = toVisit.remove(--toVisitPosition);

            boolean special = false;
            int sdx = position.getX() - root.getX();
            int sdy = position.getY() - root.getY();
            int sdz = position.getZ() - root.getZ();
            if (sdx * sdx + sdy * sdy + sdz * sdz <= 1) {
                special = true;
            }

            if (position.getY() < 0 || position.getY() > 15 || !mapViewer.getWorld().isLoaded(position.getX(), position.getZ())) {
                continue;
            }

            ClientChunk chunk = (ClientChunk) mapViewer.getWorld().getChunk(position.getX(), position.getZ());
            ChunkRenderObject renderObject = chunk.getRenderObjects()[position.getY()];

            if (!special && !frustum.isSphereInside(
                    (position.getX() << 4) + 8,
                    (position.getY() << 4) + 8,
                    (position.getZ() << 4) + 8, 16)) {
                continue;
            }

            if (renderObject != null) {
                chunkShader.setOffset(renderObject.x, renderObject.z);
                gl.bindBuffer(ARRAY_BUFFER, renderObject.buffer);
                gl.vertexAttribPointer(chunkShader.getPosition(), 3, UNSIGNED_SHORT, false, VERTEX_SIZE, 0);
                gl.vertexAttribPointer(chunkShader.getColour(), 4, UNSIGNED_BYTE, true, VERTEX_SIZE, 6);
                gl.vertexAttribPointer(chunkShader.getTexturePosition(), 2, UNSIGNED_SHORT, false, VERTEX_SIZE, 10);
                gl.vertexAttribPointer(chunkShader.getLighting(), 2, UNSIGNED_BYTE, false, VERTEX_SIZE, 14);
                gl.vertexAttribPointer(chunkShader.getTextureID(), 1, UNSIGNED_SHORT, false, VERTEX_SIZE, 16);
                gl.drawArrays(TRIANGLES, 0, renderObject.triangleCount);
            }

            int dx = (int) Math.signum(((int) camera.getX() >> 4) - position.getX());
            int dy = (int) Math.signum(((int) camera.getY() >> 4) - position.getY());
            int dz = (int) Math.signum(((int) camera.getZ() >> 4) - position.getZ());

            ChunkSection section = special ? null : chunk.getSection(position.getY());
            for (Face face : Face.values()) {
                if (special
                        || (face.getOffsetX() != 0 && dx == face.getOffsetX())
                        || (face.getOffsetY() != 0 && dy == face.getOffsetY())
                        || (face.getOffsetZ() != 0 && dz == face.getOffsetZ())) {
                    checkAndGoto(section, position, face, dx, dy, dz, special);
                }
            }
        }
    }

    private void checkAndGoto(ChunkSection section, Position position, Face face, int dx, int dy, int dz, boolean always) {
        for (Face other : Face.values()) {
            if (other != face && (section == null || section.canAccessSide(face, other))) {
                if ((other.getOffsetX() != 0 && (-dx == other.getOffsetX() || dx == 0))
                        || (other.getOffsetY() != 0 && (-dy == other.getOffsetY() || dy == 0))
                        || (other.getOffsetZ() != 0 && (-dz == other.getOffsetZ() || dz == 0))
                        || always) {
                    Position nextPosition = new Position(
                            position.getX() + other.getOffsetX(),
                            position.getY() + other.getOffsetY(),
                            position.getZ() + other.getOffsetZ()
                    );
                    if (!visited.contains(nextPosition.getX(), nextPosition.getY(), nextPosition.getZ())) {
                        toVisit.put(toVisitPosition++, nextPosition);
                        visited.add(nextPosition.getX(), nextPosition.getY(), nextPosition.getZ());
                    }
                }
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
        int oldSize = sortableRenderObjects.size();
        sortableRenderObjects.remove(sortableRenderObject);
        if (oldSize == sortableRenderObjects.size()) {
            System.out.println("Failed to remove sortable: " + oldSize + " -> " + sortableRenderObjects.size());
        }
        gl.deleteBuffer(sortableRenderObject.buffer);
    }

    public void updateChunkObject(ChunkRenderObject renderObject, UByteBuffer data) {
        if (renderObject.buffer == null) {
            renderObject.buffer = gl.createBuffer();
        }
        renderObject.triangleCount = data.size() / VERTEX_SIZE;
        gl.bindBuffer(ARRAY_BUFFER, renderObject.buffer);
        gl.bufferData(ARRAY_BUFFER, (ArrayBufferView) data, STATIC_DRAW);
    }

    /**
     * Removes a chunk object from the renderer
     *
     * @param renderObject
     *         The object to remove
     */
    public void removeChunkObject(ChunkRenderObject renderObject) {
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

    public void updateTexture(VirtualTexture texture, WebGLTexture glTexture) {
        gl.bindTexture(TEXTURE_2D, glTexture);
        gl.pixelStorei(UNPACK_FLIP_Y_WEBGL, 0);
        gl.texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, texture.getTexture());
    }

    public void placeTexture(Texture texture, ImageElement imageElement) {
        VirtualTexture.TexturePosition position = null;
        for (int i = 0; i < virtualTextures.length; i++) {
            VirtualTexture vt = virtualTextures[i];
            if (vt == null) {
                vt = new VirtualTexture(i);
                textures[i] = loadTexture(vt);
                gl.activeTexture(TEXTURE1 + i);
                gl.bindTexture(TEXTURE_2D, textures[i]);
                virtualTextures[i] = vt;
            }
            position = vt.placeTexture(imageElement, 0, 0, imageElement.getWidth());
            if (position == null) continue;
            break;
        }
        if (position == null) return;

        int offset = texture.getId() * 4;
        textureDetails.set(offset, position.getX() / 2);
        textureDetails.set(offset + 1, position.getY() / 2);
        textureDetails.set(offset + 2, imageElement.getWidth());
        textureDetails.set(offset + 3, position.getId());
        textureDetailsDirty = true;

        if (texture.getName().equals("thinkmap:missing_texture")) {
            for (Texture t : mapViewer.getBlocktextures()) {
                if (t.isLoaded()) continue;
                offset = t.getId() * 4;
                textureDetails.set(offset, position.getX() / 2);
                textureDetails.set(offset + 1, position.getY() / 2);
                textureDetails.set(offset + 2, imageElement.getWidth());
                textureDetails.set(offset + 3, position.getId());
            }
        }

        textureDirty[position.getId()] = true;
    }

    public VirtualTexture.TexturePosition[] placeTextures(Texture texture, ImageElement imageElement) {
        VirtualTexture.TexturePosition[] positions = new VirtualTexture.TexturePosition[imageElement.getHeight() / imageElement.getWidth()];
        for (int pos = 0; pos < positions.length; pos++) {
            VirtualTexture.TexturePosition position = null;
            for (int i = 0; i < virtualTextures.length; i++) {
                VirtualTexture vt = virtualTextures[i];
                if (vt == null) {
                    vt = new VirtualTexture(i);
                    textures[i] = loadTexture(vt);
                    gl.activeTexture(TEXTURE1 + i);
                    gl.bindTexture(TEXTURE_2D, textures[i]);
                    virtualTextures[i] = vt;
                }
                position = vt.placeTexture(imageElement, 0, pos * imageElement.getWidth(), imageElement.getWidth());
                if (position == null) continue;
                break;
            }
            positions[pos] = position;
        }

        VirtualTexture.TexturePosition position = positions[0];
        if (position == null) throw new RuntimeException("Texture error");
        int offset = texture.getId() * 4;
        textureDetails.set(offset, position.getX() / 2);
        textureDetails.set(offset + 1, position.getY() / 2);
        textureDetails.set(offset + 2, imageElement.getWidth());
        textureDetails.set(offset + 3, position.getId());
        textureDetailsDirty = true;

        return positions;
    }

    public void addAnimatedTexture(Texture texture, TextureMetadata metadata, VirtualTexture.TexturePosition[] positions) {
        animatedTextures.add(new AnimatedTexture(texture, metadata, positions));
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

    private class AnimatedTexture {
        private final Texture texture;
        private final TextureMetadata metadata;
        private final VirtualTexture.TexturePosition[] positions;

        private final Frame[] frames;

        private Frame currentFrame;
        private int cfId = 0;
        private double remainingTime;
        private boolean changed = true;

        public AnimatedTexture(Texture texture, TextureMetadata metadata, VirtualTexture.TexturePosition[] positions) {
            this.texture = texture;
            this.metadata = metadata;
            this.positions = positions;
            if (metadata.getFrames() == null) {
                frames = new Frame[positions.length];
                for (int i = 0; i < frames.length; i++) {
                    frames[i] = new Frame(metadata.getFrameTime(), positions[i]);
                }
            } else {
                frames = new Frame[metadata.getFrames().length];
                for (int i = 0; i < frames.length; i++) {
                    frames[i] = new Frame(
                            metadata.getFrameTime(),
                            positions[metadata.getFrames()[i]]
                    );
                }
            }

            currentFrame = frames[0];
            remainingTime = currentFrame.frameTime;
        }

        public void update(double delta) {

            if (changed) {
                int offset = texture.getId() * 4;

                VirtualTexture.TexturePosition position = currentFrame.position;
                textureDetails.set(offset, position.getX() / 2);
                textureDetails.set(offset + 1, position.getY() / 2);
                textureDetails.set(offset + 3, position.getId());

                textureDetailsDirty = true;
                changed = false;
            }

            remainingTime -= delta;
            if (remainingTime <= 0) {
                cfId++;
                cfId %= frames.length;
                currentFrame = frames[cfId];
                remainingTime = currentFrame.frameTime;
                changed = true;
            }
        }

        private class Frame {
            private int frameTime;
            private VirtualTexture.TexturePosition position;

            private Frame(int frameTime, VirtualTexture.TexturePosition position) {
                this.frameTime = frameTime;
                this.position = position;
            }
        }
    }
}
