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

package uk.co.thinkofdeath.thinkcraft.html.shared.serialize;

import uk.co.thinkofdeath.thinkcraft.shared.serializing.*;

public class JsSerializerFactory implements SerializerFactory {

    @Override
    public final Serializer create() {
        return JsObjectSerializer.newInstance();
    }

    @Override
    public final SerializerArraySerializer createSerializerArray() {
        return JsSerializerArray.create();
    }

    @Override
    public final IntArraySerializer createIntArray() {
        return JsIntArray.create();
    }

    @Override
    public final BooleanArraySerializer createBooleanArray() {
        return JsBooleanArray.create();
    }

    @Override
    public final StringArraySerializer createStringArray() {
        return JsStringArray.create();
    }

    @Override
    public final ArraySerializerArraySerializer createArrayArray() {
        return JsArraySerializerArray.create();
    }
}
