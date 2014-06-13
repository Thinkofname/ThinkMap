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
import elemental.js.util.Json;
import uk.co.thinkofdeath.thinkcraft.shared.ClientSettings;
import uk.co.thinkofdeath.thinkcraft.shared.support.DataReader;

/**
 * Manages a connection between the client and the Bukkit plugin. Fires events based on messages received.
 */
public class Connection implements EventListener {

    private final WebSocket webSocket;
    private final String address;
    private final ConnectionHandler handler;

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
    public Connection(String address, ConnectionHandler handler, final Runnable callback) {
        this.address = address;
        this.handler = handler;
        webSocket = Browser.getWindow().newWebSocket("ws://" + address + "/server");
        // Work in binary instead of strings
        webSocket.setBinaryType("arraybuffer");
        webSocket.setOnopen(new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                System.out.println("Connected to server");
                send(Browser.getWindow().newUint8Array(1).getBuffer());
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
    private native void send(ArrayBuffer buffer)/*-{
        this.@uk.co.thinkofdeath.thinkcraft.html.client.network.Connection::webSocket.send(buffer);
    }-*/;

    /**
     * Internal method to receive websocket messages
     *
     * @param evt
     *         Event
     */
    @Override
    public void handleEvent(Event evt) {
        MessageEvent event = (MessageEvent) evt;
        DataReader reader = DataReader.create((ArrayBuffer) event.getData());

        switch (reader.getUint8(0)) {
            case 0: // Client settings
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < reader.getLength(); i++) {
                    builder.append((char) reader.getUint8(i));
                }
                handler.onSettings(Json.<ClientSettings>parse(builder.toString()));
                break;
            case 1: // Set position
                handler.onSetPosition(reader.getInt32(1), reader.getUint8(5), reader.getInt32(6));
                break;
            case 2: // Message
                builder = new StringBuilder();
                for (int i = 1; i < reader.getLength(); i++) {
                    builder.append((char) reader.getUint8(i));
                }
                handler.onMessage(builder.toString());
                break;
            case 3: // Time update
                handler.onTimeUpdate(reader.getInt32(1));
                break;
        }
    }
}
