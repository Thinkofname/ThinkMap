package uk.co.thinkofdeath.mapviewer.client;

import com.google.gwt.core.client.EntryPoint;
import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.ImageElement;
import elemental.js.util.Json;
import elemental.xml.XMLHttpRequest;
import uk.co.thinkofdeath.mapviewer.client.network.Connection;

import java.util.HashMap;

public class MapViewer implements EntryPoint, EventListener {

    private ImageElement texture;
    private HashMap<String, TextureMap.Texture> textures = new HashMap<>();
    private XMLHttpRequest xhr;
    private Connection connection;

    public void onModuleLoad() {
        Browser.getWindow().getConsole().log("Started");
        texture = (ImageElement) Browser.getDocument().createElement("img");
        texture.setOnload(this);
        texture.setSrc("block_images/blocks.png");
        xhr = Browser.getWindow().newXMLHttpRequest();
        xhr.open("GET", "block_images/blocks.json", true);
        xhr.setOnload(this);
        xhr.send();
    }

    private int loaded = 0;
    @Override
    public void handleEvent(Event event) {
        loaded++;
        Browser.getWindow().getConsole().log("Loaded: " + loaded);

        if (loaded != 2) return;

        TextureMap tmap = Json.parse((String) xhr.getResponse());
        tmap.forEach(new TextureMap.Looper() {
            @Override
            public void forEach(String k, TextureMap.Texture v) {
                textures.put(k, v);
            }
        });

        connection = new Connection("localhost:23333", new Runnable() {
            @Override
            public void run() {
                // TODO:
            }
        });

    }
}
