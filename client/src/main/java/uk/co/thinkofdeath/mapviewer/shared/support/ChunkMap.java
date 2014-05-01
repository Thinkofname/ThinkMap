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
