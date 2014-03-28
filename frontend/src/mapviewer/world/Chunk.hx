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
package mapviewer.world;

import mapviewer.block.Block;
import mapviewer.renderer.webgl.WebGLRenderer;
import Reflect;
import mapviewer.block.BlockRegistry;
import haxe.io.Bytes;
import haxe.crypto.BaseCode;
import js.html.Uint8Array;
import js.html.Uint16Array;
import js.html.Uint8Array;
import mapviewer.block.Blocks;
class Chunk {

    public var x : Int;
    public var z : Int;
    public var sections : Array<ChunkSection>;
    public var needsUpdate : Bool = false;
    private var needsBuild : Bool =  false;
    public var world : World;

    private var idMap : Map<Int, Block>;
    private var blockMap : Map<Block, Int>;
    private var nextId = 1;

    public function new() {
        sections = new Array<ChunkSection>();
        idMap = new Map<Int, Block>();
        idMap[0] = Blocks.AIR;
        blockMap = new Map<Block, Int>();
        blockMap[Blocks.AIR] = 0;
    }

    /**
     * Sets the block at the location given by the x, y
     * and z coordinates relative to the chunk.
     *
     * The x and z coordinates must be between 0 and 15.
     * The y coordinate must be between 0 and 255
     */
    public function setBlock(x : Int, y : Int, z : Int, block : Block) {
        var section = sections[y >> 4];
        if (section == null) {
            if (block != Blocks.AIR) {
                section = sections[y >> 4] = ChunkSection.create();
            } else {
                return;
            }
        }
        if(!blockMap.exists(block)) {
            // TODO: Deallocate unused ids
            idMap[nextId] = block;
            blockMap[block] = nextId;
            nextId = (nextId + 1) & 0xFFFF;
        }
        needsBuild = true;
        section.needsBuild = true;
        update(x, y, z);
        var idx = x | (z << 4) | ((y & 0xF) << 8);
        var old = idMap[section.blocks[idx]];
        section.blocks[idx] = blockMap[block];
        if (old == Blocks.AIR && block != Blocks.AIR) {
            section.count++;
        } else if (old != Blocks.AIR && block == Blocks.AIR) {
            section.count--;
        }

        if (section.count == 0) sections[y >> 4] = null;
    }

    /**
     * Gets the block at the location given by the x,
     * y and z coordinates relative to the chunk.
     *
     * The x and z coordinates must be between 0 and
     * 15 and y must be between 0 and 255.
     */
    public function getBlock(x : Int, y : Int, z : Int) : Block {
        var section = sections[y >> 4];
        if (section == null) return Blocks.AIR;
        return idMap[section.blocks[x | (z << 4) | ((y & 0xF) << 8)]];
    }

    /**
     * Sets the light at the location given by the x,
     * y and z coordinates relative to the chunk.
     *
     * The x and z coordinates must be between 0 and
     * 15 and y must be between 0 and 255.
     */
    public function setLight(x : Int, y : Int, z : Int, light : Int) {
        var section = sections[y >> 4];
        if (section == null) {
            if (light == 0) return;
            section = sections[y >> 4] = ChunkSection.create();
        }
        needsBuild = true;
        section.needsBuild = true;
        update(x, y, z);

        var idx = x | (z << 4) | ((y & 0xF) << 8);
        var old = section.light[idx];
        section.light[idx] = light;

        if (old == 0 && light != 0) {
            section.count++;
        } else if (old != 0 && light == 0) {
            section.count--;
        }
		
        if (section.count == 0) sections[y >> 4] = null;
    }

    /**
     * Gets the light at the location given by the x,
     * y and z coordinates relative to the chunk.
     *
     * The x and z coordinates must be between 0 and
     * 15 and y must be between 0 and 255.
     */
    public function getLight(x : Int, y : Int, z : Int) : Int {
        var section = sections[y >> 4];
        if (section == null) return 0;
        return section.light[x | (z << 4) | ((y&0xF) << 8)];
    }

    /**
     * Sets the sky at the location given by the x,
     * y and z coordinates relative to the chunk.
     *
     * The x and z coordinates must be between 0 and
     * 15 and y must be between 0 and 255.
     */
    public function setSky(x : Int, y : Int, z : Int, sky : Int) {
        var section = sections[y >> 4];
        if (section == null) {
            if (sky == 0) return;
            section = sections[y >> 4] = ChunkSection.create();
        }
        needsBuild = true;
        section.needsBuild = true;
        update(x, y, z);

        var idx = x | (z << 4) | ((y & 0xF) << 8);
        var old = section.sky[idx];
        section.sky[idx] = sky;

        if (old == 15 && sky != 15) {
            section.count++;
        } else if (old != 15 && sky == 15) {
            section.count--;
        }


        if (section.count == 0) sections[y >> 4] = null;
    }

    /**
     * Gets the sky at the location given by the x,
     * y and z coordinates relative to the chunk.
     *
     * The x and z coordinates must be between 0 and
     * 15 and y must be between 0 and 255.
     */
    public function getSky(x : Int, y : Int, z : Int) : Int {
        var section = sections[y >> 4];
        if (section == null) return 15;
        return section.sky[x | (z << 4) | ((y&0xF) << 8)];
    }

    private function update(x : Int, y : Int, z : Int) {
        for (ox in -1 ... 2) {
            for (oz in -1 ... 2) {
                for (oy in -1 ... 2) {
                    var chunk = world.getChunk(((this.x << 4) + x + ox) >> 4,
                        ((this.z << 4) + z + oz) >> 4);
                    if (chunk == null) continue;
                    chunk.needsBuild = true;
                    var idx = (y + oy) >> 4;
                    if (idx < 0 || idx > 15) continue;
                    var section = chunk.sections[idx];
                    if (section != null) section.needsBuild = true;
                }
            }
        }
    }

    public function rebuild() {
        needsBuild = true;
        for (i in 0 ... 16) {
            var section = sections[i];
            if (section != null) section.needsBuild = true;
        }
    }

    public function buildSection(i : Int, snapshot : Dynamic, endTime : Int) : Dynamic {
        throw "NYI";
    }

    public function unload(renderer : WebGLRenderer) {
        throw "NYI";
    }

    public function fromMap(data : Dynamic) {
        x = data.x;
        z = data.z;

        var dSections : Array<Dynamic> = data.sections;
        for (i in 0 ... 16) {
            var dSection : Dynamic = dSections[i];
            if (dSection != null) {
                var section : ChunkSection = sections[i] = new ChunkSection(
                    new Uint8Array(dSection.buffer)
                );
                section.count = dSection.count;
            }
        }
        nextId = data.nextId;
        var _idMap = data.idMap;
        for (key in Reflect.fields(_idMap)) {
            idMap[Std.parseInt(key)] = BlockRegistry.get(Reflect.field(_idMap, key));
        }
        var _blockMap = data.blockMap;
        for (key in Reflect.fields(_blockMap)) {
            blockMap[BlockRegistry.get(key)] = Reflect.field(_blockMap, key);
        }
    }
}

class ChunkSection {
    private static var emptySkySection : Uint8Array;

    public var blocks : Uint16Array;
    public var light : Uint8Array;
    public var sky : Uint8Array;
    public var buffer : Uint8Array;

    public var count : Int = 0;
    public var needsBuild : Bool = false;
    public var needsUpdate : Bool = false;
	public var lastBuildId : Int = -1;
	public var lastObtainedBuild : Int = -1;
	public var transBlocks : Array<TransBlock> = new Array();

    public function new(buffer : Uint8Array) {
        this.buffer = buffer;
        blocks = new Uint16Array(buffer.buffer, 0, 16 * 16 * 16);
        light = new Uint8Array(buffer.buffer, 16 * 16 * 16 * 2, 16 * 16 * 16);
        sky = new Uint8Array(buffer.buffer, 16 * 16 * 16 * 3, 16 * 16 * 16);
    }

    inline public static function create() {
        var section = new ChunkSection(new Uint8Array(16 * 16 * 16 * 4));
        section.sky.set(emptySkySection, 0);
        return section;
    }

    private static function __init__() {
        emptySkySection = new Uint8Array(16 * 16 * 16);
        for (i in 0 ... emptySkySection.length) emptySkySection[i] = 15;
    }
}

class TransBlock {
	
	public var x : Int;
	public var y : Int;
	public var z : Int;
	public var chunk : Chunk;
	
	public function new(b : { x : Int, y : Int, z : Int }, chunk: Chunk ) {
		this.chunk = chunk;
		x = b.x;
		y = b.y;
		z = b.z;
	}
}