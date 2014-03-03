package mapviewer.renderer.webgl;
import js.html.Uint8Array;
import js.html.webgl.Buffer;
import mapviewer.js.Utils;
import mapviewer.renderer.Renderer;
import mapviewer.world.Chunk;
import js.html.webgl.RenderingContext;

class WebGLChunk extends Chunk {
	
	private var builtSections : Array<Uint8Array>;
	private var normalBuffers : Array<Buffer>;
	private var normalTriangleCount : Array<Int>;
	
	private var builtSectionsTrans : Array<Uint8Array>;
	private var transBuffers : Array<Buffer>;
	private var transTriangleCount : Array<Int>;

	public function new() {
		super();
		builtSections = new Array<Uint8Array>();
		normalBuffers = new Array<Buffer>();
		normalTriangleCount = new Array<Int>();
		
		builtSectionsTrans = new Array<Uint8Array>();
		transBuffers = new Array<Buffer>();
		transTriangleCount = new Array<Int>();
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
		if (needsUpdate) {
			needsUpdate = false;
			updateChunkBuffers(gl);
		}
		
		for (i in 0 ... 16) {
			var section = sections[i];
			if (section == null) continue;
			
			if (pass == 0) {
				renderBuffer(renderer, gl, normalBuffers[i], normalTriangleCount[i]);
			} else {
				renderBuffer(renderer, gl, transBuffers[i], transTriangleCount[i]);
			}
		}
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
	
	private function updateChunkBuffers(gl : RenderingContext) {
		for (i in 0 ... 16) {
			var section = sections[i];
			if (section != null) {
				if (builtSections[i] == null || !section.needsUpdate) continue; // Not built yet
				section.needsUpdate = false;
		
				if (normalBuffers[i] == null) {
					normalBuffers[i] = gl.createBuffer();
				}
				gl.bindBuffer(RenderingContext.ARRAY_BUFFER, normalBuffers[i]);
				gl.bufferData(RenderingContext.ARRAY_BUFFER, builtSections[i], RenderingContext.STATIC_DRAW);

				if (transBuffers[i] == null) {
					transBuffers[i] = gl.createBuffer();
				}
				gl.bindBuffer(RenderingContext.ARRAY_BUFFER, transBuffers[i]);
				gl.bufferData(RenderingContext.ARRAY_BUFFER, builtSectionsTrans[i], RenderingContext.STATIC_DRAW);

				normalTriangleCount[i] = Std.int(builtSections[i].length / 20);
				transTriangleCount[i] = Std.int(builtSectionsTrans[i].length / 20);
				builtSections[i] = null;
				builtSectionsTrans[i] = null;
			}
		}
	}
	
	override public function buildSection(i : Int, sn : Dynamic, endTime : Int) : Dynamic {
		var snapshot : WebGLSnapshot;
		if (sn == null) {
			snapshot = new WebGLSnapshot();
		} else {
			snapshot = cast sn;
		}
		var builder = snapshot.builder;
		var builderTrans = snapshot.builderTrans;
		for (x in snapshot.x ... 16) {
			snapshot.x = 0;
			for (z in snapshot.z ... 16) {
				snapshot.z = 0;
				for (y in snapshot.y ... 16) {
					snapshot.y = 0;
					var block = getBlock(x, (i << 4) + y, z);
					if (block.renderable) {
						block.render(block.transparent ? builderTrans : builder, x, (i << 4) + y, z, this);
					}
				}
					
				if (Utils.now() >= endTime) {
					snapshot.x = x;
					snapshot.y = 0;
					snapshot.z = z + 1;
					snapshot.builder = builder;
					snapshot.builderTrans = builderTrans;
					return snapshot;
				}
				snapshot.x = snapshot.y = snapshot.z = 0;
			}
			snapshot.x = snapshot.y = snapshot.z = 0;
		}
		// Store
		builtSections[i] = builder.toTypedArray();
		builtSectionsTrans[i] = builderTrans.toTypedArray();
		
		sections[i].needsUpdate = true;
		needsUpdate = true;
		return null;
	}
	
	override public function unload(renderer : Renderer) {
		var web : WebGLRenderer = cast renderer;
		for (buffer in normalBuffers) {
			web.gl.deleteBuffer(buffer);
		}
		for (buffer in transBuffers) {
			web.gl.deleteBuffer(buffer);
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