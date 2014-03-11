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