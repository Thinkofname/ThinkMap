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

package uk.co.thinkofdeath.thinkcraft.html.worker;


import com.google.gwt.core.client.EntryPoint;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.WorkerGlobalScope;
import uk.co.thinkofdeath.thinkcraft.html.shared.JavascriptLib;
import uk.co.thinkofdeath.thinkcraft.html.shared.TextureMap;
import uk.co.thinkofdeath.thinkcraft.html.shared.serialize.JsObjectSerializer;
import uk.co.thinkofdeath.thinkcraft.html.shared.settings.ClientSettings;
import uk.co.thinkofdeath.thinkcraft.html.worker.world.WorkerChunk;
import uk.co.thinkofdeath.thinkcraft.html.worker.world.WorkerWorld;
import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockRegistry;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.worker.*;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

import java.util.HashMap;

public class Worker implements EntryPoint, EventListener, IMapViewer, MessageHandler {

    private final BlockRegistry blockRegistry = new BlockRegistry(this);
    private final WorkerWorld world = new WorkerWorld(this);

    private ClientSettings clientSettings;
    private HashMap<String, Texture> textures = new HashMap<>();

    @Override
    public void onModuleLoad() {
        JavascriptLib.init();
        setOnMessage(this);
    }

    @Override
    public void handleEvent(Event evt) {
        MessageEvent event = (MessageEvent) evt;
        JsObjectSerializer serializer = JsObjectSerializer.from(event.getData());
        Message message = Messages.read(serializer);
        message.handle(this);
    }

    private void handleSettings() {
        if (clientSettings.areOresHidden()) {
            Texture replacement = textures.get("stone");
            textures.put("gold_ore", replacement);
            textures.put("iron_ore", replacement);
            textures.put("coal_ore", replacement);
            textures.put("lapis_ore", replacement);
            textures.put("diamond_ore", replacement);
            textures.put("redstone_ore", replacement);
            textures.put("emerald_ore", replacement);
            textures.put("quartz_ore", textures.get("netherrack"));
        }
    }

    @Override
    public BlockRegistry getBlockRegistry() {
        return blockRegistry;
    }

    @Override
    public Texture getTexture(String name) {
        Texture texture = textures.get(name);
        if (texture == null) {
            System.err.println("Missing texture: " + name);
            return textures.get("missing_texture");
        }
        return texture;
    }

    @Override
    public World getWorld() {
        return world;
    }

    public ClientSettings getSettings() {
        return clientSettings;
    }

    // Web worker magic

    /**
     * Returns the web worker global scope for this worker
     *
     * @return The global scope
     */
    public native WorkerGlobalScope getSelf()/*-{
        return $wnd;
    }-*/;

    public void sendMessage(WorkerMessage message, boolean reply, Object... transferables) {
        JsObjectSerializer serializer = JsObjectSerializer.newInstance();
        message.setReturn(reply);
        Messages.write(message, serializer);
        postMessage(serializer, transferables);
    }

    /**
     * Posts a message back to the creator of this worker and transfers all of the transferables
     *
     * @param message
     *         The message to send
     * @param transferables
     *         The transferables to send
     */
    private native void postMessage(JsObjectSerializer message, Object[] transferables)/*-{
        self.postMessage(message, transferables);
    }-*/;

    private native void setOnMessage(EventListener eventListener)/*-{
        self.onmessage = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(eventListener);
    }-*/;

    private native void importScripts(String script)/*-{
        importScripts(script);
    }-*/;

    @Override
    public void handle(ChunkBuildMessage chunkBuildMessage) {
        WorkerChunk chunk = (WorkerChunk) world.getChunk(chunkBuildMessage.getX(),
                chunkBuildMessage.getZ());
        if (chunk != null) {
            chunk.build(chunkBuildMessage.getSectionNumber(),
                    chunkBuildMessage.getBuildNumber());
        } else {
            sendMessage(Messages.NULL, false);
        }
    }

    @Override
    public void handle(ChunkUnloadMessage chunkUnloadMessage) {
        world.unloadChunk(chunkUnloadMessage.getX(), chunkUnloadMessage.getZ());
        sendMessage(Messages.NULL, false);
    }

    @Override
    public void handle(ChunkBuildReply chunkBuildReply) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(ChunkLoadMessage chunkLoadMessage) {
        WorkerChunk c = new WorkerChunk(world,
                chunkLoadMessage.getX(), chunkLoadMessage.getZ(),
                chunkLoadMessage.getData(), chunkLoadMessage.getReturn());
        world.addChunk(c);
        c.postAdd();
    }

    @Override
    public void handle(ChunkLoadedMessage chunkLoadedMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(ClientSettingsMessage clientSettingsMessage) {
        clientSettings = ClientSettings.create(clientSettingsMessage.areOresHidden());
        handleSettings();
        getBlockRegistry().init();
        sendMessage(Messages.NULL, false);
    }

    @Override
    public void handle(TextureMessage textureMessage) {
        TextureMap tmap = (TextureMap) textureMessage.getValue();
        tmap.forEach(new TextureMap.Looper() {
            @Override
            public void forEach(String k, Texture v) {
                textures.put(k, v);
            }
        });
        tmap.copyGrassColormap(Model.grassBiomeColors);
        tmap.copyFoliageColormap(Model.foliageBiomeColors);
        sendMessage(Messages.NULL, false);
    }
}
