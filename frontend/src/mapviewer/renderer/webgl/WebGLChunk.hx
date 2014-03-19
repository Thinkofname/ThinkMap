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
package mapviewer.renderer.webgl;
import js.html.Uint8Array;
import js.html.webgl.Buffer;
import mapviewer.js.Utils;
import mapviewer.renderer.Renderer;
import mapviewer.renderer.webgl.shaders.ChunkShader;
import mapviewer.world.Chunk;
import js.html.webgl.RenderingContext;

class WebGLChunk extends Chunk {
	
	private var normalBuffers : Array<Buffer>;
	private var normalTriangleCount : Array<Int>;
	
	private var transBuffers : Array<Buffer>;
	private var transTriangleCount : Array<Int>;

	public function new() {
		super();
		normalBuffers = new Array<Buffer>();
		normalTriangleCount = new Array<Int>();
		
		transBuffers = new Array<Buffer>();
		transTriangleCount = new Array<Int>();
	}
	
	public function render(renderer : WebGLRenderer, program : ChunkShader, pass : Int) {
		var gl = renderer.gl;
		
		if (needsBuild) {
			needsBuild = false;
			for (i in 0 ... 16) {
				var section = sections[i];
				if (section != null && section.needsBuild) {
					section.needsBuild = false;
					world.requestBuild(this, i);
				}
			}
		}
		
		for (i in 0 ... 16) {
			var section = sections[i];
			if (section == null) continue;
			
			if (pass == 0) {
				renderBuffer(renderer, gl, program, normalBuffers[i], normalTriangleCount[i]);
			} else {
				renderBuffer(renderer, gl, program, transBuffers[i], transTriangleCount[i]);
			}
		}
	}
	
	private function renderBuffer(renderer : WebGLRenderer, gl : RenderingContext, program : ChunkShader, 
			buffer : Buffer, triangleCount : Int) {
		if (buffer != null && triangleCount != 0) {
			program.setOffset(x, z);
			
			gl.bindBuffer(RenderingContext.ARRAY_BUFFER, buffer);
			gl.vertexAttribPointer(program.position, 3, RenderingContext.UNSIGNED_SHORT, false, 20, 0);
			gl.vertexAttribPointer(program.colour, 4, RenderingContext.UNSIGNED_BYTE, true, 20, 6);
			gl.vertexAttribPointer(program.texturePosition, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 10);
			gl.vertexAttribPointer(program.textureId, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 14);
			gl.vertexAttribPointer(program.lighting, 2, RenderingContext.BYTE, false, 20, 18);
			gl.drawArrays(RenderingContext.TRIANGLES, 0, triangleCount);
		}
	}
	
	public function createBuffer(i : Int, data : Uint8Array, dataTrans : Uint8Array) {
		var renderer : WebGLRenderer = cast Main.renderer;
		var gl : RenderingContext = renderer.gl;
		if (data.length > 0) {
			if (normalBuffers[i] == null) {
				normalBuffers[i] = gl.createBuffer();
			}
			gl.bindBuffer(RenderingContext.ARRAY_BUFFER, normalBuffers[i]);
			gl.bufferData(RenderingContext.ARRAY_BUFFER, data, RenderingContext.STATIC_DRAW);
		} else {
			normalBuffers[i] = null;
		}
		if (dataTrans.length > 0) {
			if (transBuffers[i] == null) {
				transBuffers[i] = gl.createBuffer();
			}
			gl.bindBuffer(RenderingContext.ARRAY_BUFFER, transBuffers[i]);
			gl.bufferData(RenderingContext.ARRAY_BUFFER, dataTrans, RenderingContext.STATIC_DRAW);
		} else {
			transBuffers[i] = null;
		}

		normalTriangleCount[i] = Std.int(data.length / 20);
		transTriangleCount[i] = Std.int(dataTrans.length / 20);
	}
	
	override public function unload(renderer : Renderer) {
		var web : WebGLRenderer = cast renderer;
		for (buffer in normalBuffers) {
			if (buffer != null) web.gl.deleteBuffer(buffer);
		}
		for (buffer in transBuffers) {
			if (buffer != null) web.gl.deleteBuffer(buffer);
		}
	}
}

class WebGLSnapshot {
	public var builder : BlockBuilder;
	public var builderTrans : BlockBuilder;
	public var x : Int = 0;
	public var y : Int = 0;
	public var z : Int = 0;
	
	public function new() {
		builder = new BlockBuilder();
		builderTrans = new BlockBuilder();
	}
}