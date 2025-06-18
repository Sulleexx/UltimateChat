package sullexxx.ultimatechat.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LinksConfig {
    private static JavaPlugin plugin;
    public static FileConfiguration config;
    private static File configFile;
    private static final String DISCORD_KEY = "Discord_ID";

    public LinksConfig(JavaPlugin plugin) {
        LinksConfig.plugin = plugin;
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
            plugin.getLogger().severe("Ошибка сохранения links-data.yml: " + e.getMessage());
        }
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void addDataLink(String player, String value) {
        config.set("Players." + player + "." + DISCORD_KEY, value);
        saveConfig();
        reloadConfig();
    }

    public static boolean hasLink(String id) {
        ConfigurationSection playersSection = config.getConfigurationSection("Players");
        if (playersSection == null) return false;

        for (String player : playersSection.getKeys(false)) {
            String discordId = playersSection.getString(player + "." + DISCORD_KEY);
            if (id.equals(discordId)) return true;
        }
        return false;
    }

    public static String getUsernameById(String id) {
        ConfigurationSection playersSection = config.getConfigurationSection("Players");
        if (playersSection == null) return null;

        for (String player : playersSection.getKeys(false)) {
            String discordId = playersSection.getString(player + "." + DISCORD_KEY);
            if (id.equals(discordId)) return player;
        }
        return null;
    }

    public static void removeDataLink(String player) {
        config.set("Players." + player + "." + DISCORD_KEY, null);

        ConfigurationSection playerSection = config.getConfigurationSection("Players." + player);
        if (playerSection != null && playerSection.getKeys(false).isEmpty()) {
            config.set("Players." + player, null);
        }
        saveConfig();
        reloadConfig();
    }
}