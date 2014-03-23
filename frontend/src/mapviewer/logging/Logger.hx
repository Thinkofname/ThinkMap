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
package mapviewer.logging;

import js.Browser;
import mapviewer.chat.TextComponent;
import mapviewer.ui.Colour;
class Logger {

    public static var CAN_LOG : Bool;

    private var name : String;

    public function new(name : String) {
        this.name = name;
    }

	// Methods are force inlined so that the correct line number 
	// shows up in the console

    @:extern
    inline public function info(txt : String) {
		#if debug
        if (CAN_LOG) {
			trace('[INFO][$name]: $txt');
			log("INFO", txt, Colour.AQUA);
		}
		#end
    }

    @:extern
    inline public function debug(txt : String) {
		#if debug
        if (CAN_LOG) {
            trace('[DEBUG][$name]: $txt');
			log("DEBUG", txt, Colour.YELLOW);
		}
		#end
    }

    @:extern
    inline public function warn(txt : String) {
		#if debug
        if (CAN_LOG) {
            trace('[WARN][$name]: $txt');
			log("WARN", txt, Colour.GOLD);
		}
		#end
    }

    @:extern
    inline public function error(txt : String) {
		#if debug
        if (CAN_LOG) {
            trace('[ERROR][$name]: $txt');
			log("ERROR", txt, Colour.RED);
		}
		#end
    }
	
	#if debug
	private function log(type : String, txt : String, colour : Colour) {
		var comp = new TextComponent('[$type]');
		comp.colour = Colour.AQUA;
		var label = new TextComponent('[$name]');
		label.bold = true;
		comp.addComponent(label);
		var msg = new TextComponent(': $txt');
		msg.colour = Colour.WHITE;
		comp.addComponent(msg);
		Main.renderer.ui.appendLine(comp);
	}
	#end
}