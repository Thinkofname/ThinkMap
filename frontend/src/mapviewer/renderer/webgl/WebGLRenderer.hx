package mapviewer.renderer.webgl;
import js.Browser;
import js.html.CanvasElement;
import js.html.Event;
import js.html.ImageElement;
import js.html.KeyboardEvent;
import js.html.MouseEvent;
import js.html.webgl.Program;
import js.html.webgl.RenderingContext;
import js.html.webgl.Shader;
import js.html.webgl.Texture;
import js.html.webgl.UniformLocation;
import mapviewer.collision.Box;
import mapviewer.js.Utils;
import mapviewer.Main;
import mapviewer.renderer.Renderer;
import mapviewer.renderer.webgl.glmatrix.Mat4;
import mapviewer.renderer.webgl.WebGLRenderer.Camera;

typedef GL = RenderingContext;

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
	private var movingForward : Bool = false;
	private var movingBackwards : Bool = false;
	public var camera : Camera;
	private var vSpeed = MIN_VSPEED;
	inline private static var MIN_VSPEED : Float = -0.2;
	private var onGround : Bool = false;
	private var offGroundFor : Int = 0;
	private var cx : Int = 0;
	private var cy : Int = 0;
	private var cz : Int = 0;
	private var firstPerson : Bool = true;
	private var flyMode : Bool = false;
	private var moveDir : Int = 0;
	
	public function new(canvas : CanvasElement) {
		this.canvas = canvas;
		pMatrix = Mat4.create();
		uMatrix = Mat4.create();
		temp = Mat4.create();
		uMatrix.identity();
		blockTextures = new Array<Texture>();
		camera = new Camera();
		camera.y = 12 * 16;
		
		// Flags are set for performance
		gl = canvas.getContextWebGL( { alpha: false, premultipliedAlpha: false, 
			antialias: false } );
		
		if (gl == null) throw "WebGL not supported";
		
		resize(0, 0);
		
		// Convert images to textures
		for (img in Main.blockTexturesRaw) {
			blockTextures.push(loadTexture(img));
		}
		
		var chunkVertexShader = createShader(chunkVertexShaderSource, GL.VERTEX_SHADER);
		var chunkFragmentShader = createShader(chunkFragmentShaderSource, GL.FRAGMENT_SHADER);
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
		
		gl.enable(GL.DEPTH_TEST);
		gl.enable(GL.CULL_FACE);
		gl.cullFace(GL.BACK);
		gl.frontFace(GL.CW);
		
		gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA);
		
		var hasLock = false;
		
		Browser.document.body.onclick = function(e : MouseEvent) {
			if (!hasLock) Utils.requestPointerLock(Browser.document.body);
		};
		Browser.document.onmousedown = function(e : MouseEvent) {
			if (hasLock && firstPerson) {			
				e.preventDefault();
				if (e.button == 2) flyMode = true;
			}
		}
		Browser.document.onmouseup = function(e : MouseEvent) {
			if (hasLock	&& firstPerson) {		
				e.preventDefault();
				if (e.button == 2) flyMode = false;
			}
		}
		Browser.document.onmousemove = function(e : MouseEvent) {
			e.preventDefault();
			if (!hasLock || !firstPerson) return;
			camera.rotY += Utils.movementX(e) / 300.0;	
			camera.rotX += Utils.movementY(e) / 300.0;	
		};
		Browser.document.onkeydown = function(e : KeyboardEvent) {
			if (!firstPerson) return;
			if (e.keyCode == 'W'.code) {
				e.preventDefault();
				movingForward = true;
			} else if (e.keyCode == 'S'.code) {
				e.preventDefault();
				movingBackwards = true;
			} else if (e.keyCode == 'A'.code) {
				moveDir = 1;
			} else if (e.keyCode == 'D'.code) {
				moveDir = -1;
			} else if (e.keyCode == ' '.code && (onGround || offGroundFor <= 1)) {
				e.preventDefault();
				vSpeed = 0.1;
			}
		};
		Browser.document.onkeyup = function(e : KeyboardEvent) {
			e.preventDefault();
			if (!firstPerson) return;
			if (e.keyCode == 'W'.code) {
				e.preventDefault();
				movingForward = false;
			} else if (e.keyCode == 'S'.code) {
				e.preventDefault();
				movingBackwards = false;
			} else if (e.keyCode == 'A'.code) {
				if (moveDir == 1) moveDir = 0;
			} else if (e.keyCode == 'D'.code) {
				if (moveDir == -1) moveDir = 0;
			}
		};
		function pToggle(e) {
			hasLock = !hasLock;
		}
		Browser.document.addEventListener("pointerlockchange", pToggle, false);
		Browser.document.addEventListener("webkitpointerlockchange", pToggle, false);
		Browser.document.addEventListener("mozpointerlockchange", pToggle, false);
		canvas.oncontextmenu = function(e : Event) { e.preventDefault(); };
	}
	
	inline public static var viewDistance : Int = 4;
	
	private var lastFrame : Int;
	private var temp : Mat4;
	
    public function draw() : Void {
		var delta : Float = Math.min((Utils.now() - lastFrame) / (1000 / 60), 2.0);
		lastFrame = Utils.now();
		
		gl.viewport(0, 0, canvas.width, canvas.height);
		var skyPosition = getScale();
		gl.clearColor(getScaledNumber(122 / 255, 0, skyPosition),
					  getScaledNumber(165 / 255, 0, skyPosition),
					  getScaledNumber(247 / 255, 0, skyPosition),
					  1);
		gl.colorMask(true, true, true, false);
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		
		gl.useProgram(mainProgram);
		gl.uniformMatrix4fv(pMatrixLocation, false, pMatrix);
		
		gl.activeTexture(GL.TEXTURE0);
		gl.bindTexture(GL.TEXTURE_2D, blockTextures[0]);
		gl.uniform1i(blockTextureLocation, 0);
		gl.uniform1f(frameLocation, Main.world.currentTime);
		
		if (firstPerson) {
			var lx = camera.x;
			var ly = camera.y;
			var lz = camera.z;
			
			if (!flyMode) {
				camera.y += vSpeed * delta;
				vSpeed = Math.max(MIN_VSPEED, vSpeed - 0.005 * delta);
			}
			
			var speed = flyMode ? 0.3 : 0.1;
			
			if (movingForward) {
				camera.x += speed * Math.sin(camera.rotY) * (flyMode ? Math.cos(camera.rotX) : 1) * delta;
				camera.z -= speed * Math.cos(camera.rotY) * (flyMode ? Math.cos(camera.rotX) : 1) * delta;
				if (flyMode) camera.y -= speed * Math.sin(camera.rotX) * delta;
			} else if (movingBackwards) {
				camera.x -= speed * Math.sin(camera.rotY) * (flyMode ? Math.cos(camera.rotX) : 1) * delta;
				camera.z += speed * Math.cos(camera.rotY) * (flyMode ? Math.cos(camera.rotX) : 1) * delta;
				if (flyMode) camera.y += speed * Math.sin(camera.rotX) * delta;
			}
			
			if (moveDir != 0) {
				camera.x -= moveDir * speed * Math.sin(camera.rotY + (Math.PI / 2)) * delta;
				camera.z += moveDir * speed * Math.cos(camera.rotY + (Math.PI / 2)) * delta;
			}
			
			checkCollision(lx, ly, lz);
			
			if (onGround) vSpeed = 0.0;
		}
		
		uMatrix.identity();
		uMatrix.scale( -1, -1, 1);
		uMatrix.rotateX( -camera.rotX - Math.PI);
		uMatrix.rotateY( -(-camera.rotY - Math.PI));
		temp.identity();
		temp.translate([-camera.x, -camera.y, -camera.z]);
		gl.uniformMatrix4fv(uMatrixLocation, false, uMatrix.multiply(temp));
		
		var ww : WebGLWorld = cast Main.world;
		ww.render(this);
		
		gl.clearColor(1, 1, 1, 1);
		gl.colorMask(false, false, false, true);
		gl.clear(GL.COLOR_BUFFER_BIT);

		var nx = (Std.int(camera.x) >> 4);
		var nz = (Std.int(camera.z) >> 4);
		if (nx != cx || nz != cz) {
			for (chunk in Main.world.chunks) {
				var x = chunk.x;
				var z = chunk.z;
				if (x < nx - viewDistance || x >= nx + viewDistance || z < nz -
					viewDistance || z >= nz + viewDistance) {
					Main.world.removeChunk(x, z);
				}
			}

			var toRequest = [];
			for (x in nx - viewDistance ... nx + viewDistance) {
				for (z in nz - viewDistance ... nz + viewDistance) {
					if (Main.world.getChunk(x, z) == null) toRequest.push([x, z]);
				}
			}
			toRequest.sort(function(a, b) {
				var bx = b[0] - camera.x / 16;
				var bz = b[1] - camera.z / 16;
				var ax = a[0] - camera.x / 16;
				var az = a[1] - camera.z / 16;
				return Std.int((ax * ax + az * az) - (bx * bx + bz * bz));
			});
			for (req in toRequest) {			
				Main.world.writeRequestChunk(req[0], req[1]);
			}
			cx = nx;
			cz = nz;
		}
		var ny = Std.int(camera.y / 16);
		if (cy != ny) {
			cy = ny;
		}
	}
	
    public function resize(width : Int, height : Int) : Void {
		pMatrix.identity();
		pMatrix.perspective(Math.PI / 180 * 80, canvas.width / canvas.height, 0.1, 500);
	}
	
    public function connected() : Void {
		var toRequest = [];
		for (x in -viewDistance ... viewDistance) {
			for (z in -viewDistance ... viewDistance) {
				toRequest.push([x, z]);
			}
		}
		toRequest.sort(function(a, b) {
			return (a[0] * a[0] + a[1] * a[1]) - (b[0] * b[0] + b[1] * b[1]);
		});
		for (req in toRequest) {			
			Main.world.writeRequestChunk(req[0], req[1]);
		}
	}
	
	private function checkCollision(lx : Float, ly : Float, lz : Float) {
		var box = new Box(lx, ly - 1.6, lz, 0.5, 1.75, 0.5);
		
		var cx = Std.int(box.x);
		var cy = Std.int(box.y);
		var cz = Std.int(box.z);
		
		box.x = camera.x;
		cx = Std.int(box.x);
		(function() {
			for (x in cx - 2 ... cx + 2) {
				for (z in cz - 2 ... cz + 2) {
					for (y in cy - 3 ... cy + 3) {
						if (Main.world.getBlock(x, y, z).collidesWith(box, x, y, z)) {
							camera.x = lx;
							box.x = lx;
							return;
						}
					}
				}
			}
		})();
		
		box.z = camera.z;
		cz = Std.int(box.z);
		(function() {
			for (x in cx - 2 ... cx + 2) {
				for (z in cz - 2 ... cz + 2) {
					for (y in cy - 3 ... cy + 3) {
						if (Main.world.getBlock(x, y, z).collidesWith(box, x, y, z)) {
							camera.z = lz;
							box.z = lz;
							return;
						}
					}
				}
			}
		})();
		
		box.y = camera.y - 1.6;
		cy = Std.int(box.y);
		onGround = false;
		var hit = false;
		for (x in cx - 2 ... cx + 2) {
			for (z in cz - 2 ... cz + 2) {
				for (y in cy - 3 ... cy + 3) {
					if (Main.world.getBlock(x, y, z).collidesWith(box, x, y, z)) {
						hit = true;
						if (y <= cy) {
							onGround = true;
						}
					}
				}
			}
		}
		
		if (hit) {
			camera.y = ly;
			box.y = ly - 1.6;
			if (vSpeed > 0.0) vSpeed = 0.0;
		}
		
		if (!onGround) {
			offGroundFor++;
		} else {
			offGroundFor = 0;
		}
	}
	
    public function shouldLoad(x : Int, z : Int) : Bool {
		if (x < cx - viewDistance || x >= cx + viewDistance || z < cz - viewDistance
			|| z >= cz + viewDistance) {
			return false;
		}
		return true;
	}
	
    public function moveTo(x : Int, y : Int, z : Int) : Void { 
		camera.x = x;
		camera.y = y + 2;
		camera.z = z;
	}	
	
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
		gl.bindTexture(GL.TEXTURE_2D, tex);
		gl.pixelStorei(GL.UNPACK_FLIP_Y_WEBGL, 0);
		gl.pixelStorei(GL.UNPACK_PREMULTIPLY_ALPHA_WEBGL, 0);
		gl.texImage2D(GL.TEXTURE_2D, 0, 
			GL.RGBA, GL.RGBA, 
			GL.UNSIGNED_BYTE, imageElement);
		gl.texParameteri(GL.TEXTURE_2D, 
			GL.TEXTURE_MAG_FILTER, GL.NEAREST);
		gl.texParameteri(GL.TEXTURE_2D, 
			GL.TEXTURE_MIN_FILTER, GL.NEAREST);
		gl.bindTexture(GL.TEXTURE_2D, null);
		return tex;
	}
	
	private static var chunkVertexShaderSource : String = "
attribute vec3 position;
attribute vec4 colour;
attribute vec2 texturePos;
attribute vec2 textureId;
attribute vec2 lighting;

uniform mat4 pMatrix;
uniform mat4 uMatrix;
uniform vec2 offset;

varying vec4 vColour;
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

varying vec4 vColour;
varying vec2 vTextureId;
varying vec2 vTexturePos;
varying vec2 vLighting;

void main(void) {
    float id = floor(vTextureId.x + 0.5);
    if (floor((vTextureId.y - vTextureId.x) + 0.5) > 0.8) {
        id = id + floor(mod(frame, vTextureId.y - vTextureId.x) + 0.5);
    }
    vec2 pos = fract(vTexturePos) * 0.03125;
    pos.x += floor(mod(id, 32.0)) * 0.03125;
    pos.y += floor(id / 32.0) * 0.03125;
    gl_FragColor = texture2D(texture, pos) * vColour;

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

		if (!gl.getShaderParameter(shader, GL.COMPILE_STATUS)) {
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
	
	public var x : Float = 0;
	public var y : Float = 0;
	public var z : Float = 0;
	public var rotX : Float = 0;
	public var rotY : Float = 0;
	
	public function new() {
		
	}
}