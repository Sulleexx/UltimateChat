package sullexxx.ultimatechat.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LanguageConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FunCommands implements CommandExecutor, TabCompleter {

    private final UltimateChat plugin;
    private final Random random = new Random();

    public FunCommands(UltimateChat plugin) {
        this.plugin = plugin;
        plugin.getCommand("ball").setExecutor(this);
        plugin.getCommand("try").setExecutor(this);
        plugin.getCommand("flip").setExecutor(this);
        plugin.getCommand("dice").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LanguageConfig.getFormattedString("Global.NotPlayer"));
            return true;
        }

        if (!player.hasPermission("ultimatechat.funcommands." + command.getName())) {
            player.sendMessage(LanguageConfig.getFormattedString("Global.NoPermission"));
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "ball":
                handleBall(player, args);
                break;
            case "try":
                handleTry(player, args);
                break;
            case "flip":
                handleFlip(player);
                break;
            case "dice":
                handleDice(player);
                break;
            default:
                player.sendMessage(LanguageConfig.getFormattedString("Global.UnknownCommand"));
        }
        return true;
    }

    private void handleBall(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(LanguageConfig.getFormattedString("Ball.Usage"));
            return;
        }

        List<String> answers = Arrays.asList(
                LanguageConfig.getString("Ball.Answer1"),
                LanguageConfig.getString("Ball.Answer2"),
                LanguageConfig.getString("Ball.Answer3"),
                LanguageConfig.getString("Ball.Answer4"),
                LanguageConfig.getString("Ball.Answer5")
        );

        String answer = answers.get(random.nextInt(answers.size()));
        String question = String.join(" ", args);
        player.sendMessage(LanguageConfig.getFormattedStringForBall("Ball.Format", answer, question));
    }

    private void handleTry(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(LanguageConfig.getFormattedString("Try.Usage"));
            return;
        }

        String action = String.join(" ", args);
        boolean success = random.nextBoolean();

        Component resultMessage = success ?
                LanguageConfig.getFormattedStringForFun("Try.Success", player.getName(), action) :
                LanguageConfig.getFormattedStringForFun("Try.Fail", player.getName(), action);

        player.sendMessage(resultMessage);
    }

    private void handleFlip(Player player) {
        String result = random.nextBoolean() ?
                LanguageConfig.getString("Flip.Heads") :
                LanguageConfig.getString("Flip.Tails");

        player.sendMessage(LanguageConfig.getFormattedStringForFun("Flip.Format", player.getName(), result));
    }

    private void handleDice(Player player) {
        int diceValue = random.nextInt(6) + 1;
        player.sendMessage(LanguageConfig.getFormattedStringForFun("Dice.Format", player.getName(), String.valueOf(diceValue)));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}