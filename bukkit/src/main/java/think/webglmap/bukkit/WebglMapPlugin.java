package think.webglmap.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import think.webglmap.bukkit.web.WebHandler;

public class WebglMapPlugin extends JavaPlugin {

    private WebHandler webHandler;

    @Override
    public void onEnable() {
        webHandler = new WebHandler(this);
        webHandler.start();

        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        webHandler.interrupt();
    }
}
