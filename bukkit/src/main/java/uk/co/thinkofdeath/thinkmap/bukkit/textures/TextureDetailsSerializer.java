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

package uk.co.thinkofdeath.thinkmap.bukkit.textures;

import com.google.gson.*;
import uk.co.thinkofdeath.thinkmap.textures.TextureDetails;

import java.lang.reflect.Type;

public class TextureDetailsSerializer implements JsonSerializer<TextureDetails> {
    @Override
    public JsonElement serialize(TextureDetails src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(src.getStart()));
        array.add(new JsonPrimitive(src.getEnd()));
        return array;
    }
}
