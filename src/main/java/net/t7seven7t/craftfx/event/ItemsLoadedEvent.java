package net.t7seven7t.craftfx.event;

import net.t7seven7t.craftfx.item.ItemRegistry;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class ItemsLoadedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ItemRegistry itemRegistry;

    public ItemsLoadedEvent(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
