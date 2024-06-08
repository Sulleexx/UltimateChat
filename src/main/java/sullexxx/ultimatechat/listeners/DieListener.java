package sullexxx.ultimatechat.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.PlayerSettings;
import sullexxx.ultimatechat.discord.DiscordConnect;

import java.util.Optional;

public class DieListener implements Listener {

    private final Sound defaultSound = Sound.ENTITY_BAT_DEATH;


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        FileConfiguration settings = PlayerSettings.config;
        if (!config.getBoolean("Messages.Death.Enable")) {
            return;
        }
        if (config.getBoolean("Discord.Enable")) {
            DiscordConnect.messageSend("die", event.getPlayer().getName());
        }
        Player player = event.getEntity();
        String causeOfDeath = event.getDeathMessage() != null ? event.getDeathMessage() : "неизвестно?";
        String formatMessage = config.getString("Messages.Death.Message")
                .replace("{player}", player.getName())
                .replace("{cause}", causeOfDeath);
        formatMessage = PlaceholderAPI.setPlaceholders(player, formatMessage);
        Component message = ChatListener.parseFormatString(formatMessage);
        Optional<Sound> sound = getDeathSound();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (settings.contains("Players." + p.getName())) {
                if (!settings.contains("Players." + p.getName() + ".DeathMessages")) {
                    p.sendMessage(message);
                    if (!sound.isEmpty()) {
                        p.playSound(p.getLocation(), sound.get(), 1, 1);
                    }
                }
                if (settings.getBoolean("Players." + p.getName() + ".DeathMessages")) {
                    p.sendMessage(message);
                    if (!sound.isEmpty()) {
                        p.playSound(p.getLocation(), sound.get(), 1, 1);
                    }
                }
                if (!settings.getBoolean("Players." + p.getName() + ".DeathMessages")) {
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

    public Optional<Sound> getDeathSound() {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        String soundName = config.getString("Messages.Death.Sound");
        if (soundName != null) {
            try {
                return Optional.of(Sound.valueOf(soundName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                UltimateChat.getInstance().getLogger().warning("Не правильное название звука в config.yml: " + soundName + ". Используется обычный звук.");
                return Optional.of(defaultSound);
            }
        } else if (soundName.contains("disable")){
            UltimateChat.getInstance().getLogger().warning("Звук отключен в: config.yml. Звука не будет");
        }
        return Optional.empty();
    }
}
