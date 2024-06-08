package sullexxx.ultimatechat.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.discord.DiscordConnect;


public class ServerReadyListener implements Listener {

    @EventHandler
    private void onServerLoad(ServerLoadEvent event) {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        if (!config.getBoolean("Server.Start.Enable")) return;
        if (!config.getBoolean("Discord.Enable")) return;
        String message = config.getString("Server.Start.Message");
        DiscordConnect.messageSendBasic(message);
        ServerStopListener.messageSent = false;
    }
}


