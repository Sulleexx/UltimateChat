package sullexxx.ultimatechat.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.configuration.PlayerSettings;
import sullexxx.ultimatechat.discord.DiscordConnect;
import sullexxx.ultimatechat.utilities.BruhHelper;
import sullexxx.ultimatechat.utilities.LinkCheck;
import sullexxx.ultimatechat.utilities.LinkCode;

import java.util.Optional;

public class JoinListener implements Listener {


    private final Sound defaultSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        FileConfiguration settings = PlayerSettings.config;
        if (!config.getBoolean("Messages.Join.Enable")) {
            return;
        }
        if (config.getBoolean("Discord.Enable")) {
            if (config.getBoolean("Discord.Link.Enable")) {
                if (!LinkCheck.isLink(event.getPlayer().getName())){
                    String code = LinkCode.GenerateCode(event.getPlayer().getName());
                    event.getPlayer().kick(LanguageConfig.getFormatedCode("Discord.Link.NoLink", event.getPlayer().getName(), code));
                    return;
                }
                if (LinkCheck.isLink(event.getPlayer().getName())) {
                    DiscordConnect.messageSend("join", event.getPlayer().getName());
                }
            }

        }
        Player player = event.getPlayer();
        String formatMessage = config.getString("Messages.Join.Message").replace("{player}", player.getName());
        formatMessage = PlaceholderAPI.setPlaceholders(player, formatMessage);
        Component message = ChatListener.parseFormatString(formatMessage);
        Optional<Sound> sound = getJoinSound();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (settings.contains("Players." + p.getName())) {
                if (!settings.contains("Players." + p.getName() + ".JoinMessages")) {
                    p.sendMessage(message);
                    if (!sound.isEmpty()) {
                        p.playSound(p.getLocation(), sound.get(), 1, 1);
                    }
                }
                if (settings.getBoolean("Players." + p.getName() + ".JoinMessages")) {
                    p.sendMessage(message);
                    if (!sound.isEmpty()) {
                        p.playSound(p.getLocation(), sound.get(), 1, 1);
                    }
                }
                if (!settings.getBoolean("Players." + p.getName() + ".JoinMessages")) {
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

    public Optional<Sound> getJoinSound() {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        String soundName = config.getString("Messages.Join.Sound");
        if (soundName != null) {
            try {
                return Optional.of(Sound.valueOf(soundName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                BruhHelper.WrongSoundName(soundName);
                return Optional.of(defaultSound);
            }
        } else if (soundName.contains("disable")){
            BruhHelper.DisabledSound();
        }
        return Optional.empty();
    }
}
