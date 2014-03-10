package mapviewer.assets;
import js.Browser;
import mapviewer.Main;

@:build(mapviewer.assets.TextureLoaderMacro.build())
class TextureLoader {
	
	public static function init(cb : Void -> Void) {
        var img = Browser.document.createImageElement();
        img.onload = function(e) { cb(); };
        img.src = "block_images/blocks_0.png";
		Main.blockTexturesRaw.push(img);
	}
}