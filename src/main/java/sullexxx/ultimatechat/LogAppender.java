package sullexxx.ultimatechat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class LogAppender extends AbstractAppender {
    private String messages = "";
    private final JDA jda;

    public LogAppender(JDA jda) {
        super("MyLogAppender" + System.currentTimeMillis(), null, null);
        this.jda = jda;
        start();
    }

    @Override
    public void append(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();
        message = "[" + java.time.LocalTime.now() + " " + event.getLevel().toString() + "]: " + message + "\n";
        messages += message;
    }

    public void sendMessages() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (messages.length() != 0) {
                        messages = messages.replaceAll("\u001B\\[[;\\d]*m", "");
                        if (messages.length() > 2000) {
                            String messageTooLong = "\n\nThis message exceeded the Discord message limit (2000 characters), so the rest was cut out. To see it in full, please check your console!";
                            messages = messages.substring(0, (1999 - messageTooLong.length()) - 6);
                            messages += messageTooLong;
                        }
                        FileConfiguration config = UltimateChat.getInstance().getConfig();
                        if (jda == null) return;
                        if (UltimateChat.getInstance().getConfig().getString("Discord.ConsoleChannelId") == null) return;
                        String channelId = config.getString("Discord.ConsoleChannelId");
                        if (channelId != null && !channelId.isEmpty()) {
                            try {
                                TextChannel channel = jda.getTextChannelById(channelId);
                                if (channel != null) {
                                    channel.sendMessage("```" + messages + "```").queue();
                                } else {
                                    Bukkit.getLogger().severe("[UC] Invalid channel ID '" + channelId + "'! Make sure the correct channel ID is specified in the configuration file! Disabling console module...");
                                    cancel();
                                }
                            } catch (NumberFormatException numberFormatException) {
                                Bukkit.getLogger().severe("[UC] Invalid channel ID format '" + channelId + "'! Disabling console module...");
                                cancel();
                            }
                        } else {
                            Bukkit.getLogger().severe("[UC] Channel ID not found in config file! Disabling console module...");
                            cancel();
                        }
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().severe("[UC] There was an error sending messages to Discord: " + e.getMessage());
                }
                messages = "";
            }
        }.runTaskTimer(UltimateChat.getInstance(), 0L, 20L);
    }
}
