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
package mapviewer.renderer.webgl.shaders;
import js.html.webgl.Program;
import js.html.webgl.RenderingContext;
import js.html.webgl.Shader;
import js.html.webgl.UniformLocation;
import mapviewer.renderer.webgl.WebGLRenderer.GL;

class TProgram {
	
	private var program : Program;
	private var gl : RenderingContext;
	private var attribs : Array<Int> = new Array();

	public function new(gl : RenderingContext, vertex : String, fragment : String) {
		this.gl = gl;
		program = createProgram(createShader(vertex, GL.VERTEX_SHADER), createShader(fragment, GL.FRAGMENT_SHADER));
	}	
	
	private function getAttrib(name : String) : Int {
		var attr = gl.getAttribLocation(program, name);
		attribs.push(attr);
		return attr;
	}
	
	private function getUniform(name : String) : UniformLocation {
		return gl.getUniformLocation(program, name);
	}
	
	public function use() {
		gl.useProgram(program);
		for (attr in attribs) {
			gl.enableVertexAttribArray(attr);
		}
	}
	
	public function disable() {
		for (attr in attribs) {
			gl.disableVertexAttribArray(attr);
		}		
	}
	
	private function createProgram(vertexShader : Shader, fragmentShader : Shader) : Program {
		var program = gl.createProgram();
		gl.attachShader(program, vertexShader);
		gl.attachShader(program, fragmentShader);
		gl.linkProgram(program);
		gl.useProgram(program);
		gl.deleteShader(vertexShader);
		gl.deleteShader(fragmentShader);
		return program;
	}
	
	public function createShader(source : String, type : Int) : Shader {
		var shader = gl.createShader(type);
		gl.shaderSource(shader, source);
		gl.compileShader(shader);

		if (!gl.getShaderParameter(shader, GL.COMPILE_STATUS)) {
			throw gl.getShaderInfoLog(shader);
		}
		return shader;
	}
}