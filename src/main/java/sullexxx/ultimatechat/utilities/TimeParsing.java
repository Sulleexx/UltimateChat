package sullexxx.ultimatechat.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParsing {
    private TimeParsing() {}

    /**
     * @return parsed millis
     */
    public static Long timeParser(String time) {
        Pattern pattern = Pattern.compile("(\\d+|\\d+\\.\\d+)([smhd])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(time);

        long t = 0;

        while (matcher.find()) {
            var value = matcher.group(1);
            var unit = matcher.group(2);

            var unitToMillis = 0;

            switch (unit.toLowerCase()) {
                case "s" -> unitToMillis = 1000;
                case "m" -> unitToMillis = 1000 * 60;
                case "h" -> unitToMillis = 1000 * 60 * 60;
                case "d" -> unitToMillis = 1000 * 60 * 60 * 24;
                default -> throw new IllegalArgumentException("While trying to parse your time duration, an unknown unit appeared! \nString: '" + time + "' \nUnknown unit: " + unit);
            }

            t = t + (unitToMillis * Long.parseLong(value));
        }

        return t;
    }
}
