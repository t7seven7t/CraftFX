package net.t7seven7t.craftfx.effect;

import com.google.common.collect.ImmutableMap;

import net.t7seven7t.craftfx.data.ConfigDataHolder;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
public final class Effect extends ConfigDataHolder {

    private final Map<ExtentState, Consumer<EffectContext>> consumerMap;

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
        // todo: get ExtentState from some magic floating thing that offers information about the player in context
        final ExtentState state = ExtentState.START;
        // run the effect for the retrieved state
        consumerMap.get(state).accept(context);
    }
}
