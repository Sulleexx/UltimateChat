package sullexxx.ultimatechat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;
import sullexxx.ultimatechat.commands.FunCommands;
import sullexxx.ultimatechat.commands.PmCommand;
import sullexxx.ultimatechat.commands.UltimateChatCommand;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.configuration.LinksConfig;
import sullexxx.ultimatechat.configuration.PlayerSettings;
import sullexxx.ultimatechat.discord.DiscordConnect;
import sullexxx.ultimatechat.exceptions.JDABuilderFailureException;
import sullexxx.ultimatechat.managers.DiscordManager;
import sullexxx.ultimatechat.managers.PluginEventManager;

import java.util.concurrent.TimeUnit;


public final class UltimateChat extends JavaPlugin {
    private static UltimateChat instance;
    private PlayerSettings playerSettings;
    private LinksConfig linksConfig;
    private JDA jda;
    private LogAppender appender;
    private org.apache.logging.log4j.core.Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        LanguageConfig.save();
        new PmCommand(this);
        new FunCommands(this);


        getCommand("ultimatechat").setExecutor(new UltimateChatCommand(this));
        getCommand("ultimatechat").setTabCompleter(new UltimateChatCommand(this));

        playerSettings = new PlayerSettings(this);
        linksConfig = new LinksConfig(this);
        if (DiscordConnect.isDiscordEnabled()){
            initializeDiscord();
        }
        PluginEventManager.registerEvents(this);


        initializeLogging();
    }

    @Override
    public void onDisable() {
        PlayerSettings.saveConfig();
        LinksConfig.saveConfig();

        if (jda != null) {
            jda.shutdown();
            try {
                if (!jda.awaitShutdown(7, TimeUnit.SECONDS)) {
                    jda.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void initializeDiscord() {
        DiscordConnect discordConnect = new DiscordConnect();
        if (getConfig().getBoolean("Discord.Enable", false)) {
            try {
                jda = getBuilder().build();
                jda.awaitReady();
                DiscordManager.setupDiscord(jda, discordConnect, this);
            } catch (Exception e) {
                getLogger().severe("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void initializeLogging() {
        try {
            if (jda == null) return;
            appender = new LogAppender(jda);
            logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
            logger.addAppender(appender);
            appender.sendMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JDABuilder getBuilder() {
        try {
            return JDABuilder.createDefault(getConfig().getString("Discord.Token"))
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES);
        } catch (Exception e) {
            throw new JDABuilderFailureException("There was an error trying to link the chat\n" +
                    "to your Discord server! Check your token.\n" +
                    "Message from JDA: " + e.getMessage());
        }
    }

    public static UltimateChat getInstance() {
        return instance;
    }
}
