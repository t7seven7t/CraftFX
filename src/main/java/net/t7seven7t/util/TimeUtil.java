package net.t7seven7t.util;

import org.bukkit.util.NumberConversions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing strings into times
 */
public class TimeUtil {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+\\.?\\d*)(\\p{Alpha}*)");

    /**
     * Parses a String into a time. Value is returned in milliseconds.
     */
    public static long parseString(String message) {
        Matcher m = TIME_PATTERN.matcher(message);
        long time = 0;
        while (m.find()) {
            double num = NumberConversions.toDouble(m.group(1));
            String suffix = m.group(2).toLowerCase();
            switch (suffix) {
                case "y":
                    time += 3.15569e10 * num;
                    break;
                case "w":
                    time += 6.048e8 * num;
                    break;
                case "d":
                    time += 8.64e7 * num;
                    break;
                case "h":
                    time += 3.6e6 * num;
                    break;
                case "m":
                    time += 6e4 * num;
                    break;
                case "t":
                    time += 50L * num;
                    break;
                case "ms":
                    time += num;
                    break;
                default:
                    // default to seconds
                    time += 1e3 * num;
                    break;
            }
        }
        return time;
    }

}
