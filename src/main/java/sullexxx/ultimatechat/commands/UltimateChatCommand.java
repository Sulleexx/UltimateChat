package sullexxx.ultimatechat.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.configuration.PlayerSettings;

import java.util.ArrayList;
import java.util.List;

public class UltimateChatCommand implements CommandExecutor, TabCompleter {

    private final UltimateChat plugin;

    public UltimateChatCommand(UltimateChat plugin) {
        this.plugin = plugin;
        plugin.getCommand("ultimatechat").setExecutor(this);
        plugin.getCommand("ultimatechat").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LanguageConfig.getFormattedString("Global.NotPlayer"));
            return true;
        }
        if (!player.hasPermission("ultimatechat.command.main.use")) {
            player.sendMessage(LanguageConfig.getFormattedString("Global.NoPermission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand", player.getName()));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(player, args);
                break;
            case "messages":
                handleMessages(player, args);
                break;
            case "help":
                handleHelp(player);
                break;
            default:
                player.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand", player.getName()));
                break;
        }

        return true;
    }

    private void handleReload(Player player, String[] args) {
        if (args.length < 2) {
            plugin.reloadConfig();
            LanguageConfig.reload();
            player.sendMessage(LanguageConfig.getFormattedString("Global.SuccessReloadAll"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "language":
                LanguageConfig.reload();
                player.sendMessage(LanguageConfig.getFormattedString("Global.SuccessReloadLanguage"));
                break;
            case "config":
                plugin.reloadConfig();
                player.sendMessage(LanguageConfig.getFormattedString("Global.SuccessReloadConfig"));
                break;
            default:
                player.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand", player.getName()));
                break;
        }
    }

    private void handleMessages(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(LanguageConfig.getFormattedString("Global.InvalidArgs", player.getName()));
            return;
        }

        String messageType = args[1].toLowerCase();
        boolean enable = Boolean.parseBoolean(args[2]);

        switch (messageType) {
            case "join":
                PlayerSettings.settingJoin(player.getName(), enable);
                player.sendMessage(enable
                        ? LanguageConfig.getFormattedString("Messages.JoinMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.JoinMessagesOff"));
                break;
            case "leave":
                PlayerSettings.settingLeave(player.getName(), enable);
                player.sendMessage(enable
                        ? LanguageConfig.getFormattedString("Messages.LeaveMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.LeaveMessagesOff"));
                break;
            case "death":
                PlayerSettings.settingDeath(player.getName(), enable);
                player.sendMessage(enable
                        ? LanguageConfig.getFormattedString("Messages.DeathMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.DeathMessagesOff"));
                break;
            case "discord":
                PlayerSettings.settingDiscordMessages(player.getName(), enable);
                player.sendMessage(enable
                        ? LanguageConfig.getFormattedString("Messages.DiscordMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.DiscordMessagesOff"));
                break;
            default:
                player.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand", player.getName()));
                break;
        }

        plugin.saveConfig();
    }

    private void handleHelp(Player player) {
        List<Component> helpMessages = LanguageConfig.getFormattedStringList("Help.Messages");
        for (Component message : helpMessages) {
            player.sendMessage(message);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("reload");
            completions.add("messages");
            completions.add("help");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reload")) {
                completions.add("language");
                completions.add("config");
                completions.add("all");
            } else if (args[0].equalsIgnoreCase("messages")) {
                completions.add("join");
                completions.add("leave");
                completions.add("death");
                completions.add("discord");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("messages")) {
            completions.add("true");
            completions.add("false");
        }
        return completions;
    }
}
