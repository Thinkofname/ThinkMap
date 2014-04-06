package uk.co.thinkofdeath.mapviewer.client;

import com.google.gwt.core.client.JavaScriptObject;

public class TextureMap extends JavaScriptObject {
    protected TextureMap() {
    }
    public final native void forEach(Looper looper)/*-{
        for (key in this) {
            if (this.hasOwnProperty(key)) {
                looper.@uk.co.thinkofdeath.mapviewer.client.TextureMap.Looper::forEach(Ljava/lang/String;Luk/co/thinkofdeath/mapviewer/client/TextureMap$Texture;)(
                    key,
                    new @uk.co.thinkofdeath.mapviewer.client.TextureMap.Texture::new(II)(this[key][0], this[key][1]));
            }
        }
    }-*/;

    public static class Texture {

        private int start;
        private int end;

        public Texture(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static interface Looper {
        void forEach(String k, Texture v);
    }
}
