package net.t7seven7t.craftfx.util;

import net.t7seven7t.craftfx.CraftFX;

import java.util.logging.Level;

/**
 *
 */
public class LogHelper {

    public void info(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public void warning(String message, Object... args) {
        log(Level.WARNING, message, args);
    }

    public void severe(String message, Object... args) {
        log(Level.SEVERE, message, args);
    }

    public void log(Level level, String message, Object... args) {
        CraftFX.plugin().getLogger().log(level, String.format(message, args));

        for (Object o : args) {
            if (o instanceof Throwable && CraftFX.debug()) {
                ((Throwable) o).printStackTrace();
            }
        }
    }

    public void debug(String message, Object... args) {
        if (CraftFX.debug()) info(message, args);
    }

}
