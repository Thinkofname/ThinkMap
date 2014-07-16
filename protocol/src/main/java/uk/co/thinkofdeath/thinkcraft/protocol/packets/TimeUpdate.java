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

package uk.co.thinkofdeath.thinkcraft.protocol.packets;

import uk.co.thinkofdeath.thinkcraft.protocol.Packet;
import uk.co.thinkofdeath.thinkcraft.protocol.PacketStream;
import uk.co.thinkofdeath.thinkcraft.protocol.ServerPacketHandler;

public class TimeUpdate implements Packet<ServerPacketHandler> {

    private int currentTime;

    public TimeUpdate() {
    }

    public TimeUpdate(int currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public Packet<ServerPacketHandler> create() {
        return new TimeUpdate();
    }

    @Override
    public void read(PacketStream in) {
        currentTime = in.readInt();
    }

    @Override
    public void write(PacketStream out) {
        out.writeInt(currentTime);
    }

    @Override
    public void handle(ServerPacketHandler handler) {
        handler.handle(this);
    }

    public int getCurrentTime() {
        return currentTime;
    }
}
