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
package mapviewer.ui;
import js.Browser;
import js.html.CanvasElement;
import js.html.CanvasRenderingContext2D;
import js.html.Event;
import js.html.MouseEvent;

class UserInterface  {
	
	private var canvas : CanvasElement;
	private var ctx : CanvasRenderingContext2D;

	public function new() {
		canvas = Browser.document.createCanvasElement();
		Browser.document.body.appendChild(canvas);
		canvas.style.position = "absolute";
		canvas.style.left = "0";
		canvas.style.top = "0";
		canvas.classList.add("no-events");
		ctx = canvas.getContext2d();
		resize();
	}
	
	public function clear() {
		ctx.clearRect(0, 0, canvas.width, canvas.height);
	}
	
	public function drawText(txt : String, colour : Colour, x : Int, y : Int) : Int {
		ctx.font = "16px Minecraft";
		ctx.textAlign = "left";
		ctx.textBaseline = "top";
		ctx.fillStyle = Colour.BLACK.hexString;
		ctx.fillText(txt, x + 2, y + 2);
		ctx.fillStyle = colour.hexString;
		ctx.fillText(txt, x, y);
		return Std.int(ctx.measureText(txt).width);
	}
	
	public function stringLength(txt : String) : Int {
		ctx.font = "16px Minecraft";
		ctx.textAlign = "left";
		ctx.textBaseline = "top";
		return Std.int(ctx.measureText(txt).width);
	}
	
	public function resize() {
		canvas.width = Browser.window.innerWidth;
		canvas.height = Browser.window.innerHeight;
	}
	
}