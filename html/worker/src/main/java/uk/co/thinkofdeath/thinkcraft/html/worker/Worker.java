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
import uk.co.thinkofdeath.thinkcraft.html.shared.NativeLib;
import uk.co.thinkofdeath.thinkcraft.html.shared.TextureMap;
import uk.co.thinkofdeath.thinkcraft.html.shared.settings.ClientSettings;
import uk.co.thinkofdeath.thinkcraft.html.worker.world.WorkerChunk;
import uk.co.thinkofdeath.thinkcraft.html.worker.world.WorkerWorld;
import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockRegistry;
import uk.co.thinkofdeath.thinkcraft.shared.building.DynamicBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkBuildMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkUnloadMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.WorkerMessage;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

import java.util.HashMap;

public class Worker implements EntryPoint, EventListener, IMapViewer {

    private final BlockRegistry blockRegistry = new BlockRegistry(this);
    private final WorkerWorld world = new WorkerWorld(this);

    private ClientSettings clientSettings;
    private HashMap<String, Texture> textures = new HashMap<>();

    @Override
    public void onModuleLoad() {
        NativeLib.init();
        setOnmessage(this);
    }

    @Override
    public void handleEvent(Event evt) {
        MessageEvent event = (MessageEvent) evt;
        WorkerMessage message = (WorkerMessage) event.getData();

        switch (message.getType()) {
            case "chunk:load":
                ChunkLoadMessage chunkLoadMessage = (ChunkLoadMessage) message.getMessage();
                WorkerChunk c = new WorkerChunk(world,
                        chunkLoadMessage.getX(), chunkLoadMessage.getZ(),
                        chunkLoadMessage.getData(), message.getReturn());
                world.addChunk(c);
                c.postAdd();
                break;
            case "chunk:unload":
                ChunkUnloadMessage chunkUnloadMessage = (ChunkUnloadMessage) message.getMessage();
                world.unloadChunk(chunkUnloadMessage.getX(), chunkUnloadMessage.getZ());
                postMessage(WorkerMessage.create("null", null, false));
                break;
            case "chunk:build":
                ChunkBuildMessage chunkBuildMessage = (ChunkBuildMessage) message.getMessage();
                WorkerChunk chunk = (WorkerChunk) world.getChunk(chunkBuildMessage.getX(),
                        chunkBuildMessage.getZ());
                if (chunk != null) {
                    chunk.build(chunkBuildMessage.getSectionNumber(),
                            chunkBuildMessage.getBuildNumber());
                } else {
                    postMessage(WorkerMessage.create("null", null, false));
                }
                break;
            case "textures":
                TextureMap tmap = (TextureMap) message.getMessage();
                tmap.forEach(new TextureMap.Looper() {
                    @Override
                    public void forEach(String k, Texture v) {
                        textures.put(k, v);
                    }
                });
                postMessage(WorkerMessage.create("null", null, false));
                break;
            case "settings":
                clientSettings = (ClientSettings) message.getMessage();
                handleSettings();
                getBlockRegistry().init();
                postMessage(WorkerMessage.create("null", null, false));
                break;
            case "pool:free":
                TUint8Array data = (TUint8Array) message.getMessage();
                DynamicBuffer.POOL.free(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown message type: " + message.getType());
        }
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

    /**
     * Posts a message back to the creator of this worker
     *
     * @param message
     *         The message to send
     */
    public native void postMessage(Object message)/*-{
        $wnd.postMessage(message);
    }-*/;

    /**
     * Posts a message back to the creator of this worker and transfers all of the transferables
     *
     * @param message
     *         The message to send
     * @param transferables
     *         The transferables to send
     */
    public native void postMessage(Object message, Object[] transferables)/*-{
        $wnd.postMessage(message, transferables);
    }-*/;

    private native void setOnmessage(EventListener eventListener)/*-{
        self.onmessage = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(eventListener);
    }-*/;

    private native void importScripts(String script)/*-{
        importScripts(script);
    }-*/;
}
