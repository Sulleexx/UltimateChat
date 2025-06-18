package sullexxx.ultimatechat.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.configuration.file.FileConfiguration;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.configuration.LinksConfig;
import sullexxx.ultimatechat.utilities.LinkCode;

public class DiscordLink extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        if (!config.getBoolean("Discord.Link.Enable")) return;
        if (!config.getBoolean("Discord.Enable")) return;
        if (LanguageConfig.getString("DiscordCommands.ConnectCommand.Name").equals(commandName)) {
            OptionMapping nickOption = event.getOption(LanguageConfig.getString("DiscordCommands.ConnectCommand.Option-1.Nickname"));
            OptionMapping codeOption = event.getOption(LanguageConfig.getString("DiscordCommands.ConnectCommand.Option-2.Code"));

            if (nickOption != null && codeOption != null) {
                String nick = nickOption.getAsString();
                String code = codeOption.getAsString();
                if (!LinkCode.CheckCode(nick, code)) {
                    event.reply(config.getString("Discord.Link.InvalidNickOrCode")).setEphemeral(true).queue();
                    return;
                }
                event.reply(config.getString("Discord.Link.Successful").replace("{DiscordName}", event.getUser().getName()).replace("{MinecraftName}", nick)).queue();
                LinksConfig.addDataLink(nick, event.getUser().getId());
                System.out.println(event.getUser().getId());
            } else {
                event.reply(config.getString("Discord.Link.InvalidArgs")).setEphemeral(true).queue();
            }
        } else if (LanguageConfig.getString("DiscordCommands.DisconnectCommand.Name").equals(commandName)) {
            if (!config.getBoolean("Discord.UnLink.Enable")) return;
            if (LinksConfig.hasLink(event.getUser().getId())) {
                String player = LinksConfig.getUsernameById(event.getUser().getId());
                LinksConfig.removeDataLink(player);
                event.reply(config.getString("Discord.UnLink.Successful").replace("{DiscordName}", event.getUser().getName()).replace("{MinecraftName}", player)).setEphemeral(true).queue();
            } else {
                event.reply(config.getString("Discord.UnLink.NoLink")).setEphemeral(true).queue();
            }
        }
    }
}
