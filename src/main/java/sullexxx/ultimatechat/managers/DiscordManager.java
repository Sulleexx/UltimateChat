package sullexxx.ultimatechat.managers;

import net.dv8tion.jda.api.JDA;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.discord.DiscordConnect;
import sullexxx.ultimatechat.discord.DiscordConsoleListener;
import sullexxx.ultimatechat.discord.DiscordLink;
import sullexxx.ultimatechat.discord.DiscordMessagesListener;
import sullexxx.ultimatechat.utilities.DiscordBotHelper;
import sullexxx.ultimatechat.utilities.DiscordEmbedHelper;
import sullexxx.ultimatechat.utilities.DiscordWebhooks;

public class DiscordManager {
    private static DiscordConnect discordConnect;
    private static DiscordConsoleListener consoleListener;
    private static DiscordMessagesListener messagesListener;
    private static DiscordLink discordLink;

    public static void setupDiscord(JDA jda, DiscordConnect discordConnect, UltimateChat plugin) {
        DiscordManager.discordConnect = discordConnect;
        consoleListener = new DiscordConsoleListener(jda);
        messagesListener = new DiscordMessagesListener(jda, plugin);
        discordLink = new DiscordLink();
        jda.addEventListener(discordConnect, consoleListener, messagesListener, discordLink);

        new DiscordBotHelper();
        new DiscordEmbedHelper();
        new DiscordWebhooks();
    }

    public static void shutdownDiscord() {
        if (discordConnect != null) {
            discordConnect.shutdown();
        }
    }
}
