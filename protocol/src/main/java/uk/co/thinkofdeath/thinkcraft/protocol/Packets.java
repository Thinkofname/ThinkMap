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

package uk.co.thinkofdeath.thinkcraft.protocol;

import uk.co.thinkofdeath.thinkcraft.protocol.packets.*;

import java.util.HashMap;

public class Packets {
    // From server
    private static Packet<ServerPacketHandler>[] serverById = new Packet[256];
    private static HashMap<Class<? extends Packet>, Integer> serverByClass = new HashMap<>();
    private static int lastServerId = 0;

    // From client
    private static Packet<ClientPacketHandler>[] clientById = new Packet[256];
    private static HashMap<Class<? extends Packet>, Integer> clientByClass = new HashMap<>();
    private static int lastClientId = 0;

    static {
        client(new InitConnection());
        client(new KeepAlive());

        server(new ServerSettings());
        server(new TimeUpdate());
        server(new SpawnPosition());
    }

    public static Packet<ServerPacketHandler> createServerPacket(int id) {
        return serverById[id].create();
    }

    public static Packet<ClientPacketHandler> createClientPacket(int id) {
        return clientById[id].create();
    }

    public static int getServerPacketId(Packet<ServerPacketHandler> packet) {
        return serverByClass.get(packet.getClass());
    }

    public static int getClientPacketId(Packet<ClientPacketHandler> packet) {
        return clientByClass.get(packet.getClass());
    }

    private static void server(Packet<ServerPacketHandler> packet) {
        serverById[lastServerId] = packet;
        serverByClass.put(packet.getClass(), lastServerId);
        lastServerId++;
    }

    private static void client(Packet<ClientPacketHandler> packet) {
        clientById[lastClientId] = packet;
        clientByClass.put(packet.getClass(), lastClientId);
        lastClientId++;
    }
}
