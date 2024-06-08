package sullexxx.ultimatechat.utilities;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;

public class DiscordEmbedHelper {

    public static Color getColorFromConfig(FileConfiguration config, String path) {
        try {
            return Color.decode(config.getString(path));
        } catch (NumberFormatException e) {
            return Color.ORANGE;
        }
    }

    public static String getDescription(FileConfiguration config, String path, String player) {
        String text = config.getString(path);
        return text != null && text.contains("{player}") ? text.replace("{player}", player) : text;
    }

    public static void sendEmbed(TextChannel channel, MessageEmbed embed) {
        if (channel != null) {
            channel.sendMessageEmbeds(embed).queue();
        }
    }

    public static void addField(EmbedBuilder eb, String name, String value, boolean inline) {
        eb.addField(name, value, inline);
    }

    public static void addColor(EmbedBuilder eb, Color color) {
        eb.setColor(color);
    }


    public static void setTimestamp(EmbedBuilder eb) {
        eb.setTimestamp(java.time.OffsetDateTime.now());
    }

    public static void addImage(EmbedBuilder eb, String imageUrl) {
        eb.setImage(imageUrl);
    }

}
