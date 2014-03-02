package mapviewer.renderer.webgl;
import js.html.CanvasElement;
import js.html.ImageElement;
import js.html.webgl.Program;
import js.html.webgl.RenderingContext;
import js.html.webgl.Shader;
import js.html.webgl.Texture;
import js.html.webgl.UniformLocation;
import mapviewer.renderer.Renderer;
import mapviewer.renderer.webgl.glmatrix.Mat4;
import mapviewer.renderer.webgl.WebGLRenderer.Camera;

class WebGLRenderer implements Renderer {
	
	// Rendering
	public var gl : RenderingContext;
	public var canvas : CanvasElement;

	// Shaders
	public var mainProgram : Program;
	public var pMatrixLocation : UniformLocation;
	public var uMatrixLocation : UniformLocation;
	public var offsetLocation : UniformLocation;
	public var blockTextureLocation : UniformLocation;
	public var frameLocation : UniformLocation;
	public var disAlphaLocation : UniformLocation;
	public var positionLocation : Int;
	public var colourLocation : Int;
	public var textureIdLocation : Int;
	public var texturePosLocation : Int;
	public var lightingLocation : Int;
	
	// Matrices
	public var pMatrix : Mat4;
	public var uMatrix : Mat4;
	
	public var blockTextures : Array<Texture>;
	
	// Controls
	public var movingForward : Bool = false;
	public var movingBackwards : Bool = false;
	public var camera : Camera;
	public var vSpeed = MIN_VSPEED;
	inline public static var MIN_VSPEED : Float = -0.2;
	public var onGround : Bool = false;
	public var offGroundFor : Int = 0;
	public var cx : Int = 0;
	public var cz : Int = 0;
	public var firstPerson : Bool = true;
	
	public function new(canvas : CanvasElement) {
		this.canvas = canvas;
		pMatrix = Mat4.create();
		uMatrix = Mat4.create();
		uMatrix.identity();
		blockTextures = new Array<Texture>();
		camera = new Camera();
		
		// Flags are set for performance
		gl = canvas.getContextWebGL( { alpha: false, premultipliedAlpha: false, 
		antialias: false } );
		
		if (gl == null) throw "WebGL not supported";
		
		resize(0, 0);
		
		// Convert images to textures
		for (img in Main.blockTexturesRaw) {
			blockTextures.push(loadTexture(img));
		}
		
		var chunkVertexShader = createShader(chunkVertexShaderSource, RenderingContext.VERTEX_SHADER);
		var chunkFragmentShader = createShader(chunkFragmentShaderSource, RenderingContext.FRAGMENT_SHADER);
		mainProgram = createProgram(chunkVertexShader, chunkFragmentShader);
		
		// Setup uniforms and attributes
		pMatrixLocation = gl.getUniformLocation(mainProgram, "pMatrix");
		uMatrixLocation = gl.getUniformLocation(mainProgram, "uMatrix");
		offsetLocation = gl.getUniformLocation(mainProgram, "offset");
		frameLocation = gl.getUniformLocation(mainProgram, "frame");
		blockTextureLocation = gl.getUniformLocation(mainProgram, "texture");
		disAlphaLocation = gl.getUniformLocation(mainProgram, "disAlpha");
		positionLocation = gl.getAttribLocation(mainProgram, "position");
		colourLocation = gl.getAttribLocation(mainProgram, "colour");
		textureIdLocation = gl.getAttribLocation(mainProgram, "textureId");
		texturePosLocation = gl.getAttribLocation(mainProgram, "texturePos");
		lightingLocation = gl.getAttribLocation(mainProgram, "lighting");
		gl.enableVertexAttribArray(positionLocation);
		gl.enableVertexAttribArray(colourLocation);
		gl.enableVertexAttribArray(textureIdLocation);
		gl.enableVertexAttribArray(texturePosLocation);
		gl.enableVertexAttribArray(lightingLocation);
		
		gl.enable(RenderingContext.DEPTH_TEST);
		gl.enable(RenderingContext.CULL_FACE);
		gl.cullFace(RenderingContext.BACK);
		gl.frontFace(RenderingContext.CW);
		
		gl.blendFunc(RenderingContext.SRC_ALPHA, RenderingContext.ONE_MINUS_SRC_ALPHA);
		
		//TODO: Controls
	}
	
	inline public static var viewDistance : Int = 4;
	
    public function draw() : Void {
		gl.viewport(0, 0, canvas.width, canvas.height);
		var skyPosition = getScale();
		gl.clearColor(getScaledNumber(122 / 255, 0, skyPosition),
					  getScaledNumber(165 / 255, 0, skyPosition),
					  getScaledNumber(247 / 255, 0, skyPosition),
					  1);
		gl.colorMask(true, true, true, false);
		gl.clear(RenderingContext.COLOR_BUFFER_BIT | RenderingContext.DEPTH_BUFFER_BIT);
	}
    public function resize(width : Int, height : Int) : Void {}
    public function connected() : Void {}
    public function shouldLoad(x : Int, z : Int) : Void {}
    public function moveTo(x : Int, y : Int, z : Int) : Void { }	
	
	private function getScale() : Float {
		var scale = (Main.world.currentTime - 6000) / 12000;
		if (scale > 1.0) {
			scale = 2.0 - scale;
		} else if (scale < 0) {
			scale = -scale;
		}
		return scale;
	}

	private function getScaledNumber(x : Float, y : Float, scale : Float) : Float {
		return x + (y - x) * scale;
	}
	
	public function loadTexture(imageElement : ImageElement) : Texture {
		var tex = gl.createTexture();
		gl.bindTexture(RenderingContext.TEXTURE_2D, tex);
		gl.pixelStorei(RenderingContext.UNPACK_FLIP_Y_WEBGL, 0);
		gl.pixelStorei(RenderingContext.UNPACK_PREMULTIPLY_ALPHA_WEBGL, 0);
		gl.texImage2D(RenderingContext.TEXTURE_2D, 0, 
			RenderingContext.RGBA, RenderingContext.RGBA, 
			RenderingContext.UNSIGNED_BYTE, imageElement);
		gl.texParameteri(RenderingContext.TEXTURE_2D, 
			RenderingContext.TEXTURE_MAG_FILTER, RenderingContext.NEAREST);
		gl.texParameteri(RenderingContext.TEXTURE_2D, 
			RenderingContext.TEXTURE_MIN_FILTER, RenderingContext.NEAREST);
		gl.bindTexture(RenderingContext.TEXTURE_2D, null);
		return tex;
	}
	
	private static var chunkVertexShaderSource : String = "
attribute vec3 position;
attribute vec3 colour;
attribute vec2 texturePos;
attribute vec2 textureId;
attribute vec2 lighting;

uniform mat4 pMatrix;
uniform mat4 uMatrix;
uniform vec2 offset;

varying vec3 vColour;
varying vec2 vTextureId;
varying vec2 vTexturePos;
varying vec2 vLighting;

void main(void) {
    vec3 pos = position;
    gl_Position = pMatrix * uMatrix * vec4((pos / 256.0) + vec3(offset.x * 16.0, 0.0, offset.y * 16.0), 1.0);
    vColour = colour;
    vTextureId = textureId;
    vTexturePos = texturePos / 256.0;
    if (vTexturePos.x == 0.0) {
        vTexturePos.x = 0.0001;
    } else if (vTexturePos.x == 1.0) {
        vTexturePos.x = 0.9999;
    }
    if (vTexturePos.y == 0.0) {
        vTexturePos.y = 0.0001;
    } else if (vTexturePos.y == 1.0) {
        vTexturePos.y = 0.9999;
    }
    vLighting = lighting;
}	
	";
	
	private static var chunkFragmentShaderSource : String = "
precision mediump float;

uniform sampler2D texture;
uniform float frame;
uniform int disAlpha;

varying vec3 vColour;
varying vec2 vTextureId;
varying vec2 vTexturePos;
varying vec2 vLighting;

void main(void) {
    float id = floor(vTextureId.x + 0.5);
    if (abs(floor((vTextureId.y - vTextureId.x) + 0.5) - 1.0) < 0.99) {
        id = id + floor(mod(frame, vTextureId.y - vTextureId.x) + 0.5);
    }
    vec2 pos = fract(vTexturePos) * 0.03125;
    pos.x += floor(mod(id, 32.0)) * 0.03125;
    pos.y += floor(id / 32.0) * 0.03125;
    gl_FragColor = texture2D(texture, pos) * vec4(vColour, 1.0);

    float scale = (frame - 6000.0) / 12000.0;
    if (scale > 1.0) {
        scale = 2.0 - scale;
    } else if (scale < 0.0) {
        scale = -scale;
    }
    scale = 1.0 - scale;

    float light = max(vLighting.x, vLighting.y * scale);
    float val = pow(0.9, 16.0 - light) * 2.0;
    gl_FragColor.rgb *= clamp(pow(val, 1.5) / 2.0, 0.0, 1.0);
    if (disAlpha == 1 && gl_FragColor.a < 0.5) discard;
}
	";
	
	public function createShader(source : String, type : Int) : Shader {
		var shader = gl.createShader(type);
		gl.shaderSource(shader, source);
		gl.compileShader(shader);

		if (!gl.getShaderParameter(shader, RenderingContext.COMPILE_STATUS)) {
			throw gl.getShaderInfoLog(shader);
		}
		return shader;
	}
	
	public function createProgram(vertexShader : Shader, fragmentShader : Shader) : Program {
		var program = gl.createProgram();
		gl.attachShader(program, vertexShader);
		gl.attachShader(program, fragmentShader);
		gl.linkProgram(program);
		gl.useProgram(program);
		return program;
	}
}

class Camera {
	
	public function new() {
		
	}
}