package sullexxx.ultimatechat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;
import sullexxx.ultimatechat.commands.UltimateChatCommand;
import sullexxx.ultimatechat.configuration.LanguageConfig;
import sullexxx.ultimatechat.configuration.LinksConfig;
import sullexxx.ultimatechat.configuration.PlayerSettings;
import sullexxx.ultimatechat.discord.DiscordConnect;
import sullexxx.ultimatechat.exceptions.JDABuilderFailureException;
import sullexxx.ultimatechat.managers.DiscordManager;
import sullexxx.ultimatechat.managers.PluginEventManager;


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
        if (jda != null) {
            jda.shutdown();
        }
        playerSettings.saveConfig();
        linksConfig.saveConfig();
    }

    private void initializeDiscord() {
        DiscordConnect discordConnect = new DiscordConnect();
        if (getConfig().getBoolean("Discord.Enable", false)) {
            try {
                jda = getBuilder().build();
                jda.awaitReady();
                DiscordManager.setupDiscord(jda, discordConnect, this);
            } catch (Exception e) {
                getLogger().severe("Ошибка: " + e.getMessage());
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
            throw new JDABuilderFailureException("Произошла ошибка при попытке связки чата\nс вашим Discord сервером! Проверьте ваш токен.\nСообщение от JDA: " + e.getMessage());
        }
    }

    public static UltimateChat getInstance() {
        return instance;
    }
}
