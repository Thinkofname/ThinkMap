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

package uk.co.thinkofdeath.mapviewer.client.render.shaders;

import elemental.html.WebGLUniformLocation;
import uk.co.thinkofdeath.mapviewer.client.render.ShaderProgram;
import uk.co.thinkofdeath.mapviewer.shared.glmatrix.Mat4;

public class ChunkShader extends ShaderProgram {

    // Uniforms
    private WebGLUniformLocation perspectiveMatrix;
    private WebGLUniformLocation viewMatrix;
    private WebGLUniformLocation offset;
    private WebGLUniformLocation frame;
    private WebGLUniformLocation scale;
    private WebGLUniformLocation[] blockTextures = new WebGLUniformLocation[5];
    // Attributes
    private int position;
    private int colour;
    private int textureDetails;
    private int textureFrames;
    private int texturePosition;
    private int lighting;
    // Flags
    private boolean alpha;

    public ChunkShader(boolean alpha) {
        super();
        this.alpha = alpha;
    }

    @Override
    protected String getVertexShader() {
        return ShaderBundle.INSTANCE.chunkVertexShader().getText();
    }

    @Override
    protected String getFragmentShader() {
        return (alpha ? "#define alpha\n" : "") + ShaderBundle.INSTANCE.chunkFragmentShader().getText();
    }

    @Override
    protected void init() {
        // Uniforms
        perspectiveMatrix = getUniform("pMatrix");
        viewMatrix = getUniform("uMatrix");
        offset = getUniform("offset");
        frame = getUniform("frame");
        scale = getUniform("scale");
        for (int i = 0; i < blockTextures.length; i++) {
            blockTextures[i] = getUniform("texture[" + i + "]");
        }
        // Attributes
        position = getAttribute("position");
        colour = getAttribute("colour");
        textureDetails = getAttribute("textureDetails");
        textureFrames = getAttribute("textureFrames");
        texturePosition = getAttribute("texturePos");
        lighting = getAttribute("lighting");
    }

    /**
     * Sets the shader's perspective matrix to the passed matrix
     *
     * @param matrix
     *         The matrix to use
     */
    public void setPerspectiveMatrix(Mat4 matrix) {
        gl.uniformMatrix4fv(perspectiveMatrix, false, matrix);
    }

    /**
     * Sets the shader's view matrix to the passed matrix
     *
     * @param matrix
     *         The matrix to use
     */
    public void setViewMatrix(Mat4 matrix) {
        gl.uniformMatrix4fv(viewMatrix, false, matrix);
    }

    /**
     * Sets the offset of the blocks (in chunk coordinates)
     *
     * @param x
     *         Offset on the x axis
     * @param z
     *         Offset on the z axis
     */
    public void setOffset(int x, int z) {
        gl.uniform2f(offset, x, z);
    }

    /**
     * Sets the current frame
     *
     * @param i
     *         Frame
     */
    public void setFrame(int i) {
        gl.uniform1f(frame, i);
    }

    /**
     * Sets the time of day scale value
     *
     * @param i
     *         Time of day scale value
     */
    public void setScale(float i) {
        gl.uniform1f(scale, i);
    }

    /**
     * Sets the id/position of the block texture
     *
     * @param id
     *         The id/position of the texture
     */
    public void setBlockTexture(int idx, int id) {
        gl.uniform1i(blockTextures[idx], id);
    }

    /**
     * Returns the position attribute
     *
     * @return The position attribute
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the colour attribute
     *
     * @return The colour attribute
     */
    public int getColour() {
        return colour;
    }

    /**
     * Returns the texture id attribute
     *
     * @return The texture id attribute
     */
    public int getTextureDetails() {
        return textureDetails;
    }

    public int getTextureFrames() {
        return textureFrames;
    }

    /**
     * Returns the texture position attribute
     *
     * @return The texture position attribute
     */
    public int getTexturePosition() {
        return texturePosition;
    }

    /**
     * Returns the lighting attribute
     *
     * @return The lighting attribute
     */
    public int getLighting() {
        return lighting;
    }
}
