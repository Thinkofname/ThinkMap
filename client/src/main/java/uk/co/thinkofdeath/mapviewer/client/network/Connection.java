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

package uk.co.thinkofdeath.mapviewer.client.network;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.ArrayBuffer;
import elemental.html.WebSocket;
import uk.co.thinkofdeath.mapviewer.shared.support.DataReader;

import java.util.logging.Logger;

/**
 * Manages a connection between the client and the Bukkit
 * plugin. Fires events based on messages received.
 */
public class Connection implements EventListener {

    private static final Logger logger = Logger.getLogger("Connection");

    private final WebSocket webSocket;
    private final String address;

    /**
     * Creates a connect to the plugin at the address. Calls the callback
     * once the connection succeeds.
     *
     * @param address  The address to connect to, may include the port
     * @param callback The Runnable to call once the connection is completed
     */
    public Connection(String address, final Runnable callback) {
        this.address = address;
        webSocket = Browser.getWindow().newWebSocket("ws://" + address + "/server");
        // Work in binary instead of strings
        webSocket.setBinaryType("arraybuffer");
        webSocket.setOnopen(new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                logger.info("Connected to server");
                send(Browser.getWindow().newUint8Array(1).getBuffer());
                callback.run();
            }
        });
        webSocket.setOnmessage(this);
    }

    // Work around for the fact that GWT doesn't support sending
    // ArrayBuffers
    private native void send(ArrayBuffer buffer)/*-{
        this.@uk.co.thinkofdeath.mapviewer.client.network.Connection::webSocket.send(buffer);
    }-*/;

    /**
     * Internal method to receive websocket messages
     *
     * @param evt Event
     */
    @Override
    public void handleEvent(Event evt) {
        MessageEvent event = (MessageEvent) evt;
        DataReader reader = DataReader.create((ArrayBuffer) ((MessageEvent) evt).getData());

        // TODO:
    }
}
