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
import mapviewer.renderer.webgl.shaders.AlphaShader;
import mapviewer.renderer.webgl.shaders.ChunkShader;
import mapviewer.renderer.webgl.shaders.ChunkShaderColour;
import mapviewer.renderer.webgl.shaders.ChunkShaderWeight;
import mapviewer.world.Chunk;
import mapviewer.world.World;
import mapviewer.renderer.webgl.WebGLRenderer.GL;

class WebGLWorld extends World {
	
	private var chunkList : Array<WebGLChunk>;
	
	private var alphaShader : AlphaShader;
	private var colourShader : ChunkShaderColour;
	private var weightShader : ChunkShaderWeight;
	
	private var normalFrameBuffer : Framebuffer;
	private var normalTexture : Texture;
	private var colourFrameBuffer : Framebuffer;
	private var renderBuffer : Renderbuffer;
	private var colourTexture : Texture;
	private var weightFrameBuffer : Framebuffer;
	private var weightRenderBuffer : Renderbuffer;
	private var weightTexture : Texture;
	
	private var buffer : Buffer;
	
	private var scaleX : Float = 0;
	private var scaleY : Float = 0;
	private var screenX : Int = 0;
	private var screenY : Int = 0;

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
			
		gl.bindFramebuffer(GL.FRAMEBUFFER, normalFrameBuffer);
		gl.viewport(0, 0, screenX, screenY);	
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		for (chunk in chunkList) {
			chunk.render(renderer, program, 0);
		}		
		
		gl.depthMask(false);
		gl.bindFramebuffer(GL.FRAMEBUFFER, colourFrameBuffer);
		gl.viewport(0, 0, screenX, screenY);
		gl.clearColor(0.0, 0.0, 0.0, 0.0);	
		gl.clear(GL.COLOR_BUFFER_BIT);
		gl.enable(GL.BLEND);
		gl.blendFunc(GL.ONE, GL.ONE);
		program.disable();
		colourShader.use();
		colourShader.setFrame(Std.int(renderer.currentFrame));
		colourShader.setScale(scale);
		colourShader.setPerspectiveMatrix(renderer.pMatrix);
		colourShader.setUMatrix(renderer.temp2);
		for (chunk in chunkList) {
			chunk.render(renderer, colourShader, 1);
		}
		
		
		gl.depthMask(false);	
		gl.bindFramebuffer(GL.FRAMEBUFFER, weightFrameBuffer);
		gl.viewport(0, 0, screenX, screenY);
		gl.clearColor(1.0, 0.0, 0.0, 0.0);
		gl.clear(GL.COLOR_BUFFER_BIT);	
		gl.blendFunc(GL.ZERO, GL.ONE_MINUS_SRC_ALPHA);
		colourShader.disable();
		weightShader.use();
		weightShader.setFrame(Std.int(renderer.currentFrame));
		weightShader.setScale(scale);
		weightShader.setPerspectiveMatrix(renderer.pMatrix);
		weightShader.setUMatrix(renderer.temp2);
		for (chunk in chunkList) {
			chunk.render(renderer, weightShader, 1);
		}
		
		gl.bindFramebuffer(GL.FRAMEBUFFER, null);
		gl.viewport(0, 0, renderer.canvas.width, renderer.canvas.height);
		gl.clearColor(0.0, 0.0, 0.0, 0.0);
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		program.disable();
		alphaShader.use();
		gl.disable(GL.BLEND);
		alphaShader.setScreenSize(renderer.canvas.width, renderer.canvas.height);
		alphaShader.setScale(scaleX, scaleY);
		
		gl.activeTexture(GL.TEXTURE0);
		gl.bindTexture(GL.TEXTURE_2D, colourTexture);
		gl.activeTexture(GL.TEXTURE1);
		gl.bindTexture(GL.TEXTURE_2D, weightTexture);
		gl.activeTexture(GL.TEXTURE2);
		gl.bindTexture(GL.TEXTURE_2D, normalTexture);
		alphaShader.setBuffers(0, 1, 2);
		
		gl.bindBuffer(GL.ARRAY_BUFFER, buffer);
		gl.vertexAttribPointer(alphaShader.position, 2, GL.FLOAT, false, 0, 0);
		gl.drawArrays(GL.TRIANGLES, 0, 6);
		
		alphaShader.disable();
		gl.disable(GL.BLEND);
		gl.depthMask(true);
		
	}
	
	public function resize(gl : RenderingContext, renderer : WebGLRenderer) {
		gl.deleteFramebuffer(normalFrameBuffer);
		gl.deleteFramebuffer(colourFrameBuffer);
		gl.deleteFramebuffer(weightFrameBuffer);
		gl.deleteRenderbuffer(renderBuffer);
		gl.deleteTexture(normalTexture);
		gl.deleteTexture(colourTexture);
		gl.deleteTexture(weightTexture);
		initBuffers(gl, renderer);
	}
	
	public function initBuffers(gl : RenderingContext, renderer : WebGLRenderer) {
		var canFloat = gl.getExtension("WEBGL_color_buffer_float") != null || gl.getExtension("OES_texture_float") != null;
		// Downscale for now
		var sx = Math.round(renderer.canvas.width);
		var sy = Math.round(renderer.canvas.height);
		
		var sizeW = getSize(sx, gl);
		var sizeH = getSize(sy, gl);
		if (renderer.canvas.width > renderer.canvas.height) {
			if (sx < sizeW) {
				screenX = sx;
				screenY = sy;
			} else {
				screenX = Math.round(sizeW);
				screenY = Math.round(sy * (sizeW / sx));
			}
		} else {			
			if (sy < sizeH) {
				screenX = sx;
				screenY = sy;
			} else {
				screenY = Math.round(sizeH);
				screenX = Math.round(sx * (sizeH / sy));
			}
		}
		scaleX = screenX / sizeW;
		scaleY = screenY / sizeH;
		
		renderBuffer = gl.createRenderbuffer();
		gl.bindRenderbuffer(GL.RENDERBUFFER, renderBuffer);
		gl.renderbufferStorage(GL.RENDERBUFFER, GL.DEPTH_COMPONENT16, sizeW, sizeH);
		
		// Normal
		normalFrameBuffer = gl.createFramebuffer();
		gl.bindFramebuffer(GL.FRAMEBUFFER, normalFrameBuffer);
		
		normalTexture = gl.createTexture();
		gl.bindTexture(GL.TEXTURE_2D, normalTexture);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE);
		
		gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGB, sizeW, sizeH, 0, GL.RGB, GL.UNSIGNED_BYTE, null);
		
		gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, normalTexture, 0);
		gl.framebufferRenderbuffer(GL.FRAMEBUFFER, GL.DEPTH_ATTACHMENT, GL.RENDERBUFFER, renderBuffer);
		
		// Colour
		
		colourFrameBuffer = gl.createFramebuffer();
		gl.bindFramebuffer(GL.FRAMEBUFFER, colourFrameBuffer);
		
		colourTexture = gl.createTexture();
		gl.bindTexture(GL.TEXTURE_2D, colourTexture);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE);
		
		gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA, sizeW, sizeH, 0, GL.RGBA, canFloat ? GL.FLOAT : GL.UNSIGNED_BYTE, null);
		
		gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, colourTexture, 0);
		gl.framebufferRenderbuffer(GL.FRAMEBUFFER, GL.DEPTH_ATTACHMENT, GL.RENDERBUFFER, renderBuffer);
		
		// Weight
		
		weightFrameBuffer = gl.createFramebuffer();
		gl.bindFramebuffer(GL.FRAMEBUFFER, weightFrameBuffer);
		
		weightTexture = gl.createTexture();
		gl.bindTexture(GL.TEXTURE_2D, weightTexture);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE);
		gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE);
		
		gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA, sizeW, sizeH, 0, GL.RGBA, GL.UNSIGNED_BYTE, null);
		
		gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, weightTexture, 0);
		gl.framebufferRenderbuffer(GL.FRAMEBUFFER, GL.DEPTH_ATTACHMENT, GL.RENDERBUFFER, renderBuffer);
		
        gl.bindTexture(GL.TEXTURE_2D, null);
        gl.bindRenderbuffer(GL.RENDERBUFFER, null);
        gl.bindFramebuffer(GL.FRAMEBUFFER, null);
		
		if (buffer == null) {
			buffer = gl.createBuffer();
			gl.bindBuffer(GL.ARRAY_BUFFER, buffer);
			gl.bufferData(GL.ARRAY_BUFFER, new Float32Array([
				-1.0, -1.0,
				1.0, -1.0,
				-1.0, 1.0,
				1.0, 1.0,
				-1.0, 1.0,
				1.0, -1.0
			]), GL.STATIC_DRAW); 
		}
		
		if (alphaShader == null) {
			alphaShader = new AlphaShader(gl);
			colourShader = new ChunkShaderColour(gl);
			weightShader = new ChunkShaderWeight(gl);
		}
	}
	
	private function getSize(x : Int, gl : RenderingContext) : Int {
		var size = Math.pow(2, Math.ceil(Math.log(x) / Math.log(2)));
		var max = gl.getParameter(GL.MAX_TEXTURE_SIZE);
		if (size > max) {
			size = max;
		}
		max = gl.getParameter(GL.MAX_VIEWPORT_DIMS);
		if (size > max) {
			size = max;
		}
		return Std.int(size);
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