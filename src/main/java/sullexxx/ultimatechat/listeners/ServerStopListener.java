package sullexxx.ultimatechat.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerLoadEvent;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.discord.DiscordConnect;

public class ServerStopListener implements Listener {

    public static boolean messageSent = false;

    @EventHandler
    public void onServerStop(PluginDisableEvent event) {
        if (messageSent) {
            return;
        }

        FileConfiguration config = UltimateChat.getInstance().getConfig();
        if (!config.getBoolean("Server.Stop.Enable")) return;
        if (!config.getBoolean("Discord.Enable")) return;
        String message = config.getString("Server.Stop.Message");
        DiscordConnect.messageSendBasic(message);
        messageSent = true;
    }

}
