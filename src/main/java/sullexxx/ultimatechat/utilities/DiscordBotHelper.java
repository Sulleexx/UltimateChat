package sullexxx.ultimatechat.utilities;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;


public class DiscordBotHelper {
    public static OnlineStatus getOnlineStatus(String status) {
        return switch (status.toLowerCase()) {
            case "dnd" -> OnlineStatus.DO_NOT_DISTURB;
            case "idle" -> OnlineStatus.IDLE;
            case "invisible" -> OnlineStatus.INVISIBLE;
            default -> OnlineStatus.ONLINE;
        };
    }

    public static Activity getActivity(String type, String text) {
        return switch (type.toLowerCase()) {
            case "listening" -> Activity.listening(text);
            case "watching" -> Activity.watching(text);
            default -> Activity.playing(text);
        };
    }
}
