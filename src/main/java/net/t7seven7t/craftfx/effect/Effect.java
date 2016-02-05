package net.t7seven7t.craftfx.effect;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

import net.t7seven7t.craftfx.data.ConfigDataHolder;
import net.t7seven7t.craftfx.data.effect.ExtentData;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
        final EffectContext oldContext = getLastContext(context.getInitiator());
        ExtentState state;
        if (oldContext != null) {
            state = oldContext.getProperty("extent-state", ExtentState.class).orElse(null);
            // remove old context
            context.copyState(oldContext);
            removeContext(context.getInitiator());
        } else if (context.getTrigger().isCanceller()) {
            // beyond here we are only creating new state and not ending any
            return;
        } else {
            final ExtentData data = getData(ExtentData.class).get();
            state = data.isInverted() ? ExtentState.END : ExtentState.START;
            if (data.isExtentDisabled(state)) {
                state = state.other();
            } else if (!data.isExtentDisabled(state.other())) {
                addContext(context.getInitiator(), context);
                context.setProperty("extent-state", state);
            }
        }
        // run the effect for the retrieved state
        Consumer<EffectContext> consumer = consumerMap.get(state);
        if (consumer != null) consumer.accept(context);
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
