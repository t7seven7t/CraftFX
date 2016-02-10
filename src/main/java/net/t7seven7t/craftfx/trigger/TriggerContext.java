package net.t7seven7t.craftfx.trigger;

import com.google.common.collect.ImmutableList;

import net.t7seven7t.craftfx.Target;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.data.DataHolder;
import net.t7seven7t.craftfx.data.DataInterface;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 */
public class TriggerContext implements DataInterface {

    private final Player initiator;
    private final List<Target> targets = new ArrayList<>();
    ItemDefinition itemDefinition;
    TriggerSpec spec;
    DataHolder holder;

    public TriggerContext(Player initiator, Object... targets) {
        this.initiator = initiator;
        for (Object o : targets) {
            if (o instanceof List) {
                for (Object o1 : (List) o) {
                    this.targets.add(o1 instanceof Target ? (Target) o : new Target(o));
                }
            }
            this.targets.add(o instanceof Target ? (Target) o : new Target(o));
        }
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

    @Override
    public String toString() {
        return "TriggerContext{" +
                "initiator=" + initiator +
                ", targets=" + targets +
                ", itemDefinition=" + itemDefinition +
                ", spec=" + spec +
                ", holder=" + holder +
                '}';
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

    public List<Target> getTargets() {
        return ImmutableList.copyOf(targets);
    }

    public Target getTarget() {
        return targets.isEmpty() ? null : targets.get(0);
    }

    public DataHolder getDataHolder() {
        return holder;
    }

    public TriggerContext copy() {
        return copy(targets);
    }

    /**
     * Copies this context but changes the target to the one supplied.
     *
     * @param targets the new target
     * @return a new TriggerContext
     */
    public TriggerContext copy(Object... targets) {
        TriggerContext context = new TriggerContext(initiator, targets);
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
