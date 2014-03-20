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
import haxe.ds.IntMap.IntMap;
import js.html.webgl.RenderingContext;
import js.html.webgl.Shader;
import js.html.webgl.UniformLocation;
import mapviewer.renderer.webgl.glmatrix.Mat4;
import mapviewer.renderer.webgl.WebGLRenderer.GL;

class ChunkShader extends TProgram {
	
	// Uniforms
	private var pMatrix : UniformLocation;
	private var uMatrix : UniformLocation;
	private var offset : UniformLocation;
	private var frame : UniformLocation;
	private var scale : UniformLocation;
	private var blockTexture : UniformLocation;
	// TODO: Remember why this is called 'disAlpha'
	private var disAlpha : UniformLocation;
	// Attribs
	public var position : Int;
	public var colour : Int;
	public var textureId : Int;
	public var texturePosition : Int;
	public var lighting : Int;

	public function new(gl : RenderingContext, ?vert : String, ?frag : String) {
		if (vert == null) vert = chunkVertexShaderSource;
		if (frag == null) frag = chunkFragmentShaderSource;
		super(gl, vert, frag);
		initAttribs();
	}	
	
	private function initAttribs() {		
		// Uniforms
		pMatrix = getUniform("pMatrix");
		uMatrix = getUniform("uMatrix");
		offset = getUniform("offset");
		frame = getUniform("frame");
		scale = getUniform("scale");
		blockTexture = getUniform("texture");
		// Attribs
		position = getAttrib("position");
		colour = getAttrib("colour");
		textureId = getAttrib("textureId");
		texturePosition = getAttrib("texturePos");
		lighting = getAttrib("lighting");
	}
	
	inline public function setPerspectiveMatrix(mat4 : Mat4) {
		gl.uniformMatrix4fv(pMatrix, false, mat4);
	}
	
	inline public function setUMatrix(mat4 : Mat4) {
		gl.uniformMatrix4fv(uMatrix, false, mat4);
	}
	
	inline public function setOffset(x : Int, z : Int) {
		gl.uniform2f(offset, x, z);
	}
	
	inline public function setFrame(i : Int) {
		gl.uniform1f(frame, i);
	}
	
	inline public function setScale(i : Float) {
		gl.uniform1f(scale, i);
	}
	
	inline public function setBlockTexture(i : Int) {
		gl.uniform1i(blockTexture, i);
	}	
		
	private static var chunkVertexShaderSource : String = "
precision mediump float;

attribute vec3 position;
attribute vec4 colour;
attribute vec2 texturePos;
attribute vec2 textureId;
attribute vec2 lighting;

uniform mat4 pMatrix;
uniform mat4 uMatrix;
uniform vec2 offset;
uniform float time;

varying vec4 vColour;
varying vec2 vTextureId;
varying vec2 vTexturePos;
varying vec2 vLighting;

void main(void) {
    vec3 pos = position;
    gl_Position = pMatrix * uMatrix * vec4((pos / 256.0) - 1.0 + vec3(offset.x * 16.0, 0.0, offset.y * 16.0), 1.0);
    vColour = colour;
    vTextureId = textureId;
    vTexturePos = texturePos / 256.0;
	vTexturePos.x = vTexturePos.x;
	vTexturePos.y = vTexturePos.y;
    vLighting = lighting;
}	
	";
	
	private static var chunkFragmentShaderSource : String = "
precision mediump float;

uniform sampler2D texture;
uniform float frame;
uniform float scale;

varying vec4 vColour;
varying vec2 vTextureId;
varying vec2 vTexturePos;
varying vec2 vLighting;

void main(void) {
    float id = floor(vTextureId.x + 0.5);
    id = id + floor(mod(frame, floor((vTextureId.y - vTextureId.x) + 0.5) + 1.0));
    vec2 pos = clamp(fract(vTexturePos), 0.0001, 0.9999) * 0.03125;
    pos.x += floor(mod(id, 32.0)) * 0.03125;
    pos.y += floor(id / 32.0) * 0.03125;
    vec4 colour = texture2D(texture, pos) * vColour;

    float light = max(vLighting.x, vLighting.y * scale);
    float val = pow(0.9, 16.0 - light) * 2.0;
    colour.rgb *= clamp(pow(val, 1.5) / 2.0, 0.0, 1.0);
	#ifndef colourPass 
	#ifndef weightPass 
		if (colour.a < 0.5) discard;
		gl_FragColor = colour;
	#endif
	#endif
	
	#ifdef colourPass 
		// Colour pass
		colour.rgb *= colour.a;
		float z = (gl_FragCoord.z / gl_FragCoord.w);
		float weight = colour.a * clamp(pow(abs(z + 5.0), -4.0), 0.2, 0.8);
		gl_FragColor = colour * weight;
	#endif
	#ifdef weightPass
		// Weight pass
		gl_FragColor = vec4(colour.a);
	#endif
}
	";
}
