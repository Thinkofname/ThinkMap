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

import java.util.HashMap;

public class Messages {

    public static final NullMessage NULL = new NullMessage();

    private static final HashMap<String, Message> messages = new HashMap<>();
    private static final HashMap<Class<? extends Message>, String> idByClass = new HashMap<>();

    static {
        register("null", new NullMessage());

        register("chunk:build", new ChunkBuildMessage());
        register("chunk:unload", new ChunkUnloadMessage());
        register("chunk:breply", new ChunkBuildReply());
        register("chunk:load", new ChunkLoadMessage());
        register("chunk:loaded", new ChunkLoadedMessage());

        register("settings", new ClientSettingsMessage());
        register("textures", new TextureMessage()); // TODO: Fixme
        register("pool:free", new FreeMessage()); // TODO: Fixme
    }

    public static Message read(ReadSerializer serializer) {
        Message message = messages.get(serializer.getString("$id"));
        return message.deserialize(serializer);
    }

    public static void write(Message message, WriteSerializer serializer) {
        serializer.putString("$id", idByClass.get(message.getClass()));
        message.serialize(serializer);
    }

    private static void register(String id, Message message) {
        messages.put(id, message);
        idByClass.put(message.getClass(), id);
    }

    private static class NullMessage extends WorkerMessage {
        @Override
        public void handle(MessageHandler handler) {

        }

        @Override
        protected void read(ReadSerializer serializer) {

        }

        @Override
        protected WorkerMessage create() {
            return NULL;
        }

        @Override
        public Message deserialize(ReadSerializer serializer) {
            return NULL;
        }
    }
}
