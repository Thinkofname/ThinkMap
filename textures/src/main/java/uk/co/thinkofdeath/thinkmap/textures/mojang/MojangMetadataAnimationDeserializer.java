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

package uk.co.thinkofdeath.thinkmap.textures.mojang;

import com.google.gson.*;

import java.lang.reflect.Type;

class MojangMetadataAnimationDeserializer implements JsonDeserializer<MojangMetadataAnimation> {
    @Override
    public MojangMetadataAnimation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MojangMetadataAnimation animation = new MojangMetadataAnimation();
        JsonObject object = json.getAsJsonObject();
        if (object.has("frames")) {
            JsonArray frames = object.getAsJsonArray("frames");
            animation.frames = new int[frames.size()];
            for (int i = 0; i < animation.frames.length; i++) {
                animation.frames[i] = frames.get(i).getAsInt();
            }
        }
        if (object.has("frametime")) {
            animation.frametime = object.get("frametime").getAsInt();
        }
        return animation;
    }
}
