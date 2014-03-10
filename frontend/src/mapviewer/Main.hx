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
    private static var req : XMLHttpRequest;
    private static var canvas : CanvasElement;
    public static var renderer : Renderer;
    public static var connection : Connection;
    public static var world : World;
	public static var modelData : Dynamic;
	public static var textureData : Dynamic;

    static function main() {
		TextureLoader.init(function() { ready(null); } );
        req = new XMLHttpRequest();
        req.onload = ready;
        req.open("GET", "block_models/models.json", true);
        req.send();
    }

    static function ready(event : Dynamic) {
        loadedCount++;
        if (loadedCount != 2) {
            return;
        }
		
		blockTextureInfo = TextureLoader.textures;
		
        var mJs = Json.parse(req.responseText);
		modelData = mJs;
		for (k in Reflect.fields(mJs)) {
			var model = new Model();
			model.fromMap(Reflect.field(mJs, k));
			Model.models[k] = model;
		}
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