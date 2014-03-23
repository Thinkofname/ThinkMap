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
class Logger {

    public static var CAN_LOG : Bool;

    private var name : String;

    public function new(name : String) {
        this.name = name;
    }


    @:extern
    inline public function info(txt : String) {
        if (CAN_LOG) {
			trace('[INFO][$name]: $txt');
			Main.renderer.ui.appendLine('[INFO][$name]: $txt');
		}
    }

    @:extern
    inline public function debug(txt : String) {
        if (CAN_LOG)
            trace('[DEBUG][$name]: $txt');
    }

    @:extern
    inline public function warn(txt : String) {
        if (CAN_LOG)
            trace('[WARN][$name]: $txt');
    }

    @:extern
    inline public function error(txt : String) {
        if (CAN_LOG)
            trace('[ERROR][$name]: $txt');
    }
}