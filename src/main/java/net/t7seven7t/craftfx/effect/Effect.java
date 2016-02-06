package net.t7seven7t.craftfx.effect;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.data.ConfigDataHolder;
import net.t7seven7t.craftfx.data.effect.DelayData;
import net.t7seven7t.craftfx.data.effect.ExtentData;
import net.t7seven7t.craftfx.data.effect.TimerData;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
public final class Effect extends ConfigDataHolder {

    private final Map<ExtentState, Consumer<EffectContext>> consumerMap;
    /**
     * Map that holds contexts for effects with an unfinished extent state
     */
    private Map<Player, EffectContext> contextMap;

    public Effect(ConfigurationSection config,
                  Map<ExtentState, Consumer<EffectContext>> consumerMap) {
        super(config);
        this.consumerMap = ImmutableMap.copyOf(consumerMap);
    }

    /**
     * Runs this effect in the context provided
     *
     * @param context context
     */
    public void run(EffectContext context) {
        context.holder = this;
        final EffectContext oldContext = getLastContext(context.getInitiator());
        ExtentState state;
        if (oldContext != null) {
            state = oldContext.getProperty("extent-state", ExtentState.class)
                    .map(ExtentState::other).orElse(null);
            // remove old context
            context.copyState(oldContext);
            removeContext(context.getInitiator());
        } else {
            final ExtentData data = getData(ExtentData.class).get();
            state = data.isInverted() ? ExtentState.END : ExtentState.START;
            if (data.isExtentDisabled(state)) {
                state = state.other();
            } else if (!data.isExtentDisabled(state.other())) {
                if (context.getTrigger().isCanceller()) {
                    // cancellers don't want to create new state
                    return;
                }
                addContext(context.getInitiator(), context);
                context.setProperty("extent-state", state);
            }
        }
        // run the effect for the retrieved state
        final Consumer<EffectContext> consumer = consumerMap.get(state);
        if (consumer == null) return;
        final TimerData timerData = getData(TimerData.class).get();
        final long delay = getData(DelayData.class).get().getDelayTicks();
        if (timerData.getIterations() > 1) {
            final int iterations = timerData.getIterations();
            new BukkitRunnable() {
                private int iteration = 0;

                @Override
                public void run() {
                    consumer.accept(context);
                    if (++iteration >= iterations) cancel();
                }
            }.runTaskTimer(CraftFX.plugin(), delay, timerData.getInterval());
        } else if (delay == 0) {
            consumer.accept(context);
        } else {
            Bukkit.getScheduler().runTaskLater(CraftFX.plugin(),
                    () -> consumer.accept(context), delay);
        }
    }

    private void addContext(Player player, EffectContext context) {
        if (contextMap == null) {
            contextMap = new MapMaker().weakKeys().makeMap();
        }
        contextMap.put(player, context);
    }

    private void removeContext(Player player) {
        if (contextMap != null) contextMap.remove(player);
    }

    private EffectContext getLastContext(Player player) {
        return contextMap == null ? null : contextMap.get(player);
    }
}
