package sullexxx.ultimatechat.utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.configuration.file.FileConfiguration;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.discord.DiscordConnect;

public class GetMemberById {
    public static Member getMember(String id) {
        FileConfiguration config = UltimateChat.getInstance().getConfig();
        String guildId = config.getString("Discord.GuildId");
        if (guildId == null || guildId.isEmpty()) {
            return null;
        }
        Guild guild = DiscordConnect.jda.getGuildById(guildId);
        if (guild == null) {
            return null;
        }
        return guild.getMemberById(id);
    }
}