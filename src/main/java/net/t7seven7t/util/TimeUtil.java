package net.t7seven7t.util;

import org.bukkit.util.NumberConversions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing strings into times
 */
public class TimeUtil {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+\\.?\\d*)(\\p{Alpha}*)");

    public static long parseString(String message) {
        Matcher m = TIME_PATTERN.matcher(message);
        long time = 0;
        while (m.find()) {
            double num = NumberConversions.toDouble(m.group(1));
            String suffix = m.group(2).toLowerCase();
            if (suffix.equals("y")) {
                time += 3.15569e10 * num;
            } else if (suffix.equals("w")) {
                time += 6.048e8 * num;
            } else if (suffix.equals("d")) {
                time += 8.64e7 * num;
            } else if (suffix.equals("h")) {
                time += 3.6e6 * num;
            } else if (suffix.equals("m")) {
                time += 6e4 * num;
            } else if (suffix.equals("t")) {
                time += 50L * num;
            } else if (suffix.equals("ms")) {
                time += num;
            } else {
                // default to seconds
                time += 1e3 * num;
            }
        }
        return time;
    }

}
