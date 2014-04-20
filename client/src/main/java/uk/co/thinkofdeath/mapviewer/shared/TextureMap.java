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

package uk.co.thinkofdeath.mapviewer.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class TextureMap extends JavaScriptObject {
    protected TextureMap() {
    }

    public final native void forEach(Looper looper)/*-{
        for (key in this) {
            if (this.hasOwnProperty(key)) {
                looper.@uk.co.thinkofdeath.mapviewer.shared.TextureMap.Looper::forEach(Ljava/lang/String;Luk/co/thinkofdeath/mapviewer/shared/TextureMap$Texture;)(
                    key,
                    new @uk.co.thinkofdeath.mapviewer.shared.TextureMap.Texture::new(II)(this[key][0], this[key][1]));
            }
        }
    }-*/;

    public static interface Looper {
        void forEach(String k, Texture v);
    }

    public static class Texture {

        private int start;
        private int end;

        public Texture(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Returns the start position of the texture
         *
         * @return The start position
         */
        public int getStart() {
            return start;
        }

        /**
         * Returns the end position of the texture
         *
         * @return The end position
         */
        public int getEnd() {
            return end;
        }
    }
}
