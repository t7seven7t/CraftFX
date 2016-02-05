package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.craftfx.trigger.TriggerSpec;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public class EffectContext {

    private final Player initiator;
    // some kind of targeting params
    private final ItemDefinition itemDefinition;
    private final TriggerSpec triggerSpec;
    private Map<String, Object> properties;

    /**
     * The instance of Effect this context is to be passed to
     */

    public EffectContext(Player initiator, ItemDefinition itemDefinition,
                         TriggerSpec triggerSpec) {
        this.initiator = initiator;
        this.itemDefinition = itemDefinition;
        this.triggerSpec = triggerSpec;
    }

    public TriggerSpec getTriggerSpec() {
        return triggerSpec;
    }

    public Player getInitiator() {
        return initiator;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setProperty(String propertyName, Object value) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(propertyName, value);
    }

    public Optional<Object> getProperty(String propertyName) {
        return properties == null ? Optional.empty() : Optional
                .ofNullable(properties.get(propertyName));
    }

    public <T> Optional<T> getProperty(String propertyName, Class<T> clazz) {
        return getProperty(propertyName).filter(clazz::isInstance).map(clazz::cast);
    }

    /**
     * Copies state from another context into this context
     *
     * @param context context to copy from
     */
    void copyState(EffectContext context) {
        this.properties = context.properties;
    }

}
