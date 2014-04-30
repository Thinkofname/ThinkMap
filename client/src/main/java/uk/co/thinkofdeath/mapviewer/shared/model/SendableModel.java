package uk.co.thinkofdeath.mapviewer.shared.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;

public class SendableModel extends JavaScriptObject {

    protected SendableModel() {
    }

    public static native SendableModel create(Model model, int x, int y, int z, Block owner)/*-{
        var m = {
            faces: [],
            x: x,
            y: y,
            z: z,
            ownerStr: owner.@uk.co.thinkofdeath.mapviewer.shared.block.Block::toString()()
        };
        var faces = model.@uk.co.thinkofdeath.mapviewer.shared.model.Model::faces;
        var size = faces.@java.util.List::size()();
        for (var i = 0; i < size; i++) {
            var face = faces.@java.util.List::get(I)(i);
            var verts = @com.google.gwt.core.client.JsArrayUtils::readOnlyJsArray([Lcom/google/gwt/core/client/JavaScriptObject;)(
                face.@uk.co.thinkofdeath.mapviewer.shared.model.ModelFace::vertices
            );
            var colour = {
                r: face.@uk.co.thinkofdeath.mapviewer.shared.model.ModelFace::r,
                g: face.@uk.co.thinkofdeath.mapviewer.shared.model.ModelFace::g,
                b: face.@uk.co.thinkofdeath.mapviewer.shared.model.ModelFace::b
            };
            m.faces.push({
                verts: [verts[0], verts[1], verts[2], verts[3]],
                colour: colour,
                cullable: face.@uk.co.thinkofdeath.mapviewer.shared.model.ModelFace::cullable,
                faceStr: (face.@uk.co.thinkofdeath.mapviewer.shared.model.ModelFace::face).@uk.co.thinkofdeath.mapviewer.shared.Face::toString()(),
                textureStr: (face.@uk.co.thinkofdeath.mapviewer.shared.model.ModelFace::texture).@uk.co.thinkofdeath.mapviewer.shared.Texture::getName()()
            });
        }
        return m;
    }-*/;

    public final native int getX()/*-{
        return this.x;
    }-*/;

    public final native int getY()/*-{
        return this.y;
    }-*/;

    public final native int getZ()/*-{
        return this.z;
    }-*/;

    public final native Block getOwner(IMapViewer mapViewer)/*-{
        if (this.owner != null) {
            return this.owner;
        }
        var br = mapViewer.@uk.co.thinkofdeath.mapviewer.shared.IMapViewer::getBlockRegistry()();
        return this.owner = br.@uk.co.thinkofdeath.mapviewer.shared.block.BlockRegistry::get(Ljava/lang/String;)(this.ownerStr);
    }-*/;

    public final native JsArray<Face> getFaces()/*-{
        return this.faces;
    }-*/;

    public static class Face extends JavaScriptObject {
        protected Face() {
        }

        public final native int getRed()/*-{
            return this.colour.r;
        }-*/;

        public final native int getGreen()/*-{
            return this.colour.g;
        }-*/;

        public final native int getBlue()/*-{
            return this.colour.b;
        }-*/;

        public final native boolean getCullable()/*-{
            return this.cullable;
        }-*/;

        public final native Texture getTexture(IMapViewer mapViewer)/*-{
            if (this.texture != null) {
                return this.texture;
            }
            return this.texture = mapViewer.@uk.co.thinkofdeath.mapviewer.shared.IMapViewer::getTexture(Ljava/lang/String;)(this.textureStr);
        }-*/;

        public final native JsArray<ModelVertex> getVertices()/*-{
            return this.verts;
        }-*/;


        public final native uk.co.thinkofdeath.mapviewer.shared.Face getFace()/*-{
            if (this.face != null) {
                return this.face;
            }
            return this.face = @uk.co.thinkofdeath.mapviewer.shared.Face::valueOf(Ljava/lang/String;)(this.faceStr);
        }-*/;

    }
}
