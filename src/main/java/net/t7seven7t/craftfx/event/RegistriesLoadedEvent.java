package net.t7seven7t.craftfx.event;

import net.t7seven7t.craftfx.CraftFX;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class RegistriesLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final CraftFX craftFX = CraftFX.instance();

    public RegistriesLoadedEvent() {
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CraftFX getCraftFX() {
        return craftFX;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
