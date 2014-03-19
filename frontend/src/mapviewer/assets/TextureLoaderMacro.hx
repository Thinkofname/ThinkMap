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
package mapviewer.assets;

import format.png.Data;
import format.png.Reader;
import format.png.Tools;
import format.png.Writer;
import haxe.io.Bytes;
import haxe.Json;
import haxe.macro.Expr.Field;
import haxe.macro.Expr.FieldType;
import haxe.macro.Expr;
import haxe.macro.Expr.Access;
import haxe.macro.Context;
import haxe.macro.TypeTools;
import mapviewer.renderer.TextureInfo;
import sys.FileSystem;
import sys.io.File;

using StringTools;

class TextureLoaderMacro {
	
	private static var size = 512;
	
    public static function build() : Array<Field> {
		Context.registerModuleDependency(Context.getLocalClass().get().module, "assets/block_images/");
        var fields : Array<Field> = Context.getBuildFields();
		var currentImage : Bytes = Bytes.alloc(size * size * 4);
		
		var tstring = TPType(TPath( { pack: [], name: "String", params: [], sub: null } ));
		var tinfopath = { pack: ["mapviewer", "renderer"], name: "TextureInfo", params: [], sub: null };
		var tinfo = TPType(TPath(tinfopath));
		
        var tmap = TPath({pack: [], name: "Map", params: [
			tstring, tinfo
		], sub: null } );
		
		var vals = [];
		var pos = 0;
		#if !display
		for (file in FileSystem.readDirectory("assets/block_images/")) {
			if (!file.endsWith(".png")) {
				continue;
			}
			var iFile = File.read("assets/block_images/" + file);
			var imgData : Data = new Reader(iFile).read();
			iFile.close();
			var header = Tools.getHeader(imgData);
			var img;
			try {
				img = Tools.extract32(imgData);
			} catch (e : Dynamic) {
				Context.warning('Failed to parse $file', Context.currentPos());
				continue;
			}
			Tools.reverseBytes(img);
			
			if (header.width != 16 || header.height != 16) {
				var meta = Json.parse(File.getContent("assets/block_images/" + file + ".mcmeta"));
				var frames : Array<Int> = new Array();
				var frs : Array<Dynamic> = meta.animation.frames;
				if (frs == null) {
					for (i in 0 ... Std.int(header.height / header.width)) {
						frames.push(i);
					}
				} else {
					for (f in frs) { 
						if (Std.is(f, Int)) {
							frames.push(f);
						} else {
							for (i in 0 ... f.time) {
								frames.push(f.index);
							}
						}
					}
				}
				var frameTime : Int = meta.animation.frametime != null ?
					meta.animation.frametime : 1;
				var start = pos;
				for (frame in frames) {
					for (na in 0 ... frameTime) {
						var ox = (pos % 32) * 16;
						var oy = (Std.int(pos / 32)) * 16;
						for (y in 0 ... 16) {
							for (x in 0 ... 16) {
								var idx = (ox + x + (oy + y) * size) * 4;
								var i = (x + (y + header.width * frame) * 16) * 4;
								currentImage.set(idx, img.get(i));
								currentImage.set(idx + 1, img.get(i + 1));
								currentImage.set(idx + 2, img.get(i + 2));
								currentImage.set(idx + 3, img.get(i + 3));
							}
						}	
						pos++;	
					}
				}
				var name = file.substring(0, file.length - 4);
				vals.push({
					expr: EBinop(OpArrow, 
						{ expr: EConst(CString(name)), pos: Context.currentPos() }, 
						{ expr: ENew(tinfopath, [
							{ expr: EConst(CInt(Std.string(start))), pos: Context.currentPos() },
							{ expr: EConst(CInt(Std.string(pos - 1))), pos: Context.currentPos() }
						]), pos: Context.currentPos() }),
					pos: Context.currentPos()
				});				
				continue;
			}
			var ox = (pos % 32) * 16;
			var oy = (Std.int(pos / 32)) * 16;
			
			for (y in 0 ... 16) {
				for (x in 0 ... 16) {
					var idx = (ox + x + (oy + y) * size) * 4;
					var i = (x + y * 16) * 4;
					currentImage.set(idx, img.get(i));
					currentImage.set(idx + 1, img.get(i + 1));
					currentImage.set(idx + 2, img.get(i + 2));
					currentImage.set(idx + 3, img.get(i + 3));
				}
			}		
			var name = file.substring(0, file.length - 4);
			vals.push({
				expr: EBinop(OpArrow, 
					{ expr: EConst(CString(name)), pos: Context.currentPos() }, 
					{ expr: ENew(tinfopath, [
						{ expr: EConst(CInt(Std.string(pos))), pos: Context.currentPos() },
						{ expr: EConst(CInt(Std.string(pos))), pos: Context.currentPos() }
					]), pos: Context.currentPos() }),
				pos: Context.currentPos()
			});
			pos++;
		}
		FileSystem.createDirectory("build");
		FileSystem.createDirectory("build/block_images");
		var out = File.write("build/block_images/blocks_0.png");
		new Writer(out).write(Tools.build32ARGB(size, size, currentImage));
		out.close();
		#end
		
		fields.push( {
			name: "textures",
			access: [Access.APublic, Access.AStatic],
			kind: FieldType.FVar(tmap, {
				expr: EArrayDecl(vals),
				pos: Context.currentPos()
			}),
			pos: Context.currentPos()
		});
		return fields;
	}
}