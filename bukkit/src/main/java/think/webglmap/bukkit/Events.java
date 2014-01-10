package think.webglmap.bukkit;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

@RequiredArgsConstructor
public class Events implements Listener
{

    private final WebglMapPlugin plugin;

    @EventHandler
    public void onChunkLoad( ChunkLoadEvent event )
    {
        plugin.getChunkManager().addChunk( event.getChunk().getChunkSnapshot() );
    }

    @EventHandler
    public void onChunkUnload( ChunkUnloadEvent event )
    {
        plugin.getChunkManager().removeChunk( event.getChunk().getX(), event.getChunk().getZ() );
    }
}
