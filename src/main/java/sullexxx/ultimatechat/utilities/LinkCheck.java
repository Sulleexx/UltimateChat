package sullexxx.ultimatechat.utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LinksConfig;
import sullexxx.ultimatechat.discord.DiscordConnect;

public class LinkCheck {
    public static boolean isLink(String player){
        FileConfiguration config = LinksConfig.config;
        FileConfiguration config2 = UltimateChat.getInstance().getConfig();
        if (!config.contains("Players." + player + ".Discord_ID") || config.getString("Players." + player + ".Discord_ID") == null){
            return false;
        } else if (!config.contains("Players."+ player)){
            return false;
        }

        TextChannel channel = DiscordConnect.jda.getTextChannelById(config2.getString("Discord.ChannelId"));
        Guild guild = channel.getGuild();
        Member member = guild.getMemberById(config.getString("Players." + player + ".Discord_ID"));
        if (member == null) {
            LinksConfig.removeDataLink(player);
            return false;
        }

        return true;
    }
}
