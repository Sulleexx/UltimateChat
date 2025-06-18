package sullexxx.ultimatechat.utilities;

import org.bukkit.configuration.file.FileConfiguration;
import sullexxx.ultimatechat.UltimateChat;

import java.util.List;

public class BlackListWords {
    private static final FileConfiguration config = UltimateChat.getInstance().getConfig();
    public static final List<String> blacklistWords = config.getStringList("Messages.WordsBlackList");
}
