package sullexxx.ultimatechat.utilities;

import sullexxx.ultimatechat.UltimateChat;

public class BruhHelper {
    public static void WrongSoundName(String soundName) {
        UltimateChat.getInstance().getLogger().warning("Incorrect sound name in config.yml: " + soundName + ". Using default sound.");
    }
    public static void NoSelectedSound() {
        UltimateChat.getInstance().getLogger().warning("No selected sound in: config.yml.");
    }
    public static void DisabledSound() {
        UltimateChat.getInstance().getLogger().warning("Sound is disabled in: config.yml. There will be no sound.");
    }
}
