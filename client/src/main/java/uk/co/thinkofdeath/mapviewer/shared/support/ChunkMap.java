package uk.co.thinkofdeath.mapviewer.shared.support;

import com.google.gwt.core.client.JavaScriptObject;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;

import java.util.List;

public class ChunkMap<T extends Chunk> extends JavaScriptObject {
    protected ChunkMap() {
    }

    public static native <T extends Chunk> ChunkMap<T> create()/*-{
        return {
            map: [],
            values: @java.util.ArrayList::new()()
        };
    }-*/;

    public final native int size()/*-{
        return this.values.@java.util.ArrayList::size()();
    }-*/;

    public final native boolean contains(int x, int z)/*-{
        var cx = this.map[x];
        if (cx == null) {
            return false;
        }
        return cx[z] != null;
    }-*/;

    public final native void put(int x, int z, T chunk)/*-{
        var cx = this.map[x];
        if (cx == null) {
            this.map[x] = cx = [];
        }
        cx[z] = chunk;
        this.values.@java.util.ArrayList::add(Ljava/lang/Object;)(chunk);
    }-*/;

    public final native T get(int x, int z)/*-{
        var cx = this.map[x];
        if (cx == null) {
            return null;
        }
        return cx[z];
    }-*/;

    public final native T remove(int x, int z)/*-{
        var cx = this.map[x];
        if (cx == null) {
            return null;
        }
        var val = cx[z];
        delete cx[z];
        this.values.@java.util.ArrayList::remove(Ljava/lang/Object;)(val);
        return val;
    }-*/;

    public final native List<Chunk> values()/*-{
        return this.values;
    }-*/;
}
