package net.t7seven7t.craftfx.effect;

import com.google.common.collect.ImmutableList;

import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.data.effect.DelayData;
import net.t7seven7t.craftfx.data.effect.ExtentData;
import net.t7seven7t.craftfx.data.effect.TimerData;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
public final class EffectSpec {

    private final List<String> aliases = new ArrayList<>();
    private final List<Data> defaultData = new ArrayList<>();
    private final Map<ExtentState, Consumer<EffectContext>> consumerMap = new IdentityHashMap<>(2);

    private EffectSpec() {
        defaultData.add(new ExtentData());
        defaultData.add(new DelayData(0));
        defaultData.add(new TimerData());
    }

    public static Builder builder() {
        return new Builder();
    }

    public Effect newEffect(ConfigurationSection config) {
        final Effect effect = new Effect(config, consumerMap);
        defaultData.forEach(d -> effect.offer(d.copy()));
        return effect;
    }

    public List<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    public static final class Builder {
        private final EffectSpec spec;

        public Builder() {
            this.spec = new EffectSpec();
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

        /**
         * Adds an effect to be run at the specified extent state. Only one consumer allowed per
         * state. Additional will overwrite previous.
         *
         * @param state    state to run the effect at
         * @param consumer the method that runs the effect
         * @return the same builder object
         */
        public Builder effect(ExtentState state, Consumer<EffectContext> consumer) {
            spec.consumerMap.put(state, consumer);
            return Builder.this;
        }

        /**
         * Fills all extent states with the consumer to run.
         *
         * @param consumer the method that runs the effect
         * @return the same builder object
         */
        public Builder effect(Consumer<EffectContext> consumer) {
            spec.consumerMap.put(ExtentState.START, consumer);
            spec.consumerMap.put(ExtentState.END, consumer);
            return Builder.this;
        }

        public EffectSpec build() {
            return spec;
        }
    }

}
