package mapviewer;

import js.Browser;
import js.html.ImageElement;


class Main {

    public static var blockTexturesRaw : Array<ImageElement> = new Array<ImageElement>();
    private static var loadedCount : Int = 0;


    static function main() {
        var img = Browser.document.createImageElement();
        img.onload = function(event : Dynamic) {
            ready();
        }
        img.src = "block_images/blocks_0.png";
    }

    static function ready() {
        loadedCount++;
        if (loadedCount != 3) {
            return;
        }
    }
}