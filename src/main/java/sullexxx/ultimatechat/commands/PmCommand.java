package sullexxx.ultimatechat.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.discord.DiscordPrivateMessages;

import java.util.ArrayList;
import java.util.List;

public class PmCommand implements CommandExecutor, TabCompleter {

    private final UltimateChat plugin;

    public PmCommand(UltimateChat plugin) {
        this.plugin = plugin;
        plugin.getCommand("msg").setExecutor(this);
        plugin.getCommand("msg").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LanguageConfig.getFormattedString("Global.NotPlayer"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(LanguageConfig.getFormattedString("Global.InvalidArgs", player.getName()));
            return true;
        }

        String targetName = args[0];
        String message = String.join(" ", args).substring(targetName.length()).trim();

        Player target = Bukkit.getPlayer(targetName);
        if (!UltimateChat.getInstance().getConfig().getBoolean("Commands.Pm.Enable")) sender.sendMessage(LanguageConfig.getFormattedString("Pm.PmDisabledMinecraft"));

        if (target != null && target.isOnline()) {
            sendPrivateMessage(player, target, message);
        } else {
            handleOfflinePlayer(player, targetName, message);
        }

        return true;
    }

    private void sendPrivateMessage(Player sender, Player target, String message) {
        Component formattedMessage = LanguageConfig.getFormattedString("Commands.Pm.Format", sender.getName(), target.getName(), message);
        sender.sendMessage(formattedMessage);
        target.sendMessage(formattedMessage);

        String soundName = UltimateChat.getInstance().getConfig().getString("Sound", "disable").toUpperCase();

        if (!soundName.equalsIgnoreCase("disable")) {
            try {
                Sound sound = Sound.valueOf(soundName);
                target.playSound(target.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                UltimateChat.getInstance().getLogger().warning("Sound " + soundName + " not found. Check configuration.");
            }
        }
    }

    public static void sendPrivateMessageFromDiscord(String sender, Player target, String message, String targetName) {
        Component formattedMessage = LanguageConfig.getFormattedString("Commands.Pm.FormatDiscord", sender, targetName, message);
        target.sendMessage(formattedMessage);

        String soundName = UltimateChat.getInstance().getConfig().getString("Sound", "disable").toUpperCase();

        if (!soundName.equalsIgnoreCase("disable")) {
            try {
                Sound sound = Sound.valueOf(soundName);
                target.playSound(target.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                UltimateChat.getInstance().getLogger().warning("Sound " + soundName + " not found. Check configuration.");
            }
        }
    }

    public static boolean handleOfflinePlayerFromDiscord(String sender, String targetName, String message) {
        boolean isSent = DiscordPrivateMessages.sendPrivateMessage(targetName, message, sender);

        if (isSent) {
            return true;
        } else {
            return false;
        }
    }

    public static void handleOfflinePlayer(Player sender, String targetName, String message) {
        if (!UltimateChat.getInstance().getConfig().getBoolean("Discord.Enable")) {
            sender.sendMessage(LanguageConfig.getFormattedString("Pm.DiscordConnect"));
            return;
        }

        boolean isSent = DiscordPrivateMessages.sendPrivateMessage(targetName, message, sender.getName());

        if (isSent) {
            sender.sendMessage(LanguageConfig.getFormattedString("Pm.DiscordSuccess"));
        } else {
            sender.sendMessage(LanguageConfig.getFormattedString("Pm.DiscordFailed"));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 2) {
            completions.add("<message>");
        }

        return completions;
    }
}