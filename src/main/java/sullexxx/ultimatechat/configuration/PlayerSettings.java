package sullexxx.ultimatechat.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PlayerSettings {
    private final JavaPlugin plugin;
    public static FileConfiguration config;
    public static File configFile;


    public PlayerSettings(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "player-settings.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("player-settings.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void settingJoin(String player, Boolean value) {
        config.set("Players." + player + ".JoinMessages", value);
        saveConfig();
    }

    public static void settingLeave(String player, Boolean value) {
        config.set("Players." + player + ".LeaveMessages", value);
        saveConfig();
    }

    public static void settingDeath(String player, Boolean value) {
        config.set("Players." + player + ".DeathMessages", value);
        saveConfig();
    }

    public static void settingDiscordMessages(String player, Boolean value) {
        config.set("Players." + player + ".DiscordMessages", value);
        saveConfig();
    }
}

