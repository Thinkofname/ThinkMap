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
import js.Browser.document;
import js.html.CanvasElement;
import js.html.CanvasRenderingContext2D;
import js.html.Element;
import js.html.Event;
import js.html.MouseEvent;

class UserInterface  {
	
	private var canvas : CanvasElement;
	private var ctx : CanvasRenderingContext2D;

	public function new() {
		
	}
	
	private var posX : Element;
	private var posY : Element;
	private var posZ : Element;
	public function updatePosition(x : Int, y : Int, z : Int) {
		if (posX == null) {
			posX = document.getElementById("position-x");
			posY = document.getElementById("position-y");
			posZ = document.getElementById("position-z");
		}
		posX.innerHTML = '$x';
		if (x < 0) posX.style.color = Colour.RED.hexString;
		else  posX.style.color = Colour.GREEN.hexString;
		
		posY.innerHTML = '$y';
		if (y < 0) posY.style.color = Colour.RED.hexString;
		else  posY.style.color = Colour.GREEN.hexString;
		
		posZ.innerHTML = '$z';
		if (z < 0) posZ.style.color = Colour.RED.hexString;
		else  posZ.style.color = Colour.GREEN.hexString;
	}
	
	private var fpsFPS : Element;
	private var fpsMS : Element;
	public function updateFPS(fps : Int, jsMS : Int, ms : Int) {
		if (fpsFPS == null) {
			fpsFPS = document.getElementById("fps");
			fpsMS = document.getElementById("ms");
		}
		
		fpsFPS.innerHTML = '$fps';
		fpsFPS.style.color = (fps >= 55 ? Colour.GREEN : (fps >= 30 ? Colour.YELLOW : Colour.RED)).hexString;
		
		fpsMS.innerHTML = '$jsMS / $ms';
	}
	
}