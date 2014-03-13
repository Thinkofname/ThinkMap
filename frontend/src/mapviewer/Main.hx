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
package mapviewer;

import mapviewer.assets.TextureLoader;
import mapviewer.logging.Logger;
import mapviewer.model.Model;
import mapviewer.renderer.webgl.WebGLRenderer;
import mapviewer.renderer.webgl.WebGLWorld;
import mapviewer.world.World;
import mapviewer.network.Connection;
import mapviewer.renderer.Renderer;
import js.html.CanvasElement;
import mapviewer.renderer.TextureInfo;
import Reflect;
import haxe.Json;
import mapviewer.block.BlockRegistry;
import js.html.XMLHttpRequest;
import js.Browser;
import js.html.ImageElement;

class Main {

    public static var blockTexturesRaw : Array<ImageElement> = new Array();
    public static var blockTextureInfo : Map<String, TextureInfo> = new Map();
    private static var loadedCount : Int = 0;
    private static var canvas : CanvasElement;
    public static var renderer : Renderer;
    public static var connection : Connection;
    public static var world : World;

    static function main() {
		TextureLoader.init(ready);
    }

    static function ready() {		
		blockTextureInfo = TextureLoader.textures;
        BlockRegistry.init();

        canvas = cast Browser.document.getElementById("main");
        canvas.width = Browser.window.innerWidth;
        canvas.height = Browser.window.innerHeight;
        Browser.window.onresize = function(e) {
            canvas.width = Browser.window.innerWidth;
            canvas.height = Browser.window.innerHeight;
			renderer.resize(canvas.width, canvas.height);
        };

        connection = new Connection('${Config.hostname}:${Config.port}');
		
		world = new WebGLWorld();
		renderer = new WebGLRenderer(canvas);
        Browser.window.requestAnimationFrame(draw);
    }

    private static function draw(unused : Float) : Bool {
		renderer.draw();
        Browser.window.requestAnimationFrame(draw);
        return true;
    }
	
	static function __init__() {
		Logger.CAN_LOG = true;
	}
}