package sullexxx.ultimatechat.managers;

import org.bukkit.plugin.PluginManager;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.features.EntityInfoDisplay;
import sullexxx.ultimatechat.listeners.*;

public class PluginEventManager {

    public static void registerEvents(UltimateChat plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvents(new ChatListener(), plugin);
        pm.registerEvents(new JoinListener(), plugin);
        pm.registerEvents(new LeaveListener(), plugin);
        pm.registerEvents(new DieListener(), plugin);
        pm.registerEvents(new ServerReadyListener(), plugin);
        pm.registerEvents(new ServerStopListener(), plugin);
        pm.registerEvents(new EntityInfoDisplay(), plugin);
    }
}
