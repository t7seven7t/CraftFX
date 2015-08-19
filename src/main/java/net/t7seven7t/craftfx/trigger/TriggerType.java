package net.t7seven7t.craftfx.trigger;

import com.google.common.collect.Maps;

import org.bukkit.event.block.Action;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Enum of all possible ways that an effect may be triggered. Statically typed because plugins
 * shouldn't change these.
 */
public enum TriggerType {
    CHAT,
    DEATH,
    EMPTY_BUCKET,
    EQUIP,
    HIT_ENTITY,
    BREAK_ITEM,
    CONSUME_ITEM,
    HOLD_ITEM,
    LEFT_CLICK_AIR,
    LEFT_CLICK_BLOCK,
    MOVE,
    CHANGE_HEALTH,
    PROJECTILE_HIT,
    LAUNCH_PROJECTILE,
    RIGHT_CLICK_AIR,
    RIGHT_CLICK_BLOCK,
    RIGHT_CLICK_ENTITY,
    TELEPORT;

    /**
     * Map of name to TriggerType
     */
    private final static Map<String, TriggerType> BY_NAME = Maps.newHashMap();

    static {
        Arrays.stream(values()).forEach(t -> BY_NAME.put(t.name(), t));
    }

    /**
     * Factory for creating new instances of Trigger for each type
     */
    final Supplier<Trigger> supplier;

    private TriggerType() {
        this.supplier = () -> new Trigger(this);
    }

    /**
     * Get the TriggerType matching the text supplied
     *
     * @param text name of TriggerType
     */
    public static TriggerType matches(String text) {
        return BY_NAME.get(text.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", ""));
    }

    /**
     * Gets the TriggerType matching the action supplied
     *
     * @param action action of TriggerType
     */
    public static TriggerType matches(Action action) {
        return matches(action.name());
    }

}
