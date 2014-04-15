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

import elemental.html.WebGLProgram;
import elemental.html.WebGLRenderingContext;
import elemental.html.WebGLShader;
import elemental.html.WebGLUniformLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class ShaderProgram {

    protected WebGLRenderingContext gl;

    private WebGLProgram program;
    private List<Integer> attributes = new ArrayList<>();

    /**
     * Creates a shader
     */
    protected ShaderProgram() {
    }

    /**
     * Init and compile the program
     *
     * @param gl
     *         The WebGL Context
     */
    public void setup(WebGLRenderingContext gl) {
        this.gl = gl;
        compile();
    }

    /**
     * Sets this program as the active program for the WebGL Context and enables all vertex
     * attribute arrays
     */
    public void use() {
        gl.useProgram(program);
        for (int attribute : attributes) {
            gl.enableVertexAttribArray(attribute);
        }
    }

    /**
     * Disables all vertex attribute arrays for the program
     */
    public void disable() {
        for (int attribute : attributes) {
            gl.disableVertexAttribArray(attribute);
        }
    }

    /**
     * Compiles the shaders into a program
     */
    private void compile() {
        program = gl.createProgram();
        WebGLShader vs = createShader(getVertexShader(), WebGLRenderingContext.VERTEX_SHADER);
        WebGLShader fs = createShader(getFragmentShader(), WebGLRenderingContext.FRAGMENT_SHADER);
        gl.attachShader(program, vs);
        gl.attachShader(program, fs);
        gl.linkProgram(program);
        gl.useProgram(program);
        gl.deleteShader(vs);
        gl.deleteShader(fs);

        // Init uniforms and attributes
        init();
    }

    /**
     * Creates a shader from a source string
     *
     * @param source
     *         The shader source
     * @param type
     *         The type of shader. Either VERTEX_SHADER or FRAGMENT_SHADER
     * @return The created WebGLShader
     * @throws uk.co.thinkofdeath.mapviewer.client.render.ShaderProgram.ShaderError
     *         Thrown if the shader fails to compile
     */
    private WebGLShader createShader(String source, int type) {
        WebGLShader shader = gl.createShader(type);
        gl.shaderSource(shader, source);
        gl.compileShader(shader);
        if (!(Boolean) gl.getShaderParameter(shader, WebGLRenderingContext.COMPILE_STATUS))
            throw new ShaderError(gl.getShaderInfoLog(shader));
        return shader;
    }

    /**
     * Returns the source for the vertex shader
     *
     * @return the shader source
     */
    protected abstract String getVertexShader();

    /**
     * Returns the source for the fragment shader
     *
     * @return the shader source
     */
    protected abstract String getFragmentShader();

    /**
     * Called when the shader is compiled so that uniforms and attributes may be setup
     */
    protected abstract void init();

    /**
     * Returns the location of the attribute in the program
     *
     * @param name
     *         The name of the attribute
     * @return The attribute location
     */
    protected int getAttribute(String name) {
        int attribute = gl.getAttribLocation(program, name);
        attributes.add(attribute);
        return attribute;
    }

    /**
     * Returns the location of the uniform in the program
     *
     * @param name
     *         The name of the uniform
     * @return The uniform location
     */
    protected WebGLUniformLocation getUniform(String name) {
        return gl.getUniformLocation(program, name);
    }

    /**
     * Thrown when a shader fails to compile
     */
    private class ShaderError extends RuntimeException {
        public ShaderError(String shaderInfoLog) {
            super(shaderInfoLog);
        }

        private ShaderError() {
        }
    }
}
