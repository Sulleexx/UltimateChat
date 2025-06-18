package sullexxx.ultimatechat.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.commands.PmCommand;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.utilities.GetLinkedPlayer;
import sullexxx.ultimatechat.utilities.LinkCheck;

import java.awt.Color;

import static sullexxx.ultimatechat.discord.DiscordConnect.jda;

public class DiscordPrivateMessages extends ListenerAdapter {

    public static boolean sendPrivateMessage(String playerName, String message, String sender) {
        FileConfiguration cfg = UltimateChat.getInstance().getConfig();
        if (!cfg.getBoolean("Discord.Enable")
                || !cfg.getBoolean("Commands.PmToDiscord.Enable")
                || !cfg.getBoolean("Discord.Link.Enable")) {
            return false;
        }

        String playerId = GetLinkedPlayer.getLinkPlayer(playerName);
        if (playerId.equalsIgnoreCase("notFoundLOL")) {
            Bukkit.getLogger().warning("Discord Link Player not found for: " + playerName);
            return false;
        }

        // Prepare embed data
        String title = cfg.getString("Commands.PmToDiscord.Title", "Message from player: {sender}");
        title = replacePlaceholders(title, sender, playerName, message);
        String description = cfg.getString("Commands.PmToDiscord.Description", "{message}");
        String formattedMessage = replacePlaceholders(description, sender, playerName, message);
        Color color = parseColor(cfg.getString("Commands.PmToDiscord.Color", null));
        String footer = cfg.getString("Commands.PmToDiscord.Footer", "");
        String avatarUrl = "https://cravatar.eu/helmavatar/" + sender + "/55.png";

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(title)
                .setDescription(formattedMessage)
                .setFooter(footer)
                .setThumbnail(avatarUrl);
        if (color != null) eb.setColor(color);

        MessageEmbed embed = eb.build();
        Button replyButton = createButtonWithColor(
                "reply:" + sender + ":" + playerId,
                LanguageConfig.getString("Pm.ModalMenuReplyDiscord.Button.Label")
        );

        Guild guild = LinkCheck.guildE();
        Member member = (guild != null ? guild.getMemberById(playerId) : null);

        if (member != null) {
            member.getUser().openPrivateChannel().queue(pc -> sendEmbed(pc.getUser(), embed, replyButton));
        } else {
            jda.retrieveUserById(playerId).queue(
                    user -> user.openPrivateChannel()
                            .queue(pc -> sendEmbed(user, embed, replyButton)),
                    failure -> Bukkit.getLogger().warning("Failed to retrieve Discord user ID: " + playerId)
            );
        }
        return true;
    }

    private static void sendEmbed(User user, MessageEmbed embed, Button button) {
        user.openPrivateChannel().queue(pc ->
                pc.sendMessageEmbeds(embed)
                        .setComponents(ActionRow.of(button))
                        .queue()
        );
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if (buttonId.startsWith("reply:")) {
            String[] parts = buttonId.split(":");
            if (parts.length >= 3) {
                String originalSender = parts[1];
                String discordUserId = parts[2];

                TextInput messageInput = TextInput.create("reply_message", LanguageConfig.getString("Pm.ModalMenuReplyDiscord.Label"), TextInputStyle.PARAGRAPH)
                        .setPlaceholder(LanguageConfig.getString("Pm.ModalMenuReplyDiscord.Placeholder"))
                        .setRequiredRange(1, 2000)
                        .build();

                Modal modal = Modal.create("reply_modal:" + originalSender + ":" + discordUserId, LanguageConfig.getString("Pm.ModalMenuReplyDiscord.Title") + originalSender)
                        .addComponents(ActionRow.of(messageInput))
                        .build();

                event.replyModal(modal).queue();
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        if (modalId.startsWith("reply_modal:")) {
            String[] parts = modalId.split(":");
            if (parts.length >= 3) {
                String toPlayer = parts[1];
                String fromDiscordUserId = parts[2];
                String replyMessage = event.getValue("reply_message").getAsString();

                boolean isSent = sendReplyToPlayer(toPlayer, fromDiscordUserId, replyMessage);
                String responseKey = isSent ? "Pm.ModalMenuReplyDiscord.Successful_reply" : "Pm.ModalMenuReplyDiscord.UnSuccessful_reply";
                event.reply(LanguageConfig.getString(responseKey).replace("{player}", toPlayer))
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    private boolean sendReplyToPlayer(String toPlayer, String fromDiscordUserId, String message) {
        String senderName = GetLinkedPlayer.getPlayerByDiscordId(fromDiscordUserId);
        if (!senderName.equals("notFoundLOL")) {
            Player player = Bukkit.getPlayer(toPlayer);
            if (player != null && player.isOnline()) {
                PmCommand.sendPrivateMessageFromDiscord(senderName, player, message, toPlayer);
                return true;
            }
            return PmCommand.handleOfflinePlayerFromDiscord(senderName, toPlayer, message);
        }
        return false;
    }

    private static String replacePlaceholders(String text, String sender, String player, String message) {
        return text.replace("%sender%", sender)
                .replace("%player%", player)
                .replace("%message%", message);
    }

    private static Color parseColor(String hex) {
        if (hex == null || hex.isEmpty()) return null;
        try {
            return Color.decode(hex);
        } catch (NumberFormatException ex) {
            Bukkit.getLogger().warning("Invalid color code: " + hex);
            return null;
        }
    }

    private static Button createButtonWithColor(String id, String label) {
        String colorString = LanguageConfig.getString("Pm.ModalMenuReplyDiscord.Button.Color");
        if (colorString.isEmpty()) {
            return Button.primary(id, label);
        }
        return switch (colorString.toLowerCase()) {
            case "danger", "red" -> Button.danger(id, label);
            case "secondary", "gray", "grey" -> Button.secondary(id, label);
            case "success", "green" -> Button.success(id, label);
            default -> Button.primary(id, label);
        };
    }
}
