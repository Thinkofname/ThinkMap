package uk.co.thinkofdeath.mapviewer.client.network;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.ArrayBuffer;
import elemental.html.WebSocket;
import uk.co.thinkofdeath.mapviewer.shared.support.DataReader;

import java.util.logging.Logger;

public class Connection implements EventListener {

    private static final Logger logger = Logger.getLogger("Connection");

    private final WebSocket webSocket;
    private final String address;

    public Connection(String address, final Runnable callback) {
        this.address = address;
        webSocket = Browser.getWindow().newWebSocket("ws://" + address + "/server");
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

    private native void send(ArrayBuffer buffer)/*-{
        (this.@uk.co.thinkofdeath.mapviewer.client.network.Connection::webSocket).send(buffer);
    }-*/;

    @Override
    public void handleEvent(Event evt) {
        MessageEvent event = (MessageEvent) evt;
        DataReader reader = DataReader.create((ArrayBuffer) ((MessageEvent) evt).getData());
    }
}
