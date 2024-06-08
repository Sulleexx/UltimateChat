package sullexxx.ultimatechat.utilities;

import java.util.HashMap;
import java.util.Random;

public class LinkCode {

    private static final HashMap<String, String> playerCodes = new HashMap<>();

    public static String GenerateCode(String playerName) {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        String codeStr = String.valueOf(code);
        playerCodes.put(playerName, codeStr);
        return codeStr;
    }

    public static boolean CheckCode(String playerName, String code) {
        String storedCode = playerCodes.get(playerName);
        if (storedCode != null && storedCode.equals(code)) {
            playerCodes.remove(playerName);
            return true;
        }
        return false;
    }
}

