package sullexxx.ultimatechat.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import sullexxx.ultimatechat.UltimateChat;

import java.util.List;
import java.util.Objects;


public class DiscordConsoleListener extends ListenerAdapter {
    private static JDA jda;

    public DiscordConsoleListener(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!UltimateChat.getInstance().getConfig().getBoolean("Discord.Enable")) return;
        Channel channel = event.getChannel();
        TextChannel console = jda.getTextChannelById(Objects.requireNonNull(UltimateChat.getInstance().getConfig().getString("Discord.ConsoleChannelId")));
        if (channel != console) return;
        if (event.getAuthor().isBot()) return;
        List<String> blacklist = UltimateChat.getInstance().getConfig().getStringList("Discord.Console.Blacklist-commands");
        String message = event.getMessage().getContentRaw();
        if (blacklist.contains(message)) return;
        if (message.startsWith("/")){
            message = message.replaceFirst("/", "");
        }
        String finalMessage = message;
        Bukkit.getScheduler().runTask(UltimateChat.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalMessage));
    }

}