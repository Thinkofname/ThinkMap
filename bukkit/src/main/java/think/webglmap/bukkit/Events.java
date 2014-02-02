package think.webglmap.bukkit;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

@RequiredArgsConstructor
public class Events implements Listener {

    private final WebglMapPlugin plugin;

    /**
     * Used to mark the chunk as an active chunk and load it into memory
     *
     * @param event the event
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
//        plugin.getChunkManager(event.getWorld()).addChunk(event.getChunk().getChunkSnapshot());
    }

    /**
     * Used to mark the chunk as inactive and save it
     *
     * @param event the event
     */
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
//        plugin.getChunkManager(event.getWorld()).removeChunk(event.getChunk().getX(), event.getChunk().getZ());
    }
}
