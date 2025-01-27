package sullexxx.ultimatechat.utilities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import sullexxx.ultimatechat.configuration.LinksConfig;

public class GetLinkedPlayer {
    public static String getLinkPlayer(String playerName) {
        FileConfiguration config = LinksConfig.config;
        ConfigurationSection playersSection = config.getConfigurationSection("Players");
        if (playersSection != null && playersSection.contains(playerName)) {
            String discordId = playersSection.getString(playerName + ".Discord_ID");
            if (discordId == null) {
                discordId = playersSection.getString(playerName + ".Discord-ID");
            }
            if (discordId != null && !discordId.equals("0")) {
                return discordId;
            }
        }
        return "notFoundLOL";
    }
}