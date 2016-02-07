package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.Target;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.data.DataInterface;
import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.craftfx.target.TargetSelector;
import net.t7seven7t.craftfx.trigger.Trigger;
import net.t7seven7t.craftfx.trigger.TriggerSpec;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 */
public class EffectContext implements DataInterface {
    private final Player initiator;
    private final Target origin;
    // some kind of targeting params
    private final ItemDefinition itemDefinition;
    private final TriggerSpec triggerSpec;
    private final Trigger trigger;
    private final Effect effect;
    TargetSelector selector;
    private Map<String, Object> properties;

    /**
     * The instance of Effect this context is to be passed to
     */
    public EffectContext(Effect effect, Player initiator, Target origin,
                         ItemDefinition itemDefinition, TriggerSpec triggerSpec, Trigger trigger) {
        this.effect = effect;
        this.origin = origin;
        this.initiator = initiator;
        this.itemDefinition = itemDefinition;
        this.triggerSpec = triggerSpec;
        this.trigger = trigger;
    }

    public Target getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        return "EffectContext{" +
                "initiator=" + initiator +
                ", itemDefinition=" + itemDefinition +
                ", triggerSpec=" + triggerSpec +
                '}';
    }

    public TargetSelector getTargetSelector() {
        return selector;
    }

    public List<Target> getTargets() {
        return selector.getTargets();
    }

    public Trigger getTrigger() {
        return trigger;
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

    public void forTargets(Consumer<Target> consumer) {
        getTargets().forEach(consumer);
    }

    /**
     * Convenience method for scheduling a task
     *
     * @param r runnable to execute one tick later
     */
    public void run(Runnable r) {
        run(r, 0);
    }

    /**
     * Convenience method for scheduling a task
     *
     * @param r     runnable to execute
     * @param delay delay in ticks
     */
    public void run(Runnable r, long delay) {
        Bukkit.getScheduler().runTaskLater(CraftFX.plugin(), r, delay);
    }

    /**
     * Copies state from another context into this context
     *
     * @param context context to copy from
     */
    void copyState(EffectContext context) {
        this.properties = context.properties;
    }

    @Override
    public <T extends Data> Optional<T> getData(Class<T> clazz) {
        return effect != null ? effect.getData(clazz) : Optional.<T>empty();
    }

}
