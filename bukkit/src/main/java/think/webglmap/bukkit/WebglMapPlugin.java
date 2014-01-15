package think.webglmap.bukkit;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import think.webglmap.bukkit.web.WebHandler;

public class WebglMapPlugin extends JavaPlugin {

    /**
     * Returns the plugin's web handler
     *
     * @returns the web handler
     */
    @Getter
    private WebHandler webHandler;
    @Getter
    private ChunkManager chunkManager;

    @Override
    public void onEnable() {
        chunkManager = new ChunkManager(this);

        webHandler = new WebHandler(this);
        webHandler.start();

        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        webHandler.interrupt();
        chunkManager.cleanup();
    }
}
