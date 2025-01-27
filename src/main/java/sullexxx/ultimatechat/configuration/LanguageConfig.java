package sullexxx.ultimatechat.configuration;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import sullexxx.ultimatechat.UltimateChat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("CallToPrintStackTrace")
public class LanguageConfig {
    private static final File file;
    private static YamlConfiguration config;
    public static final LegacyComponentSerializer unusualHexSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();


    static {
        file = new File(UltimateChat.getInstance().getDataFolder(), "language.yml");
        if (!file.exists()) {
            UltimateChat.getInstance().saveResource("language.yml", false);
        }
        try {
            config = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            try {
                Files.copy(UltimateChat.getInstance().getResource("language.yml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                config = YamlConfiguration.loadConfiguration(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Component getFormattedString(String path) {
        String rawString = config.getString(path, "undefined");
        return doubleFormat(rawString);
    }

    public static Component getFormattedString(String path, String player) {
        String rawString = config.getString(path, "undefined")
                .replace("{player}", player);
        return doubleFormat(rawString);
    }

    public static Component getFormatedCode(String path, String player, String code) {
        FileConfiguration confige = UltimateChat.getInstance().getConfig();
        String rawString = confige.getString(path, "undefined")
                .replace("{player}", player)
                .replace("{code}", code);
        return doubleFormat(rawString);
    }


    public static Component getFormattedString(String path, String player, String target) {
        String rawString = config.getString(path, "undefined")
                .replace("{player}", player)
                .replace("{target}", target);
        return doubleFormat(rawString);
    }

    public static Component getFormattedString(String path, String player, String target, String message) {
        FileConfiguration confige = UltimateChat.getInstance().getConfig();
        String rawString = confige.getString(path, "undefined")
                .replace("{sender}", player)
                .replace("{receiver}", target)
                .replace("{message}", message);
        return doubleFormat(rawString);
    }

    public static YamlConfiguration config() {
        return config;
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void reload() {
        try {
            config = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Component> getFormattedStringList(String path) {
        List<String> rawStrings = config.getStringList(path);
        return rawStrings.stream()
                .map(LegacyComponentSerializer.legacyAmpersand()::deserialize)
                .collect(Collectors.toList());
    }

    @NotNull
    public static Component doubleFormat(@NotNull String message) {
        message = message.replace('§', '&');
        Component component = MiniMessage.miniMessage().deserialize(message).decoration(TextDecoration.ITALIC, false);
        String legacyMessage = toLegacy(component);
        legacyMessage = ChatColor.translateAlternateColorCodes('&', legacyMessage);
        return unusualHexSerializer.deserialize(legacyMessage);
    }

    public static String toLegacy(Component component) {
        return unusualHexSerializer.serialize(component);
    }

}
