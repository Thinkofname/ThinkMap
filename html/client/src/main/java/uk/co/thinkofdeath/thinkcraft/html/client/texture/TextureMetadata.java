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

package uk.co.thinkofdeath.thinkcraft.html.client.texture;

import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.IntArraySerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializable;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;

public class TextureMetadata implements Serializable {

    private int frameTime;
    private int[] frames;

    @Override
    public void serialize(Serializer serializer) {
        Serializer animation = Platform.workerSerializers().create();
        animation.putInt("frametime", frameTime);
        if (frames != null) {
            IntArraySerializer frs = Platform.workerSerializers().createIntArray();
            for (int f : frames) {
                frs.add(f);
            }
            animation.putArray("frames", frs);
        }
    }

    @Override
    public void deserialize(Serializer serializer) {
        Serializer animation = serializer.getSub("animation");
        if (animation.has("frametime")) {
            frameTime = animation.getInt("frametime");
        } else {
            frameTime = 1;
        }

        if (animation.has("frames")) {
            IntArraySerializer frs = (IntArraySerializer) animation.getArray("frames");
            frames = new int[frs.size()];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = frs.getInt(i);
            }
        }
    }

    public int getFrameTime() {
        return frameTime;
    }

    public int[] getFrames() {
        return frames;
    }

    @Override
    public Serializable create() {
        return new TextureMetadata();
    }
}
