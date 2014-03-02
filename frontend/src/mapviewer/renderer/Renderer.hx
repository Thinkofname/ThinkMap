package mapviewer.renderer;

interface Renderer {

    public function draw() : Void;
    public function resize(width : Int, height : Int) : Void;
    public function connected() : Void;
    public function shouldLoad(x : Int, z : Int) : Void;
    public function moveTo(x : Int, y : Int, z : Int) : Void;
}