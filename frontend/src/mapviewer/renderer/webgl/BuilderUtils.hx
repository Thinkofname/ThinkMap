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
import mapviewer.block.Block;
import mapviewer.renderer.LightInfo;
import mapviewer.renderer.TextureInfo;

class BuilderUtils {
	
	public static function addFaceTop(builder : BlockBuilder, 
			x : Float, y : Float, z : Float, w : Float, h : Float, 
			r : Int, g : Int, b : Int, 
			topLeft : LightInfo, topRight : LightInfo, bottomLeft : LightInfo,
			bottomRight : LightInfo, texture : TextureInfo, ?scaleTexture = false) {
				
		var tx : Float = 0;
		var ty : Float = 0;
		var tw : Float = 1;
		var th : Float = 1;
		if (!scaleTexture) {
			tx = x % 1;
			ty = z % 1;
			tw = w;
			th = h;
		}
		
		builder
			//
			.position(x, y, z)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x, y, z + h)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x + w, y, z + h)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky)
			//
			.position(x, y, z + h)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky);
	}
	
	public static function addFaceBottom(builder : BlockBuilder, 
			x : Float, y : Float, z : Float, w : Float, h : Float, 
			r : Int, g : Int, b : Int, 
			topLeft : LightInfo, topRight : LightInfo, bottomLeft : LightInfo,
			bottomRight : LightInfo, texture : TextureInfo, ?scaleTexture = false) {
				
		var tx : Float = 0;
		var ty : Float = 0;
		var tw : Float = 1;
		var th : Float = 1;
		if (!scaleTexture) {
			tx = x % 1;
			ty = z % 1;
			tw = w;
			th = h;
		}
		
		builder
			.position(x, y, z)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x, y, z + h)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x, y, z + h)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky)
			//
			.position(x + w, y, z + h)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky);
	}
	
	public static function addFaceLeft(builder : BlockBuilder, 
			x : Float, y : Float, z : Float, w : Float, h : Float, 
			r : Int, g : Int, b : Int, 
			topLeft : LightInfo, topRight : LightInfo, bottomLeft : LightInfo,
			bottomRight : LightInfo, texture : TextureInfo, ?scaleTexture = false) {
				
		var tx : Float = 0;
		var ty : Float = 0;
		var tw : Float = 1;
		var th : Float = 1;
		if (!scaleTexture) {
			tx = z % 1;
			ty = y % 1;
			tw = w;
			th = h;
		}

		builder
			.position(x, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky)
			//
			.position(x, y, z + w)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x, y + h, z + w)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x, y, z + w)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky);
	}
	
	public static function addFaceRight(builder : BlockBuilder, 
			x : Float, y : Float, z : Float, w : Float, h : Float, 
			r : Int, g : Int, b : Int, 
			topLeft : LightInfo, topRight : LightInfo, bottomLeft : LightInfo,
			bottomRight : LightInfo, texture : TextureInfo, ?scaleTexture = false) {
				
		var tx : Float = 0;
		var ty : Float = 0;
		var tw : Float = 1;
		var th : Float = 1;
		if (!scaleTexture) {
			tx = z % 1;
			ty = y % 1;
			tw = w;
			th = h;
		}

		builder
			.position(x, y, z)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x, y, z + w)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x, y + h, z + w)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x, y, z + w)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky);
	}
	
	public static function addFaceFront(builder : BlockBuilder, 
			x : Float, y : Float, z : Float, w : Float, h : Float, 
			r : Int, g : Int, b : Int, 
			topLeft : LightInfo, topRight : LightInfo, bottomLeft : LightInfo,
			bottomRight : LightInfo, texture : TextureInfo, ?scaleTexture = false) {
				
		var tx : Float = 0;
		var ty : Float = 0;
		var tw : Float = 1;
		var th : Float = 1;
		if (!scaleTexture) {
			tx = x % 1;
			ty = y % 1;
			tw = w;
			th = h;
		}

		builder
			.position(x, y, z)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x + w, y + h, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky);
	}
	
	public static function addFaceBack(builder : BlockBuilder, 
			x : Float, y : Float, z : Float, w : Float, h : Float, 
			r : Int, g : Int, b : Int, 
			topLeft : LightInfo, topRight : LightInfo, bottomLeft : LightInfo,
			bottomRight : LightInfo, texture : TextureInfo, ?scaleTexture = false) {
				
		var tx : Float = 0;
		var ty : Float = 0;
		var tw : Float = 1;
		var th : Float = 1;
		if (!scaleTexture) {
			tx = x % 1;
			ty = y % 1;
			tw = w;
			th = h;
		}

		builder
			.position(x, y, z)
			.colour(r, g, b)
			.tex(tx + tw, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomLeft.light, bottomLeft.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x + w, y + h, z)
			.colour(r, g, b)
			.tex(tx, ty)
			.texId(texture.start, texture.end)
			.lighting(topRight.light, topRight.sky)
			//
			.position(x, y + h, z)
			.colour(r, g, b)
			.tex(tx + tw, ty)
			.texId(texture.start, texture.end)
			.lighting(topLeft.light, topLeft.sky)
			//
			.position(x + w, y, z)
			.colour(r, g, b)
			.tex(tx, ty + th)
			.texId(texture.start, texture.end)
			.lighting(bottomRight.light, bottomRight.sky);
	}
}