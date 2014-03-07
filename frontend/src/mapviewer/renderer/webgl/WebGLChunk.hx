package mapviewer.renderer.webgl;
import js.html.Uint8Array;
import js.html.webgl.Buffer;
import mapviewer.js.Utils;
import mapviewer.renderer.Renderer;
import mapviewer.world.Chunk;
import js.html.webgl.RenderingContext;

class WebGLChunk extends Chunk {
	
	private var isPacked : Bool = false; 
	inline private static var PACK_TIME : Int = 150;
	private var unpackedTicks : Int = PACK_TIME;
	// Chunked mode
	private var normalBuffers : Array<Buffer>;
	private var normalTriangleCount : Array<Int>;	
	private var normalData : Array<Uint8Array>;
	private var normalSize : Int = 0;
	private var transBuffers : Array<Buffer>;
	private var transTriangleCount : Array<Int>;
	private var transData : Array<Uint8Array>;
	private var transSize : Int = 0;
	// Packed mode
	private var packedBuffer : Buffer;
	private var packedTriangleCount : Int = 0;
	private var packedTransBuffer : Buffer;
	private var packedTransTriangleCount : Int = 0;

	public function new() {
		super();
		normalBuffers = new Array<Buffer>();
		normalTriangleCount = new Array<Int>();
		normalData = new Array<Uint8Array>();
		
		transBuffers = new Array<Buffer>();
		transTriangleCount = new Array<Int>();
		transData = new Array<Uint8Array>();
	}
	
	public function render(renderer : WebGLRenderer, pass : Int) {
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
		
		if (!isPacked && unpackedTicks <= 0) {
			packChunk();
		}
		
		if (!isPacked) {
			unpackedTicks--;
			for (i in 0 ... 16) {
				var section = sections[i];
				if (section == null) continue;
				
				if (normalBuffers[i] == null && normalData[i] != null) {
					createBuffer(i, normalData[i], transData[i]);
				}
				
				if (pass == 0) {
					renderBuffer(renderer, gl, normalBuffers[i], normalTriangleCount[i]);
				} else {
					renderBuffer(renderer, gl, transBuffers[i], transTriangleCount[i]);
				}
			}
		} else {
			if (pass == 0) {
				renderBuffer(renderer, gl, packedBuffer, packedTriangleCount);
			} else {
				renderBuffer(renderer, gl, packedTransBuffer, packedTransTriangleCount);
			}
		}
	}
	
	private function packChunk() {
		isPacked = true;
		
		var renderer : WebGLRenderer = cast Main.renderer;
		var gl : RenderingContext = renderer.gl;
		
		var pData = new Uint8Array(normalSize);
		var offset = 0;
		var pTransData = new Uint8Array(transSize);
		var tOffset = 0;
		for (i in 0 ... 16) {
			if (sections[i] == null || normalData[i] == null) continue;
			packedTriangleCount += normalTriangleCount[i];
			packedTransTriangleCount += transTriangleCount[i];
			pData.set(normalData[i], offset);
			offset += normalData[i].length;
			pTransData.set(transData[i], tOffset);
			tOffset += transData[i].length;
		}
		
		packedBuffer = gl.createBuffer();
		gl.bindBuffer(RenderingContext.ARRAY_BUFFER, packedBuffer);
		gl.bufferData(RenderingContext.ARRAY_BUFFER, pData, RenderingContext.STATIC_DRAW);
		
		packedTransBuffer = gl.createBuffer();
		gl.bindBuffer(RenderingContext.ARRAY_BUFFER, packedTransBuffer);
		gl.bufferData(RenderingContext.ARRAY_BUFFER, pTransData, RenderingContext.STATIC_DRAW);
	}
	
	private function renderBuffer(renderer : WebGLRenderer, gl : RenderingContext, buffer : Buffer, triangleCount : Int) {
		if (buffer != null && triangleCount != 0) {
			gl.uniform2f(renderer.offsetLocation, x, z);
			gl.bindBuffer(RenderingContext.ARRAY_BUFFER, buffer);
			gl.vertexAttribPointer(renderer.positionLocation, 3, RenderingContext.UNSIGNED_SHORT, false, 20, 0);
			gl.vertexAttribPointer(renderer.colourLocation, 3, RenderingContext.UNSIGNED_BYTE, true, 20, 6);
			gl.vertexAttribPointer(renderer.texturePosLocation, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 10);
			gl.vertexAttribPointer(renderer.textureIdLocation, 2, RenderingContext.UNSIGNED_SHORT, false, 20, 14);
			gl.vertexAttribPointer(renderer.lightingLocation, 2, RenderingContext.BYTE, false, 20, 18);
			gl.drawArrays(RenderingContext.TRIANGLES, 0, triangleCount);
		}
	}
	
	public function createBuffer(i : Int, data : Uint8Array, dataTrans : Uint8Array) {
		var renderer : WebGLRenderer = cast Main.renderer;
		var gl : RenderingContext = renderer.gl;
		if (isPacked) {
			isPacked = false;
			unpackedTicks = PACK_TIME;
			gl.deleteBuffer(packedBuffer);
			packedBuffer = null;
			packedTriangleCount = 0;
			gl.deleteBuffer(packedTransBuffer);
			packedTransBuffer = null;
			packedTransTriangleCount = 0;
		}
		if (normalBuffers[i] == null) {
			normalBuffers[i] = gl.createBuffer();
		}
		gl.bindBuffer(RenderingContext.ARRAY_BUFFER, normalBuffers[i]);
		gl.bufferData(RenderingContext.ARRAY_BUFFER, data, RenderingContext.STATIC_DRAW);
		
		normalSize -= normalData[i] == null ? 0 : normalData[i].length;
		normalData[i] = data;
		normalSize += normalData[i].length;

		if (transBuffers[i] == null) {
			transBuffers[i] = gl.createBuffer();
		}
		gl.bindBuffer(RenderingContext.ARRAY_BUFFER, transBuffers[i]);
		gl.bufferData(RenderingContext.ARRAY_BUFFER, dataTrans, RenderingContext.STATIC_DRAW);
		
		transSize -= transData[i] == null ? 0 : transData[i].length;
		transData[i] = dataTrans;
		transSize += transData[i].length;

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
		if (packedBuffer != null) web.gl.deleteBuffer(packedBuffer);
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