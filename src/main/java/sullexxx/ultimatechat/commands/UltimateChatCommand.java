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
import sullexxx.ultimatechat.utilities.AutoMessages;

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
        if (sender instanceof Player player && !player.hasPermission("ultimatechat.command.main.use")) {
            player.sendMessage(LanguageConfig.getFormattedString("Global.NoPermission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand", sender.getName()));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender, args);
                break;
            case "messages":
                if (sender instanceof Player player) {
                    handleMessages(player, args);
                } else {
                    sender.sendMessage(LanguageConfig.getFormattedString("Global.NotPlayer"));
                    return true;
                }
                break;
            case "help":
                handleHelp(sender);
                break;
            default:
                sender.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand", sender.getName()));
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.reloadConfig();
            LanguageConfig.reload();
            sender.sendMessage(LanguageConfig.getFormattedString("Global.SuccessReloadAll"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "language":
                LanguageConfig.reload();
                sender.sendMessage(LanguageConfig.getFormattedString("Global.SuccessReloadLanguage"));
                break;
            case "config":
                plugin.reloadConfig();
                sender.sendMessage(LanguageConfig.getFormattedString("Global.SuccessReloadConfig"));
                break;
            case "all":
                plugin.reloadConfig();
                LanguageConfig.reload();
                sender.sendMessage(LanguageConfig.getFormattedString("Global.SuccessReloadAll"));
                return;
            default:
                sender.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand"));
                break;
        }
    }

    private void handleMessages(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(LanguageConfig.getFormattedString("Global.InvalidArgs"));
            return;
        }

        String messageType = args[1].toLowerCase();
        String value = args[2];

        switch (messageType) {
            case "join":
                boolean enableJoin = Boolean.parseBoolean(value);
                PlayerSettings.settingJoin(player.getName(), enableJoin);
                player.sendMessage(enableJoin
                        ? LanguageConfig.getFormattedString("Messages.JoinMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.JoinMessagesOff"));
                break;
            case "leave":
                boolean enableLeave = Boolean.parseBoolean(value);
                PlayerSettings.settingLeave(player.getName(), enableLeave);
                player.sendMessage(enableLeave
                        ? LanguageConfig.getFormattedString("Messages.LeaveMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.LeaveMessagesOff"));
                break;
            case "death":
                boolean enableDeath = Boolean.parseBoolean(value);
                PlayerSettings.settingDeath(player.getName(), enableDeath);
                player.sendMessage(enableDeath
                        ? LanguageConfig.getFormattedString("Messages.DeathMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.DeathMessagesOff"));
                break;
            case "discord":
                boolean enableDiscord = Boolean.parseBoolean(value);
                PlayerSettings.settingDiscordMessages(player.getName(), enableDiscord);
                player.sendMessage(enableDiscord
                        ? LanguageConfig.getFormattedString("Messages.DiscordMessagesOn")
                        : LanguageConfig.getFormattedString("Messages.DiscordMessagesOff"));
                break;
            case "auto_message":
                int status = AutoMessages.runOrStop(value); // -1 = failed, 0 = stopped, 1 = running

                switch (status) {
                    case 0:
                        player.sendMessage(LanguageConfig.getFormattedString("Messages.AutoMessageOff"));
                        PlayerSettings.settingAutoMessage(player.getName(), value, false);
                        break;
                    case 1:
                        player.sendMessage(LanguageConfig.getFormattedString("Messages.AutoMessageOn"));
                        PlayerSettings.settingAutoMessage(player.getName(), value, true);
                        break;
                    case -1:
                        player.sendMessage(LanguageConfig.getFormattedString("Messages.AutoMessageFailed"));
                        break;
                }
                break;
            default:
                player.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand"));
                break;
        }

        plugin.saveConfig();
    }

    private void handleHelp(CommandSender player) {
        List<Component> helpMessages = LanguageConfig.getFormattedStringList("Help");
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
                completions.add("auto_message");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("messages")) {
                if (args[1].equalsIgnoreCase("auto_message")) {
                    return AutoMessages.runnables.keySet().stream().toList();
                }
                completions.add("true");
                completions.add("false");
            }
        }
        return completions;
    }
}