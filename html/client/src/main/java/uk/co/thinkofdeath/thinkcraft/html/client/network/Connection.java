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

package uk.co.thinkofdeath.thinkcraft.html.client.network;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.ArrayBuffer;
import elemental.html.WebSocket;
import uk.co.thinkofdeath.thinkcraft.protocol.ClientPacketHandler;
import uk.co.thinkofdeath.thinkcraft.protocol.Packet;
import uk.co.thinkofdeath.thinkcraft.protocol.Packets;
import uk.co.thinkofdeath.thinkcraft.protocol.ServerPacketHandler;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.InitConnection;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.Buffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;

/**
 * Manages a connection between the client and the Bukkit plugin. Fires events based on messages received.
 */
public class Connection implements EventListener {

    private final WebSocket webSocket;
    private final String address;
    private final ServerPacketHandler handler;

    /**
     * Creates a connect to the plugin at the address. Calls the callback once the connection succeeds.
     *
     * @param address
     *         The address to connect to, may include the port
     * @param handler
     *         The handler to handle received events
     * @param callback
     *         The Runnable to call once the connection is completed
     */
    public Connection(String address, ServerPacketHandler handler, final Runnable callback) {
        this.address = address;
        this.handler = handler;
        webSocket = Browser.getWindow().newWebSocket("ws://" + address + "/server/ws");
        // Work in binary instead of strings
        webSocket.setBinaryType("arraybuffer");
        webSocket.setOnopen(new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                System.out.println("Connected to server");
                send(new InitConnection());
                if (callback != null) callback.run();
            }
        });
        webSocket.setOnmessage(this);
    }

    /**
     * The address of the server this connection is connected to
     *
     * @return The address of the server
     */
    public String getAddress() {
        return address;
    }

    // Work around for the fact that GWT doesn't support sending
    // ArrayBuffers
    private native void send(Buffer buffer)/*-{
        this.@uk.co.thinkofdeath.thinkcraft.html.client.network.Connection::webSocket.send(buffer.buffer);
    }-*/;

    public void send(Packet<ClientPacketHandler> packet) {
        DataPacketStream packetStream = new DataPacketStream();
        packetStream.writeInt(Packets.getClientPacketId(packet));
        packet.write(packetStream);
        UByteBuffer data = packetStream.getBuffer().getArray();
        send(data);
    }

    /**
     * Internal method to receive websocket messages
     *
     * @param evt
     *         Event
     */
    @Override
    public void handleEvent(Event evt) {
        MessageEvent event = (MessageEvent) evt;

        DataPacketStream packetStream = new DataPacketStream((ArrayBuffer) event.getData());
        int id = packetStream.readUByte();
        Packet<ServerPacketHandler> packet = Packets.createServerPacket(id);
        packet.read(packetStream);
        packet.handle(handler);
    }
}
