package mapviewer.worker;
import js.html.DataView;
import js.html.Uint8Array;
import mapviewer.block.BlockRegistry;
import mapviewer.block.Blocks;
import mapviewer.renderer.Renderer;
import mapviewer.world.Chunk;
import mapviewer.world.Chunk.ChunkSection;

class WorkerChunk extends Chunk {

	public function new(byteData : Uint8Array) {
		super();
		var data = new DataView(byteData.buffer);
		var sMask = data.getUint16(8);
		x = data.getInt32(0);
		z = data.getInt32(4);
		var offset = 10;
		
		for (i in 0 ... 16) {
			if (sMask & (1 << i) != 0) {
				var idx = 0;
				var cs = sections[i] = ChunkSection.create();
				for (oy in 0 ... 16) {
					for (oz in 0 ... 16) {
						for (ox in 0 ... 16) {
							var id = (byteData[offset] << 8) + byteData[offset + 1];
							var dataVal = byteData[offset + 2];
							var light = byteData[offset + 3];
							var sky = byteData[offset + 4];
							offset += 5;
							var block = BlockRegistry.getByLegacy(id, dataVal);
							var rid = blockMap[block];
							if (rid == null) {
								idMap[nextId] = block;
								rid = blockMap[block] = nextId;
								nextId = (nextId + 1) & 0xFFFF;
							}
							cs.blocks[idx] = rid;
							cs.light[idx] = light;
							cs.sky[idx] = sky;
							idx++;
							
							if (block != Blocks.AIR) cs.count++;
							if (light != 0) cs.count++;
							if (sky != 15) cs.count++;
						}
					}
				}
			}
		}
	}	
	
	public function send() {
		var out : Dynamic = { };
		out.x = x;
		out.z = z;
		
		var secs = new Array<Dynamic>();
		var buffers = new Array<Dynamic>();
		for (i in 0 ... 16) {
			if (sections[i] != null) {
				var s : Dynamic = secs[i] = { };
				s.count = sections[i].count;
				s.buffer = new Uint8Array(sections[i].buffer);
				buffers.push(s.buffer.buffer);
			}
		}
		out.sections = secs;
		
		out.nextId = nextId;
		out.idMap = { };
		for (k in idMap.keys()) {
			out.idMap[k] = idMap[k].toString();
		}
		out.blockMap = { };
		for (k in blockMap.keys()) {
			Reflect.setField(out.blockMap, k.toString(), blockMap[k]);
		}
		WorkerMain.self.postMessage(out, buffers);
	}
	
	override public function unload(renderer : Renderer) {}
}