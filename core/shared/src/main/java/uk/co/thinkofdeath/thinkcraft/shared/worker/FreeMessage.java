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

package uk.co.thinkofdeath.thinkcraft.shared.worker;

import uk.co.thinkofdeath.thinkcraft.shared.serializing.ReadSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.WriteSerializer;

@Deprecated
public class FreeMessage extends WorkerMessage {

    private Object value;

    FreeMessage() {
    }

    public FreeMessage(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void serialize(WriteSerializer serializer) {
        super.serialize(serializer);
        serializer.putTemp("value", value);
    }

    @Override
    protected void read(ReadSerializer serializer) {
        value = serializer.getTemp("value");
    }

    @Override
    protected WorkerMessage create() {
        return new TextureMessage();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }
}
