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

package uk.co.thinkofdeath.thinkcraft.html.client.debug;

import elemental.html.Float32Array;
import elemental.html.WebGLUniformLocation;
import uk.co.thinkofdeath.thinkcraft.html.client.render.ShaderProgram;
import uk.co.thinkofdeath.thinkcraft.html.shared.buffer.JavascriptBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4;

public class DebugShader extends ShaderProgram {

    // Uniforms
    private WebGLUniformLocation perspectiveMatrix;
    private WebGLUniformLocation viewMatrix;
    // Attributes
    private int position;
    private int colour;

    public DebugShader() {
        super();
    }

    @Override
    protected String getVertexShader() {
        return DebugShaderBundle.INSTANCE.chunkVertexShader().getText();
    }

    @Override
    protected String getFragmentShader() {
        return DebugShaderBundle.INSTANCE.chunkFragmentShader().getText();
    }

    @Override
    protected void init() {
        // Uniforms
        perspectiveMatrix = getUniform("pMatrix");
        viewMatrix = getUniform("uMatrix");
        // Attributes
        position = getAttribute("position");
        colour = getAttribute("colour");
    }

    /**
     * Sets the shader's perspective matrix to the passed matrix
     *
     * @param matrix
     *         The matrix to use
     */
    public void setPerspectiveMatrix(Matrix4 matrix) {
        gl.uniformMatrix4fv(perspectiveMatrix, false, (Float32Array) ((JavascriptBuffer) matrix.getStorage()).getRaw());
    }

    /**
     * Sets the shader's view matrix to the passed matrix
     *
     * @param matrix
     *         The matrix to use
     */
    public void setViewMatrix(Matrix4 matrix) {
        gl.uniformMatrix4fv(viewMatrix, false, (Float32Array) ((JavascriptBuffer) matrix.getStorage()).getRaw());
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
}
