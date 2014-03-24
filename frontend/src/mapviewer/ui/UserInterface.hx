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
import mapviewer.chat.BaseComponent;
import mapviewer.chat.TextComponent;

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
	
	private var chatBox : Element;
	private var chatLines : Array<Element>;
	
	private function appendElement(text : Element) {
		if (chatBox == null) initChat();
		var line = chatLines.shift();
		chatBox.removeChild(line);
		line.innerHTML = "";
		line.appendChild(text);
		line.appendChild(document.createBRElement());
		chatLines.push(line);
		chatBox.appendChild(line);
	}
	
	public function appendLine(line : BaseComponent) {
		appendElement(toHtml(line));
	}
	
	@:access(mapviewer.chat)
	private function toHtml(line : BaseComponent) : Element {		
		//TODO: Click and hover events
		var element = document.createSpanElement();
		if (Std.is(line, TextComponent)) {
			var txt : TextComponent = cast line;
			element.innerHTML = StringTools.htmlEscape(txt.text);
		} else {
			throw "Unhandle component type";
		}
		if (line._colour != null) element.style.color = line._colour.hexString;
		if (line._bold) element.style.fontWeight = "bold";
		if (line._italic) element.style.fontStyle = "italic";
		if (line.underlined) element.style.textDecoration = "underline";
		if (line.strikethrough) {
			if (element.style.textDecoration != null) {
				element.style.textDecoration += " line-through";
			} else {
				element.style.textDecoration = "line-through";
			}
		}
		if (line.extra != null) {
			for (extra in line.extra) {
				element.appendChild(toHtml(extra));
			}
		}
		return element;
	}
	
	private function initChat() {
		chatBox = document.getElementById("chat-box");
		chatLines = new Array();
		for (i in 0 ... 12) {
			var ele = document.createSpanElement();
			ele.innerHTML = '<br/>';
			chatBox.appendChild(ele);
			chatLines.push(ele);
		}
	}
	
}