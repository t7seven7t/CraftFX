package net.t7seven7t.util;

import net.md_5.bungee.api.ChatColor;

import java.text.MessageFormat;

/**
 * Contains utility methods for formatting strings
 */
public class FormatUtil {

    /**
     * Replaces arguments in the result string and translates color codes
     */
    public static String format(String pattern, Object... arguments) {
        String result = MessageFormat.format(pattern, arguments);
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    /**
     * Transforms a string to all uppercase and replaces spaces with underscores.
     */
    public static String makeConstant(String string) {
        return string.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", "");
    }

}
