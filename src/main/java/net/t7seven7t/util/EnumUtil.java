package net.t7seven7t.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Utility class for matching Strings to enum values or constants within enum-like classes
 */
public class EnumUtil {


    public static <V extends Enum<V>> V matchEnumValue(Class<V> enumClass, String id) {
        return getValue(enumClass.getEnumConstants(), id);
    }

    /**
     * Attempts to match a value from one of Bukkit's constants classes by searching for a values()
     * method
     */
    public static <V> V matchConstantValue(Class<V> constantClazz, String id) {
        try {
            Method m = constantClazz.getMethod("values");
            return getValue((V[]) m.invoke(null), id);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static <V> V getValue(V[] values, String id) {
        String pattern = enumify(id);
        return Arrays.stream(values)
                .filter(v -> v != null)
                .filter(v -> pattern.equalsIgnoreCase(v.toString()))
                .findAny()
                .orElse(null);
    }

    public static String enumify(String string) {
        return string.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", "");
    }

}
