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
import mapviewer.model.Model;
import mapviewer.renderer.webgl.shaders.ChunkShader;
import mapviewer.world.Chunk;
import js.html.webgl.RenderingContext;

class WebGLChunk extends Chunk {
	
	private var normalBuffers : Array<Buffer>;
	private var normalTriangleCount : Array<Int>;
	
	private var transBuffers : Array<Buffer>;
	private var transBuilders : Array<BlockBuilder>;

	public function new() {
		super();
		normalBuffers = new Array<Buffer>();
		normalTriangleCount = new Array<Int>();
		
		transBuffers = new Array<Buffer>();
		transBuilders = new Array<BlockBuilder>();
	}
	
	public function render(renderer : WebGLRenderer, program : ChunkShader, transparent : Bool) {
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
		
		var hasSet = false;
		
		for (i in 0 ... 16) {
			var section = sections[i];
			if (section == null) continue;
			
			if (!transparent) {
				hasSet = renderBuffer(renderer, gl, program, normalBuffers[i], normalTriangleCount[i], hasSet);
			} else {	
				hasSet = renderTrans(renderer, gl, program, i, hasSet);
			}
		}
	}
	
	@:access(mapviewer.renderer.webgl.BlockBuilder)
	@:access(mapviewer.renderer.webgl.DynamicUint8Array)
	private function renderTrans(renderer : WebGLRenderer, gl : RenderingContext, program : ChunkShader, i : Int, hasSet : Bool) : Bool {
		var section = sections[i];
		if (section != null && section.transBlocks.length > 0) {
			if (!hasSet) {
				program.setOffset(x, z);				
				hasSet = true;
			}
			if (transBuffers[i] == null) {
				transBuffers[i] = gl.createBuffer();
			}
			var sorted : Bool = false;
			if (Main.renderer.shouldResort) {
				section.needSort = true;
			}
			if (section.needSort && Main.renderer.numSorted < WebGLRenderer.SORT_LIMIT) {
				section.transBlocks.sort(sortBlocks);
				Main.renderer.numSorted += section.transBlocks.length;
				sorted = true;
				section.needSort = false;
			}
				
			var builder = transBuilders[i];
			if (builder == null) {
				builder = transBuilders[i] = new BlockBuilder();
			}
			
			if (sorted) {
				builder.buffer.offset = 0; // Reuse the old one to save resizing
				
				Model.dumbLight = true;
				for (b in section.transBlocks) {
					var block = getBlock(b.x, b.y, b.z);
					if (block.renderable && block.transparent)
						block.render(builder, b.x, b.y, b.z, this);
				}
				Model.dumbLight = false;
			}
			
			var data = builder.buffer.getSub();
			gl.bindBuffer(RenderingContext.ARRAY_BUFFER, transBuffers[i]);
			gl.bufferData(RenderingContext.ARRAY_BUFFER, data, RenderingContext.DYNAMIC_DRAW);
			
			gl.vertexAttribPointer(program.position, 3, RenderingContext.UNSIGNED_SHORT, false, 20, 0);
			gl.vertexAttribPointer(program.colour, 4, RenderingContext.UNSIGNED_BYTE, true, 20, 6);
			gl.vertexAttribPointer(program.texturePosition, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 10);
			gl.vertexAttribPointer(program.textureId, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 14);
			gl.vertexAttribPointer(program.lighting, 2, RenderingContext.BYTE, false, 20, 18);
			gl.drawArrays(RenderingContext.TRIANGLES, 0, Std.int(data.length / 20));
		} else {
			if (transBuilders[i] != null) {
				transBuilders[i].free();
				transBuilders[i] == null;
			}
		}
		return hasSet;
	}
	
	private static function sortBlocks(a : TransBlock, b : TransBlock) : Int {		
		var camera = Main.renderer.camera;
		var cx = Std.int(camera.x);
		var cy = Std.int(camera.y);
		var cz = Std.int(camera.z);
		var adx = (a.chunk.x << 4) + a.x - cx;
		var ady = a.y - cy;
		var adz = (a.chunk.z << 4) + a.z - cz;
		var distA = adx * adx + ady * ady + adz * adz;
		var bdx = (b.chunk.x << 4) + b.x - cx;
		var bdy = b.y - cy;
		var bdz = (b.chunk.z << 4) + b.z - cz;
		var distB = bdx * bdx + bdy * bdy + bdz * bdz;
		return distB - distA;
	}
	
	private function renderBuffer(renderer : WebGLRenderer, gl : RenderingContext, program : ChunkShader, 
			buffer : Buffer, triangleCount : Int, hasSet : Bool) : Bool {
		if (buffer != null && triangleCount != 0) {					
			if (!hasSet) {
				program.setOffset(x, z);				
				hasSet = true;
			}
			
			gl.bindBuffer(RenderingContext.ARRAY_BUFFER, buffer);
			gl.vertexAttribPointer(program.position, 3, RenderingContext.UNSIGNED_SHORT, false, 20, 0);
			gl.vertexAttribPointer(program.colour, 4, RenderingContext.UNSIGNED_BYTE, true, 20, 6);
			gl.vertexAttribPointer(program.texturePosition, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 10);
			gl.vertexAttribPointer(program.textureId, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 14);
			gl.vertexAttribPointer(program.lighting, 2, RenderingContext.BYTE, false, 20, 18);
			gl.drawArrays(RenderingContext.TRIANGLES, 0, triangleCount);
		}
		return hasSet;
	}
	
	public function createBuffer(i : Int, data : Uint8Array) {
		var renderer : WebGLRenderer = Main.renderer;
		var gl : RenderingContext = renderer.gl;
		if (data.length > 0) {
			if (normalBuffers[i] == null) {
				normalBuffers[i] = gl.createBuffer();
			}
			gl.bindBuffer(RenderingContext.ARRAY_BUFFER, normalBuffers[i]);
			gl.bufferData(RenderingContext.ARRAY_BUFFER, data, RenderingContext.STATIC_DRAW);
		} else {
			if (normalBuffers[i] != null) {
				gl.deleteBuffer(normalBuffers[i]);
				normalBuffers[i] = null;
			}
		}

		normalTriangleCount[i] = Std.int(data.length / 20);
	}
	
	override public function unload(renderer : WebGLRenderer) {
		var web : WebGLRenderer = renderer;
		for (buffer in normalBuffers) {
			if (buffer != null) web.gl.deleteBuffer(buffer);
		}
		for (buffer in transBuffers) {
			if (buffer != null) web.gl.deleteBuffer(buffer);
		}
		for (builder in transBuilders) {
			if (builder != null) builder.free();
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