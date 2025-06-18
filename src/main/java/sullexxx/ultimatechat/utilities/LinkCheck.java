package sullexxx.ultimatechat.utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sullexxx.ultimatechat.UltimateChat;
import sullexxx.ultimatechat.configuration.LinksConfig;
import sullexxx.ultimatechat.discord.DiscordConnect;

import static sullexxx.ultimatechat.discord.DiscordConnect.jda;

public class LinkCheck {
    public static boolean isLink(String player){
        FileConfiguration config = LinksConfig.config;
        FileConfiguration config2 = UltimateChat.getInstance().getConfig();
        if (!config.contains("Players." + player + ".Discord_ID") || config.getString("Players." + player + ".Discord_ID") == null){
            return false;
        } else if (!config.contains("Players."+ player)){
            return false;
        }

        Guild guild = DiscordConnect.jda.getGuildById(config2.getString("Discord.GuildId"));
        if (guild == null) {
            return false;
        }

        try {
            Member member = guild.retrieveMemberById(config.getString("Players." + player + ".Discord_ID")).complete();
            if (member == null) {
                LinksConfig.removeDataLink(player);
                return false;
            }
            return true;
        } catch (Exception e) {
            LinksConfig.removeDataLink(player);
            return false;
        }
    }
    public static Guild guildE(){
        FileConfiguration config2 = UltimateChat.getInstance().getConfig();
        return jda.getGuildById(config2.getString("Discord.GuildId"));
    }
    public static Member memberE(Guild guild, String player){
        FileConfiguration config = LinksConfig.config;
        return guild.getMemberById(config.getString("Players." + player + ".Discord_ID"));

    }
}
