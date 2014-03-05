package mapviewer.block;

import mapviewer.block.Block.Face;
import mapviewer.collision.Box;
import mapviewer.model.Model;
import mapviewer.renderer.LightInfo;
import mapviewer.renderer.TextureInfo;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.utils.Chainable;
import mapviewer.block.BlockRegistry.BlockRegistrationEntry;
import mapviewer.world.Chunk;

using mapviewer.renderer.webgl.BuilderUtils;

class Block implements Chainable {

    /// The registration entry for this block
    public var regBlock : BlockRegistrationEntry;

    /// Whether the block can actually be rendered
    @:chain public var renderable : Bool = true;
    /// Whether the block needs to be rendereed with other transparent objects
    @:chain public var transparent : Bool = false;
    /// Whether the block is solid
    @:chain public var solid : Bool = true;
    /// Whether the block has collisions
    @:chain public var collidable : Bool = true;
    /// Whether the block gives a shadow (AO)
    @:chain public var shade : Bool = true;
    /// The colour (tint) of the block
    @:chain public var colour : Int = 0xFFFFFF;
    /// Wether to force tinting for the block
    @:chain public var forceColour : Bool = false;
    /// The name of the texture for this block
    @:chain public var texture : String;
    /// Whether the block can render against itself
    @:chain public var allowSelf : Bool = false;
    /// The model the block should use (if any)
    @:chain public var model : mapviewer.model.Model;

    public function new() {
		
    }
	
	/**
	 * Returns whether the block at the coordinates collides with
	 * the box
	 */
	public function collidesWith(box : Box, x : Int, y : Int, z : Int) : Bool {
		if (!collidable) return false;
		return box.checkBox(x, y, z, 1.0, 1.0, 1.0);
	}	

    /**
     * Returns whether this should render its side against the block
     */
	public function shouldRenderAgainst(block : Block) : Bool {
		return !block.solid && (allowSelf || block != this);
	}
	
    /**
     * Renders the block at the coordinates relative to the
     * chunk
     */
	public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {
		if (model != null) {
			model.render(builder, x, y, z, chunk);
			return;
		}
		
		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y + 1, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.TOP);
			var colour = getColour(this, Face.TOP);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x, y + 1, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y + 1, (chunk.z << 4) + z);
			
			builder.addFaceTop(x, y + 1, z, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x    , y + 1, z - 1, x + 2, y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x    , y + 1, z    , x + 2, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y + 1, z    , x + 1, y + 2, z + 2, light, sky),
				texture);
		}
		
		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y - 1, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.BOTTOM);
			var colour = getColour(this, Face.BOTTOM);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x, y - 1, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y - 1, (chunk.z << 4) + z);
			
			builder.addFaceBottom(x, y, z, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x    , y - 1, z - 1, x + 2, y    , z + 1, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y    , z + 1, light, sky),
				blockLightingRegion(chunk, this, x    , y - 1, z    , x + 2, y    , z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z    , x + 1, y    , z + 2, light, sky),
				texture);
		}
		
		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x + 1, y, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.LEFT);
			var colour = getColour(this, Face.LEFT);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x + 1, y, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x + 1, y, (chunk.z << 4) + z);
			
			builder.addFaceLeft(x + 1, y, z, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x + 1, y    , z - 1, x + 2, y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x + 1, y    , z    , x + 2, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1, light, sky),
				blockLightingRegion(chunk, this, x + 1, y - 1, z    , x + 2, y + 1, z + 2, light, sky),
				texture);
		}
		
		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x - 1, y, (chunk.z << 4) + z))) {
			var texture = getTexture(Face.RIGHT);
			var colour = getColour(this, Face.RIGHT);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		
			
			var light = chunk.world.getLight((chunk.x << 4) + x - 1, y, (chunk.z << 4) + z);
			var sky = chunk.world.getSky((chunk.x << 4) + x - 1, y, (chunk.z << 4) + z);
			
			builder.addFaceRight(x, y, z, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x - 1, y    , z    , x    , y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y    , z - 1, x    , y + 2, z + 1, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z    , x    , y + 1, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x    , y + 1, z + 1, light, sky),
				texture);
		}

		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y, (chunk.z << 4) + z + 1))) {
			var texture = getTexture(Face.FRONT);
			var colour = getColour(this, Face.FRONT);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		

			var light = chunk.world.getLight((chunk.x << 4) + x, y, (chunk.z << 4) + z + 1);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y, (chunk.z << 4) + z + 1);

			builder.addFaceFront(x, y, z + 1, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x    , y    , z + 1, x + 2, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y    , z + 1, x + 1, y + 2, z + 2, light, sky),
				blockLightingRegion(chunk, this, x    , y - 1, z + 1, x + 2, y + 1, z + 2, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2, light, sky),
				texture);
		}

		if (shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y, (chunk.z << 4) + z - 1))) {
			var texture = getTexture(Face.BACK);
			var colour = getColour(this, Face.BACK);
			var r = (colour >> 16) & 0xFF;
			var g = (colour >> 8) & 0xFF;
			var b = colour & 0xFF;		

			var light = chunk.world.getLight((chunk.x << 4) + x, y, (chunk.z << 4) + z - 1);
			var sky = chunk.world.getSky((chunk.x << 4) + x, y, (chunk.z << 4) + z - 1);

			builder.addFaceBack(x, y, z, 1, 1, r, g, b,
				blockLightingRegion(chunk, this, x - 1, y    , z - 1, x + 1, y + 2, z, light, sky),
				blockLightingRegion(chunk, this, x    , y    , z - 1, x + 2, y + 2, z, light, sky),
				blockLightingRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y + 1, z, light, sky),
				blockLightingRegion(chunk, this, x    , y - 1, z - 1, x + 2, y + 1, z, light, sky),
				texture);
		}
	}
	
	/**
	 * Returns the texture for the face
	 */
	public function getTexture(face : Face) : TextureInfo {
		return Main.blockTextureInfo[texture];
	}
	
	@:chain public var getColour : Block -> Block.Face -> Int = function(self : Block, face : Block.Face) : Int {
		return self.forceColour ? self.colour : 0xFFFFFF;
	};

    public function toString() : String {
        return regBlock.toString();
    }
	
	public static function blockLightingRegion(chunk : Chunk, self : Block, x1 : Int, y1 : Int, z1 : Int, 
		x2 : Int, y2 : Int, z2 : Int, ?faceLight : Int = 0, ?faceSkyLight : Int = 0) {
		var light = 0;
		var sky = 0;
		var count = 0;
		var valSolid : Int = Std.int((15 * Math.pow(0.85, faceLight + 1)));
		var valSkySolid : Int = Std.int((15 * Math.pow(0.85, faceSkyLight + 1)));
		for (y in y1 ... y2) {
			if (y < 0 || y > 255) continue;
			for (x in x1 ... x2) {
				for (z in z1 ... z2) {
					var px = (chunk.x << 4) + x;
					var pz = (chunk.z << 4) + z;
					if (!chunk.world.isLoaded(px, y, pz)) continue;
					count++;
					var valSky = 6;
					var val = 6;
					
					var block = chunk.world.getBlock(px, y, pz);
					if (block.shade && (block.solid || block == self)) {
						val -= valSolid;
						valSky -= valSkySolid;
					} else {
						valSky = chunk.world.getSky(px, y, pz);
						val = chunk.world.getLight(px, y, pz);
					}
					light += val;
					sky += valSky;
				}
			}
		}
		if (count == 0) return new LightInfo(15, 15);
		return new LightInfo(Std.int(light / count), Std.int(sky / count));
	}
}

class Face {
	
	public static var TOP    = new Face(0, "top");
	public static var BOTTOM = new Face(1, "bottom");
	public static var RIGHT  = new Face(2, "right");
	public static var LEFT   = new Face(3, "left");
	public static var BACK   = new Face(4, "back");
	public static var FRONT  = new Face(5, "front");
	
	public var id : Int;
	public var name : String;
	
	private function new(id : Int, name : String) {
		this.id = id;
		this.name = name;
		if (nameMap == null) nameMap = new Map<String, Face>();
		nameMap[name] = this;
	}
	
	private static var nameMap;
	
	public static function fromName(name : String) : Face {
		return nameMap[name];
	}
}