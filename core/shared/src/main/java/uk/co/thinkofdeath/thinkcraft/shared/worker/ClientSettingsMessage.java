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

public class ClientSettingsMessage extends WorkerMessage {

    private boolean oresHidden;

    ClientSettingsMessage() {
    }

    public ClientSettingsMessage(boolean oresHidden) {
        this.oresHidden = oresHidden;
    }

    public boolean areOresHidden() {
        return oresHidden;
    }

    @Override
    public void serialize(WriteSerializer serializer) {
        super.serialize(serializer);
        serializer.putBoolean("oresHidden", oresHidden);
    }

    @Override
    protected void read(ReadSerializer serializer) {
        oresHidden = serializer.getBoolean("oresHidden");
    }

    @Override
    protected WorkerMessage create() {
        return new ClientSettingsMessage();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }
}
