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
import mapviewer.chat.HoverEvent.HoverAction;

class HoverEvent {
	
	public var action(default, null) : HoverAction;
	public var value(default, null) : BaseComponent;

	public function new(action : HoverAction, value : BaseComponent) {
		this.action = action;
		this.value = value;
	}	
}

class HoverAction {
	
	public static var SHOW_TEXT(default, null) = new HoverAction("show_text");
	public static var SHOW_ACHIEVEMENT(default, null) = new HoverAction("show_achievement");
	public static var SHOW_ITEM(default, null) = new HoverAction("show_item");
	
	public var name : String;
	private function new(name : String) {
		this.name = name;
	}
}