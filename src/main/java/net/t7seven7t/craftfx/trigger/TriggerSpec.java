package net.t7seven7t.craftfx.trigger;

import com.google.common.collect.ImmutableList;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.data.ConfigData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.data.trigger.EntityTypeData;
import net.t7seven7t.craftfx.data.trigger.SlotData;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 */
public final class TriggerSpec {

    private final List<String> aliases = new ArrayList<>();
    private final List<Predicate<TriggerContext>> predicates = new ArrayList<>();
    private final List<Trigger> triggers = new ArrayList<>();
    private final List<Data> defaultData = new ArrayList<>();
    private final Listener listener = new Listener() {
    };

    private TriggerSpec() {
        defaultData.add(new ConfigData());
        defaultData.add(new SlotData("hand"));
        defaultData.add(new EntityTypeData());
        predicates.add(c -> {
            if (!c.getTarget().getEntity().isPresent()) return true;
            final EntityTypeData data = c.getData(EntityTypeData.class).get();
            final Optional<EntityType> optType = data.getEntityType();
            if (optType.isPresent()) return true;
            return c.getTarget().getEntity(optType.get().getEntityClass()).isPresent();
        });
        // todo: add default filters like level/xp based, etc & make defaults configurable
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    private void run(TriggerContext context, Event event) {
        context.spec = this;
        // Check all inventory slots specified by SlotData
        // If any matching run them
        final ItemStack[] contents = context.getInitiator().getInventory().getContents();
        // special use case for where context target is an ItemStack::
        final Optional<ItemStack> optItem = context.getTarget().as(ItemStack.class);
        final int heldSlot = context.getInitiator().getInventory().getHeldItemSlot();
        for (Trigger t : triggers) {
            if (optItem.isPresent()) {
                // special use for contexts that have a ItemStack target
                if (!t.getItemDefinition().isSimilar(optItem.get())) continue;
            } else if (!isItemCorrect(t, contents, heldSlot)) continue;
            final Optional<Function> optFunc = context.getTarget().as(Function.class);
            if (optFunc.isPresent()) {
                // unsafe call if someone uses other functions as target
                context = context.copy(optFunc.get().apply(event));
            }
            final TriggerContext copy = context.copy();
            t.fill(copy);
            if (predicates.stream().anyMatch(p -> !p.test(copy))) continue;
            t.run(copy);
        }
    }

    private boolean isItemCorrect(Trigger trigger, ItemStack[] contents, int heldSlot) {
        final SlotData slotData = trigger.getData(SlotData.class).get();
        final ItemDefinition item = trigger.getItemDefinition();
        if (slotData.isHandSlot() && item.isSimilar(contents[heldSlot])) return true;
        for (int slot : slotData.getSlots()) {
            if (slot >= contents.length) continue;
            if (item.isSimilar(contents[slot])) return true;
        }
        return false;
    }

    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
        defaultData.forEach(d -> trigger.offer(d.copy()));
    }

    @Override
    public String toString() {
        return "TriggerSpec{" +
                "aliases=" + aliases +
                '}';
    }

    public static final class Builder {
        private final TriggerSpec spec = new TriggerSpec();

        public TriggerSpec build() {
            return spec;
        }

        public Builder aliases(String... aliases) {
            for (String alias : aliases) {
                spec.aliases.add(alias.toLowerCase());
            }
            return Builder.this;
        }

        public Builder data(Data data) {
            Iterator<Data> it = spec.defaultData.iterator();
            Class<?> clazz = data.getClass();
            while (it.hasNext()) {
                if (it.next().getClass() == clazz) it.remove();
            }
            spec.defaultData.add(data);
            return Builder.this;
        }

        public <T extends Event> Builder listener(Class<T> eventClazz,
                                                  Function<T, TriggerContext> function,
                                                  EventPriority priority) {
            return listener(eventClazz, function, priority, false);
        }

        public <T extends Event> Builder listener(Class<T> eventClazz,
                                                  Function<T, TriggerContext> function) {
            return listener(eventClazz, function, EventPriority.MONITOR);
        }

        public <T extends Event> Builder listener(Class<T> eventClazz,
                                                  Function<T, TriggerContext> function,
                                                  boolean ignoreCancelled) {
            return listener(eventClazz, function, EventPriority.MONITOR, ignoreCancelled);
        }

        public <T extends Event> Builder listener(Class<T> eventClazz,
                                                  Function<T, TriggerContext> function,
                                                  EventPriority priority, boolean ignoreCancelled) {
            Bukkit.getPluginManager().registerEvent(eventClazz, spec.listener,
                    priority, new TriggerExecutor<>(eventClazz, spec, function), CraftFX.plugin(),
                    ignoreCancelled);
            return Builder.this;
        }

        public Builder filter(Predicate<TriggerContext> predicate) {
            spec.predicates.add(predicate);
            return Builder.this;
        }
    }

    private static class TriggerExecutor<T extends Event> implements EventExecutor {
        private final Function<T, TriggerContext> function;
        private final TriggerSpec spec;
        private final Class<T> clazz;

        private TriggerExecutor(Class<T> clazz, TriggerSpec spec,
                                Function<T, TriggerContext> function) {
            this.clazz = clazz;
            this.spec = spec;
            this.function = function;
        }

        @Override
        public void execute(Listener listener, Event event) throws EventException {
            if (!clazz.isInstance(event)) return;
            final TriggerContext context = function.apply((T) event);
            if (context == null) return;
            if (event.isAsynchronous()) {
                Bukkit.getScheduler().runTask(CraftFX.plugin(), () -> spec.run(context, event));
                return;
            }
            spec.run(context, event);
        }
    }
}
