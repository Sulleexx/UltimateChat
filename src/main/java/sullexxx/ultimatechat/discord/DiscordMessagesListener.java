package sullexxx.ultimatechat.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.PlayerSettings;
import sullexxx.ultimatechat.listeners.ChatListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscordMessagesListener extends ListenerAdapter {
    private static JDA jda;
    private UltimateChat plugin;
    private List<String> banwords = new ArrayList<>();
    private final Sound defaultSound = Sound.ENTITY_ALLAY_DEATH;

    public DiscordMessagesListener(JDA jda, UltimateChat plugin) {
        this.jda = jda;
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        FileConfiguration settings = PlayerSettings.config;
        if (!config.getBoolean("Discord.Sync.Discord-to-minecraft")) return;
        if (!config.getBoolean("Discord.Enable")) return;

        TextChannel channel = jda.getTextChannelById(config.getString("Discord.ChannelId"));
        if (channel == null || !event.getChannel().equals(channel) || event.getAuthor().isBot()) return;

        String messageContent = event.getMessage().getContentRaw();
        if (messageContent.length() > 250) {
            Emoji emoji = Emoji.fromUnicode("\u274C");
            event.getMessage().addReaction(emoji).queue();
            return;
        }

        banwords = config.getStringList("Discord.Sync.BanWords");
        for (String banword : banwords) {
            if (messageContent.toLowerCase().contains(banword.toLowerCase())) {
                event.getMessage().delete().queue();
                return;
            }
        }
        Optional<Sound> sound = getDiscordMessageSound();
        String format = config.getString("Discord.Sync.FromDiscordToMinecraft").replace("{user}", event.getAuthor().getName()).replace("{message}", messageContent);
        if (format.contains("{role}")) {
            Role highestRole = event.getMember().getRoles().get(0);
            if (highestRole == null) return;
            String roleName = highestRole.getName();
            format = format.replace("{role}", roleName);
        }
        Component message = ChatListener.parseFormatString(format);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (settings.contains("Players." + p.getName())) {
                if (!settings.contains("Players." + p.getName() + ".DiscordMessages")) {
                    p.sendMessage(message);
                    if (!sound.isEmpty()) {
                        p.playSound(p.getLocation(), sound.get(), 1, 1);
                    }
                }
                if (settings.getBoolean("Players." + p.getName() + ".DiscordMessages")) {
                    p.sendMessage(message);
                    if (!sound.isEmpty()) {
                        p.playSound(p.getLocation(), sound.get(), 1, 1);
                    }
                }
                if (!settings.getBoolean("Players." + p.getName() + ".DiscordMessages")) {
                    return;
                }
            } else {
                p.sendMessage(message);
                if (!sound.isEmpty()) {
                    p.playSound(p.getLocation(), sound.get(), 1, 1);
                }
            }
        }
    }
    public Optional<Sound> getDiscordMessageSound() {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        String soundName = config.getString("Discord.Sync.FromDiscordToMinecraft_Sound");
        if (soundName != null) {
            try {
                return Optional.of(Sound.valueOf(soundName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Не правильное название звука в config.yml: " + soundName + ". Используется обычный звук.");
                return Optional.of(defaultSound);
            }
        } else if (soundName.contains("disable")){
            plugin.getLogger().warning("Звук отключен в: config.yml. Звука не будет");
        }
        return Optional.empty();
    }
}
