package sullexxx.ultimatechat.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.configuration.file.FileConfiguration;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.utilities.GetLinkedPlayer;
import sullexxx.ultimatechat.utilities.GetMemberById;

import java.awt.*;

public class DiscordPrivateMessages {
    public static boolean sendPrivateMessage(String playerName, String message, String sender) {
        if (!UltimateChat.getInstance().getConfig().getBoolean("Discord.Enable")) return false;
        if (!UltimateChat.getInstance().getConfig().getBoolean("Commands.PmToDiscord.Enable")) return false;
        if (!UltimateChat.getInstance().getConfig().getBoolean("Discord.Link.Enable")) return false;

        String playerId = GetLinkedPlayer.getLinkPlayer(playerName);
        if (playerId == null || playerId.equalsIgnoreCase("notFoundLOL")) return false;

        Member member = GetMemberById.getMember(playerId);
        if (member == null) return false;

        FileConfiguration config = UltimateChat.getInstance().getConfig();

        String title = config.getString("Commands.PmToDiscord.Title", "Сообщение от игрока {sender}");
        title = replacePlaceholders(title, sender, playerName, message);

        String description = config.getString("Commands.PmToDiscord.Description", "{message}");
        String formattedMessage = replacePlaceholders(description, sender, playerName, message);

        String colorHex = config.getString("Commands.PmToDiscord.Color", "");
        Color color = parseColor(colorHex);

        String footer = config.getString("Commands.PmToDiscord.Footer", "");

        String avatarUrl = "https://cravatar.eu/helmavatar/" + sender + "/55.png";

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(formattedMessage);
        embedBuilder.setColor(color);
        embedBuilder.setFooter(footer);
        embedBuilder.setThumbnail(avatarUrl);

        MessageEmbed embed = embedBuilder.build();

        member.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessageEmbeds(embed).queue();
        });

        return true;
    }

    private static String replacePlaceholders(String text, String sender, String player, String message) {
        return text
                .replace("%sender%", sender)
                .replace("%player%", player)
                .replace("%message%", message);
    }

    private static Color parseColor(String colorHex) {
        if (colorHex == null || colorHex.isEmpty()) {
            return null;
        }

        try {
            return Color.decode(colorHex);
        } catch (NumberFormatException e) {
            System.err.println("Некорректный формат цвета: " + colorHex);
            return null;
        }
    }
}