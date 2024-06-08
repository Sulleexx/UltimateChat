package sullexxx.ultimatechat.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.configuration.file.FileConfiguration;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.utilities.DiscordBotHelper;
import sullexxx.ultimatechat.utilities.DiscordEmbedHelper;
import sullexxx.ultimatechat.utilities.DiscordWebhooks;

import java.awt.*;

public class DiscordConnect extends ListenerAdapter {
    private static FileConfiguration config;
    public static String webhookUrl;
    public static JDA jda;

    public DiscordConnect() {
        System.out.println("DiscordConnect class initialization");

        config = UltimateChat.getInstance().getConfig();
        if (!isDiscordEnabled()) return;

        try {
            JDABuilder builder = setupJDA();
            jda = builder.build();
            jda.updateCommands().addCommands(
                    Commands.slash("привязка", "Привязать аккаунт к майнкрафт серверу")
                            .addOption(OptionType.STRING, "ник", "Ваш ник", true)
                            .addOption(OptionType.STRING, "код", "Ваш код", true),
                    Commands.slash("убрать_привязку", "Убрать привязку от аккаунта")
            ).queue();

            this.webhookUrl = DiscordWebhooks.createWebhook(config.getString("Discord.ChannelId"), "UltimateChatWebhook", config.getString("Discord.Token"));
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public static void messageSendBasic(String message) {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        if (jda == null) return;
        if (config.getString("Discord.ChannelId") == null) return;
        if (!config.getBoolean("Discord.Enable")) return;
        TextChannel channel = jda.getTextChannelById(config.getString("Discord.ChannelId"));
        channel.sendMessage(message).queue();
    }

    public static void messageSend(String option, String player) {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        if (jda == null) return;
        if (config.getString("Discord.ChannelId") == null) return;
        if (!config.getBoolean("Discord.Enable")) return;

        TextChannel channel = jda.getTextChannelById(config.getString("Discord.ChannelId"));
        String avatarUrl = "https://cravatar.eu/helmavatar/" + player + "/55.png";
        EmbedBuilder eb = new EmbedBuilder();
        String path = "";

        switch (option.toLowerCase()) {
            case "join":
                path = "Messages.Join.Discord";
                break;
            case "die":
                path = "Messages.Death.Discord";
                break;
            case "leave":
                path = "Messages.Leave.Discord";
                break;
            default:
                throw new IllegalArgumentException("Invalid option: " + option);
        }

        Color color = DiscordEmbedHelper.getColorFromConfig(config, path + ".Color");
        String description = DiscordEmbedHelper.getDescription(config, path + ".Text", player);

        String discordFormat = config.getString("Messages.DiscordFormat");
        if (discordFormat != null) {
            switch (discordFormat) {
                case "1":
                    eb.setTitle(" ");
                    eb.setDescription(description);
                    eb.setThumbnail(avatarUrl);
                    break;
                case "2":
                    eb.setAuthor(description, null, avatarUrl);
                    break;
                default:
                    eb.setTitle(" ");
                    eb.setDescription(description);
                    eb.setThumbnail(avatarUrl);
                    break;
            }
        }

        DiscordEmbedHelper.addColor(eb, color);
        MessageEmbed messageEmbed = eb.build();

        DiscordEmbedHelper.sendEmbed(channel, messageEmbed);
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot()) return;

        String message = event.getMessage().getContentRaw();
        if (message.equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
            if (webhookUrl != null) {
                DiscordWebhooks.sendWebhookMessage(webhookUrl, "Ping command received!", user.getName());
            }
        }
    }

    public static boolean isDiscordEnabled() {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        String enable = config.getString("Discord.Enable", "false");
        return enable.equalsIgnoreCase("true");
    }

    private JDABuilder setupJDA() {
        JDABuilder builder = UltimateChat.getInstance().getBuilder();

        String activityText = config.getString("Discord.ActivityText", " | ");
        String activityType = config.getString("Discord.ActivityType", "playing");
        String status = config.getString("Discord.BotStatus", "online");

        builder.setStatus(DiscordBotHelper.getOnlineStatus(status));
        builder.setActivity(DiscordBotHelper.getActivity(activityType, activityText));

        return builder;
    }

    public void shutdown() {
        DiscordWebhooks.deleteWebhook(webhookUrl, config.getString("Discord.Token"));
        System.out.println("Бот выключен и вебхук удален");
    }
}
