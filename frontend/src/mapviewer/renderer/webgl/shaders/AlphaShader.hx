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
package mapviewer.renderer.webgl.shaders;
import js.html.webgl.RenderingContext;
import js.html.webgl.UniformLocation;

class AlphaShader extends TProgram {
	
	public var position : Int;
	
	private var screen : UniformLocation;
	private var scale : UniformLocation;
	private var cb : UniformLocation;
	private var wb : UniformLocation;
	private var nb : UniformLocation;

	public function new(gl : RenderingContext) {
		super(gl, "
precision mediump float;

attribute vec2 position;

void main(void) {
	gl_Position = vec4(position, -0.9, 1.0);
}
		", "
precision mediump float;

uniform vec2 screen;
uniform vec2 scale;
uniform sampler2D cb;
uniform sampler2D wb;
uniform sampler2D nb;

void main(void) {
	vec2 rp = (gl_FragCoord.xy / screen) * scale;
	vec4 accum = texture2D(cb, rp);
	float reveal = texture2D(wb, rp).r;
	vec4 colour = texture2D(nb, rp);
	vec4 blend = vec4(accum.rgb / clamp(accum.a, 1e-4, 5e4), reveal);
	gl_FragColor = vec4(colour.rgb * blend.a + blend.rgb * (1.0 - blend.a), 1.0);
}		
		");
		
		position = getAttrib("position");
		screen = getUniform("screen");
		scale = getUniform("scale");
		cb = getUniform("cb");
		wb = getUniform("wb");
		nb = getUniform("nb");
	}
	
	inline public function setScreenSize(x : Int, y : Int) {
		gl.uniform2f(screen, x, y);
	}	
	
	inline public function setScale(x : Float, y : Float) {
		gl.uniform2f(scale, x, y);
	}
	
	inline public function setBuffers(x : Int, y : Int, z : Int) {
		gl.uniform1i(cb, x);
		gl.uniform1i(wb, y);
		gl.uniform1i(nb, z);
	}
}