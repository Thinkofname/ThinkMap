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

import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;

public abstract class WorkerMessage implements Message {

    private boolean ret;
    private int sender;

    WorkerMessage() {
    }

    /**
     * Returns whether this message should be replied to
     *
     * @return Whether this message should be replied to
     */
    public boolean getReturn() {
        return ret;
    }

    public void setReturn(boolean ret) {
        this.ret = ret;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getSender() {
        return sender;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.putBoolean("return", ret);
    }

    protected abstract void read(Serializer serializer);

    protected abstract WorkerMessage create();

    @Override
    public Message deserialize(Serializer serializer) {
        WorkerMessage message = create();
        message.ret = serializer.getBoolean("return");
        message.read(serializer);
        return message;
    }
}
