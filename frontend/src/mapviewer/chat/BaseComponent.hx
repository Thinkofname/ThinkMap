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
import mapviewer.ui.Colour;

class BaseComponent {
	
	private var parent : BaseComponent;
	
	/**
	 * The colour of this component and any child components (unless overridden)
	 */
	public var colour(get, set) : Colour;
	private var _colour : Colour;
	function get_colour() : Colour return (_colour == null) ? (parent == null ? Colour.WHITE : parent.colour) : _colour;
	function set_colour(c : Colour) : Colour return _colour = c;
	
	/**
	 * Whether this component and any child components (unless overriden) is bold
	 */
	public var bold(get, set) : Bool;
	private var _bold : Null<Bool>;
	function get_bold() : Bool return (_bold == null) ? (parent == null ? false : parent.bold) : _bold;
	function set_bold(b : Bool) : Bool return _bold = b;
	
	/**
	 * Whether this component and any child components (unless overriden) is italic
	 */
	public var italic(get, set) : Bool;
	private var _italic : Null<Bool>;
	function get_italic() : Bool return (_italic == null) ? (parent == null ? false : parent.italic) : _italic;
	function set_italic(b : Bool) : Bool return _italic = b;
	
	/**
	 * Whether this component and any child components (unless overriden) is underlined
	 */
	public var underlined(get, set) : Bool;
	private var _underlined : Null<Bool>;
	function get_underlined() : Bool return (_underlined == null) ? (parent == null ? false : parent.underlined) : _underlined;
	function set_underlined(b : Bool) : Bool return _underlined = b;
	
	/**
	 * Whether this component and any child components (unless overriden) is strikethrough
	 */
	public var strikethrough(get, set) : Bool;
	private var _strikethrough : Null<Bool>;
	function get_strikethrough() : Bool return (_strikethrough == null) ? (parent == null ? false : parent.strikethrough) : _strikethrough;
	function set_strikethrough(b : Bool) : Bool return _strikethrough = b;
	
	/**
	 * Whether this component and any child components (unless overriden) is obfuscated
	 */
	public var obfuscated(get, set) : Bool;
	private var _obfuscated : Null<Bool>;
	function get_obfuscated() : Bool return (_obfuscated == null) ? (parent == null ? false : parent.obfuscated) : _strikethrough;
	function set_obfuscated(b : Bool) : Bool return _obfuscated = b;
	
	private var extra : Array<BaseComponent>;
	
	public function addComponent(c : BaseComponent) {
		if (extra == null) {
			extra = new Array();
		}
		c.parent = this;
		extra.push(c);
	}
	
	public var clickEvent : ClickEvent;
	public var hoverEvent : HoverEvent;

	private function new() {
		
	}	
}