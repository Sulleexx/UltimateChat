package sullexxx.ultimatechat.utilities;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import sullexxx.ultimatechat.UltimateChat;
import net.kyori.adventure.text.Component;
import sullexxx.ultimatechat.configuration.LanguageConfig;

import java.util.HashMap;
import java.util.Map;

public class AutoMessages {
    private static final FileConfiguration config = UltimateChat.getInstance().getConfig();

    public static Map<String, BukkitRunnable> runnables = new HashMap<>();
    public static Map<String, Boolean> taskStates = new HashMap<>();
    public static Map<String, String> idToConfigKey = new HashMap<>();

    static {
        initializeTasks();
    }

    private static void initializeTasks() {
        for (String message : config.getConfigurationSection("Messages.AutoMessages").getKeys(false)) {
            var id = message.toLowerCase().replaceAll(" ", "_");
            idToConfigKey.put(id, message);

            var runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    var raw = LanguageConfig.getString("Messages.AutoMessages." + message + ".Text");

                    if (raw.equals("undefined") || raw.isBlank()) {
                        return;
                    }

                    Component text = LanguageConfig.getFormattedStringG("Messages.AutoMessages." + message + ".Text");

                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(text));
                }
            };

            runnables.put(id, runnable);
            taskStates.put(id, true);

            runnable.runTaskTimer(UltimateChat.getInstance(), 0, retrieveCooldown(message) / 50);
        }
    }

    public static int runOrStop(String id) {
        id = id.toLowerCase().replaceAll(" ", "_");
        if (!taskStates.containsKey(id)) return -1;

        boolean state = taskStates.get(id);
        BukkitRunnable runnable = runnables.get(id);

        if (runnable == null) {
            throw new IllegalArgumentException("Unable to run/stop task: '" + id + "' does not exist!");
        }

        if (state) {
            runnable.cancel();
            taskStates.put(id, false);
            return 0;
        } else {
            String configKey = idToConfigKey.get(id);
            if (configKey == null) {
                throw new IllegalStateException("Original config key not found for: " + id);
            }

            var newRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    Component text = LanguageConfig.getFormattedStringG("Messages.AutoMessages." + configKey + ".Text");
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(text));
                }
            };

            newRunnable.runTaskTimer(
                    UltimateChat.getInstance(),
                    0,
                    retrieveCooldown(configKey) / 50
            );

            runnables.put(id, newRunnable);
            taskStates.put(id, true);
            return 1;
        }
    }

    private static Long retrieveCooldown(String messageId) {
        return TimeParsing.timeParser(config.getString("Messages.AutoMessages." + messageId + ".Interval"));
    }
}