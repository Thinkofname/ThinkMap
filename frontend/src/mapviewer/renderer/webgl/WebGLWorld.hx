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
import js.html.Float32Array;
import js.html.webgl.Buffer;
import js.html.webgl.Framebuffer;
import js.html.webgl.Renderbuffer;
import js.html.webgl.RenderingContext;
import js.html.webgl.Texture;
import mapviewer.js.Utils;
import mapviewer.Main;
import mapviewer.renderer.webgl.shaders.ChunkShader;
import mapviewer.world.Chunk;
import mapviewer.world.World;
import mapviewer.renderer.webgl.WebGLRenderer.GL;

class WebGLWorld extends World {
	
	private var chunkList : Array<WebGLChunk>;
	
	private var needUpdate : Bool = true;

	public function new() {
		super(true);
		chunkList = new Array<WebGLChunk>();
	}
	
	public function render(renderer : WebGLRenderer, program : ChunkShader) {	
		var gl = renderer.gl;
		chunkList.sort(chunkSort);
		
		var scale = (Main.world.currentTime - 6000.0) / 12000.0;
		if (scale > 1.0) {
			scale = 2.0 - scale;
		} else if (scale < 0.0) {
			scale = -scale;
		}
		scale = 1.0 - scale;
		program.setScale(scale);
		if (renderer.currentFrame != renderer.previousFrame) program.setFrame(Std.int(renderer.currentFrame));
			
		gl.viewport(0, 0, gl.canvas.width, gl.canvas.height);	
		gl.colorMask(true, true, true, false);
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		for (chunk in chunkList) {
			chunk.render(renderer, program, false);
		}		
		
		
	}
	
	public function resize(gl : RenderingContext, renderer : WebGLRenderer) {
	}	
	
	private static function chunkSort(a : WebGLChunk, b : WebGLChunk) : Int {
		var renderer : WebGLRenderer = cast Main.renderer;
		var ax = a.x * 16 + 8 - Std.int(renderer.camera.x);
		var az = a.z * 16 + 8 - Std.int(renderer.camera.z);
		var bx = b.x * 16 + 8 - Std.int(renderer.camera.x);
		var bz = b.z * 16 + 8 - Std.int(renderer.camera.z);
		return (bx * bx + bz * bz) - (ax * ax + az * az);
	}

    override public function addChunk(chunk : Chunk) : Bool {
        if (!super.addChunk(chunk)) {
			return false;
		}
		chunkList.push(cast chunk);
		return true;
    }

    override public function removeChunk(x : Int, z : Int) {
        var chunk = getChunk(x, z);
		chunkList.remove(cast chunk);
		super.removeChunk(x, z);
    }
	
	override public function newChunk() : Chunk {
		return new WebGLChunk();
	}
	
	override public function queueCompare(a : BuildJob, b : BuildJob) : Int {
		var web : WebGLRenderer = cast Main.renderer;
		var camera = web.camera;
		var adx = (a.chunk.x * 16) + 8 - camera.x;
		var ady = (a.i * 16) + 8 - camera.y;
		var adz = (a.chunk.z * 16) + 8 - camera.z;
		var distA = adx * adx + ady * ady + adz * adz;
		var bdx = (b.chunk.x * 16) + 8 - camera.x;
		var bdy = (b.i * 16) + 8 - camera.y;
		var bdz = (b.chunk.z * 16) + 8 - camera.z;
		var distB = bdx * bdx + bdy * bdy + bdz * bdz;
		return Std.int(distB - distA);
	}
}