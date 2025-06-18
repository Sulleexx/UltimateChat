package sullexxx.ultimatechat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import sullexxx.ultimatechat.discord.DiscordConnect;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.utilities.BlackListWords;
import sullexxx.ultimatechat.utilities.DiscordWebhooks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {
    private final LuckPerms api;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public ChatListener() {
        RegisteredServiceProvider<LuckPerms> provider = UltimateChat.getInstance().getServer().getServicesManager().getRegistration(LuckPerms.class);
        api = (provider != null) ? provider.getProvider() : null;
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        FileConfiguration config = UltimateChat.getInstance().getConfig();

        List<String> blackListWords = BlackListWords.blacklistWords;
        String lowerCaseMessage = plainMessage.toLowerCase();

        for (String word : blackListWords) {
            if (lowerCaseMessage.contains(word.toLowerCase())) {
                player.sendMessage(LanguageConfig.getFormattedStringE("Chats.BlacklistedWord", word));
                event.setCancelled(true);
                return;
            }
        }

        Map<String, String> chatSymbols = new HashMap<>();
        config.getConfigurationSection("General.Chats").getKeys(false).forEach(chatSection ->
                chatSymbols.put(chatSection, config.getString("General.Chats." + chatSection + ".Symbol", ""))
        );

        UserManager userManager = api.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        String prefix = (user != null) ? user.getCachedData().getMetaData().getPrefix() : "";
        String suffix = (user != null) ? user.getCachedData().getMetaData().getSuffix() : "";

        prefix = (prefix != null) ? prefix : "";
        suffix = (suffix != null) ? suffix : "";

        for (String chatSection : chatSymbols.keySet()) {
            ConfigurationSection chatConfig = config.getConfigurationSection("General.Chats." + chatSection);
            String symbol = chatSymbols.get(chatSection);

            if (symbol != null && !symbol.isBlank() && plainMessage.startsWith(symbol)) {
                String cleanedMessage = plainMessage.substring(symbol.length()).trim();
                handleChat(event, player, cleanedMessage, chatConfig, prefix, suffix, plainMessage);
                return;
            }
        }

        handleLocalChat(event, player, plainMessage, config, prefix, suffix);
    }

    private void handleChat(AsyncChatEvent event, Player player, String cleanedMessage, ConfigurationSection chatConfig, String prefix, String suffix, String rawMessage) {
        UUID playerId = player.getUniqueId();
        String permission = chatConfig.getString("Permission");

        if (permission != null && !"none".equalsIgnoreCase(permission.trim()) && !player.hasPermission(permission.trim())) {
            player.sendMessage(LanguageConfig.getFormattedString("Chats.NoPermissionToChat"));
            event.setCancelled(true);
            return;
        }

        if (isInCooldown(playerId, chatConfig)) {
            player.sendMessage(LanguageConfig.getFormattedString("Chats.CooldownMessage"));
            event.setCancelled(true);
            return;
        }

        cooldowns.put(playerId, System.currentTimeMillis());

        String formattedMessage = formatMessage(chatConfig.getString("Format"), prefix, suffix, player.getName(), cleanedMessage);
        Component formatComponent = parseFormatString(formattedMessage);
        int range = chatConfig.getInt("Range");


        String enabled = chatConfig.getString("Enable");
        if (enabled.equalsIgnoreCase("true")) {
            sendMessage(event, player, formatComponent, range, cleanedMessage, permission);
        } else {
            player.sendMessage(LanguageConfig.getFormattedString("Chats.DisabledChat"));
        }
    }

    private boolean isInCooldown(UUID playerId, ConfigurationSection chatConfig) {
        if (cooldowns.containsKey(playerId)) {
            long secondsSinceLastMessage = (System.currentTimeMillis() - cooldowns.get(playerId)) / 1000;
            long cooldownTime = chatConfig.getLong("Cooldown", -1);
            return cooldownTime != -1 && secondsSinceLastMessage < cooldownTime;
        }
        return false;
    }

    private void handleLocalChat(AsyncChatEvent event, Player player, String plainMessage, FileConfiguration config, String prefix, String suffix) {
        ConfigurationSection localChatConfig = config.getConfigurationSection("General.Chats.Local");
        String formattedMessage = formatMessage(localChatConfig.getString("Format"), prefix, suffix, player.getName(), plainMessage);
        Component formatComponent = parseFormatString(formattedMessage);
        int localRange = localChatConfig.getInt("Range");
        String permission = localChatConfig.getString("Permission");


        event.setCancelled(true);
        String enabled = localChatConfig.getString("Enable");
        if (enabled.equalsIgnoreCase("true")) {
            sendMessageToRange(player, formatComponent, localRange, permission);
        } else {
            player.sendMessage(LanguageConfig.getFormattedString("Chats.DisabledChat"));
        }
    }

    private String formatMessage(String format, String prefix, String suffix, String playerName, String message) {
        return PlaceholderAPI.setPlaceholders(
                null,
                format.replace("{prefix}", prefix).replace("{suffix}", suffix).replace("{player}", playerName).replace("{message}", message)
        );
    }

    private void sendMessage(AsyncChatEvent event, Player player, Component formattedMessage, int range, String cleanedMessage, String permission) {
        event.setCancelled(true);
        if (range == -2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!sendResult(p, permission, formattedMessage)) return;
            }

            String origName = UltimateChat.getInstance().getConfig().getString("Discord.Sync.FromMinecraftToDiscord.Webhook.Name");
            String webhookName = origName.replace("{PlayerName}", player.getName());
            webhookName = PlaceholderAPI.setPlaceholders(player, webhookName);
            webhookName = PlaceholderAPI.setBracketPlaceholders(player, webhookName);

            String message = UltimateChat.getInstance().getConfig().getString("Discord.Sync.FromMinecraftToDiscord.Webhook.Message").replace("{message}", cleanedMessage);
            DiscordWebhooks.sendWebhookMessageE(DiscordConnect.webhookUrl, message, webhookName, player.getName());
        } else if (range == -1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendResult(p, permission, formattedMessage);
            }
        } else {
            sendMessageToRange(player, formattedMessage, range, permission);
        }
    }

    private Boolean sendResult(Player p, String permission, Component formattedMessage) {
        if (permission == null || permission.isEmpty() || permission.equalsIgnoreCase("none") || p.hasPermission(permission)) {
            p.sendMessage(formattedMessage);
            return true;
        } else {
            p.sendMessage(LanguageConfig.getFormattedString("Chats.NoPermissionToChat"));
            return false;
        }
    }

    private void sendMessageToRange(Player player, Component formattedMessage, int range, String permission) {
        int recipients = 0;
        if (!permission.equalsIgnoreCase("none")) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (p.getWorld().equals(player.getWorld()) && p.getLocation().distance(player.getLocation()) <= range && p.hasPermission(permission)) {
                    p.sendMessage(formattedMessage);
                    recipients++;
                }
            }
            if (recipients == 1) {
                player.sendMessage(LanguageConfig.getFormattedString("Chats.NotInRadius"));
            }
        }
        if (permission.equalsIgnoreCase("none")) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (p.getWorld().equals(player.getWorld()) && p.getLocation().distance(player.getLocation()) <= range ) {
                    p.sendMessage(formattedMessage);
                    recipients++;
                }
            }
            if (recipients == 1) {
                player.sendMessage(LanguageConfig.getFormattedString("Chats.NotInRadius"));
            }
        }
    }


    @NotNull
    public static Component parseFormatString(@NotNull String message) {
        message = message.replace('§', '&');
        Component component = MiniMessage.miniMessage().deserialize(message).decoration(TextDecoration.ITALIC, false);
        String legacyMessage = LanguageConfig.toLegacy(component);
        legacyMessage = ChatColor.translateAlternateColorCodes('&', legacyMessage);
        return LanguageConfig.unusualHexSerializer.deserialize(legacyMessage);
    }
}
