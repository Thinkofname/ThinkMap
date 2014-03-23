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
package mapviewer.chat;
import mapviewer.chat.ClickEvent.ClickAction;

class ClickEvent {
	
	public var action(default, null) : ClickAction;
	public var value(default, null) : String;

	public function new(action : ClickAction, value : String) {
		this.action = action;
		this.value = value;
	}	
}

class ClickAction {
	
	public static var OPEN_URL(default, null) = new ClickAction("open_url");
	public static var OPEN_FILE(default, null) = new ClickAction("open_file");
	public static var RUN_COMMAND(default, null) = new ClickAction("run_command");
	public static var SUGGEST_COMMAND(default, null) = new ClickAction("suggest_command");
	
	public var name : String;
	private function new(name : String) {
		this.name = name;
	}
}