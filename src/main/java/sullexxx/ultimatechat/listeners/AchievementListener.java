package sullexxx.ultimatechat.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.PlayerSettings;
import sullexxx.ultimatechat.utilities.BruhHelper;

import java.util.Optional;


public class AchievementListener implements Listener {

    UltimateChat plugin;
    private final Sound defaultSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;


    @EventHandler
    public void OnAdvancementDone(PlayerAdvancementDoneEvent event) {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        FileConfiguration settings = PlayerSettings.config;
        if (!config.getBoolean("Messages.Leave.Enable")) {
            return;
        }
        Player player = event.getPlayer();
        String formatMessage = config.getString("Messages.Leave.Message").replace("{player}", player.getName());
        formatMessage = PlaceholderAPI.setPlaceholders(player, formatMessage);
        Component message = ChatListener.parseFormatString(formatMessage);
        Optional<Sound> sound = getLeaveSound();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (settings.contains("Players." + p.getName())) {
                if (settings.getBoolean("Players." + p.getName() + ".LeaveMessages")) {
                    p.sendMessage(message);
                    if (!sound.isEmpty()) {
                        p.playSound(p.getLocation(), sound.get(), 1, 1);
                    }
                }
                if (!settings.getBoolean("Players." + p.getName() + ".LeaveMessages")) {
                    return;
                }
            }
        }
    }

    public Optional<Sound> getLeaveSound() {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        String soundName = config.getString("Messages.Leave.Sound");
        if (soundName != null) {
            try {
                return Optional.of(Sound.valueOf(soundName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                BruhHelper.WrongSoundName(soundName);
                return Optional.of(defaultSound);
            }
        } else {
            BruhHelper.NoSelectedSound();
        }
        return Optional.empty();
    }
}
