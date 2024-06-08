package sullexxx.ultimatechat.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import sullexxx.ultimatechat.UltimateChat;

public class DiscordPrivateMessages {



    public static void sendMessage(String playerName, String message, String sender) {
        if (!UltimateChat.getInstance().getConfig().getBoolean("Discord.Enable")) return;
    }

}
