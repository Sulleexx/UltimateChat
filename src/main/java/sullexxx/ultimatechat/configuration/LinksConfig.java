package sullexxx.ultimatechat.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LinksConfig {
    private final JavaPlugin plugin;
    public static FileConfiguration config;
    public static File configFile;


    public LinksConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "links-data.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("links-data.yml", false);
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

    public static void addDataLink(String player, String value) {
        config.set("Players." + player + ".Discord_ID", value);
        saveConfig();
    }

    public static boolean hasLink(String id) {
        ConfigurationSection playersSection = config.getConfigurationSection("Players");
        if (playersSection != null) {
            for (String key : playersSection.getKeys(false)) {
                String discordId = playersSection.getString(key + ".Discord_ID");
                if (discordId != null && discordId.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getUsernameById(String id) {
        ConfigurationSection playersSection = config.getConfigurationSection("Players");
        if (playersSection != null) {
            for (String key : playersSection.getKeys(false)) {
                String discordId = playersSection.getString(key + ".Discord_ID");
                if (discordId != null && discordId.equals(id)) {
                    return key;
                }
            }
        }
        return null;
    }


    public static void removeDataLink(String player) {
        config.set("Players." + player + ".Discord_ID", null);
        saveConfig();
    }
}

