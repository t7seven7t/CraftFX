package net.t7seven7t.craftfx.trigger;

import net.t7seven7t.craftfx.Target;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.data.DataHolder;
import net.t7seven7t.craftfx.data.DataInterface;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Optional;
import java.util.function.Function;

/**
 *
 */
public class TriggerContext implements DataInterface {

    private final Player initiator;
    private final Target target;
    ItemDefinition itemDefinition;
    TriggerSpec spec;
    DataHolder holder;

    public TriggerContext(Player initiator, Object target) {
        this.initiator = initiator;
        this.target = target instanceof Target ? (Target) target : new Target(target);
    }

    /**
     * Create a TriggerContext that will delay calculation of the target until after at least one
     * trigger is confirmed in the item matching process
     *
     * @param initiator player that initiated the trigger
     * @param function  function to calculate the target
     * @param <T>       event class to calculate target from
     */
    public <T extends Event> TriggerContext(Player initiator, Function<T, Object> function) {
        this(initiator, (Object) function);
    }

    public TriggerContext(Player initiator) {
        this(initiator, null);
    }

    public TriggerSpec getSpec() {
        return spec;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public Player getInitiator() {
        return initiator;
    }

    public Target getTarget() {
        return target;
    }

    public DataHolder getDataHolder() {
        return holder;
    }

    public TriggerContext copy() {
        return copy(target);
    }

    /**
     * Copies this context but changes the target to the one supplied.
     *
     * @param target the new target
     * @return a new TriggerContext
     */
    public TriggerContext copy(Object target) {
        TriggerContext context = new TriggerContext(initiator, target);
        context.spec = spec;
        // item def and holder are filled by trigger
        context.itemDefinition = itemDefinition;
        context.holder = holder;
        return context;
    }

    @Override
    public <T extends Data> Optional<T> getData(Class<T> clazz) {
        return holder != null ? holder.getData(clazz) : Optional.<T>empty();
    }
}
