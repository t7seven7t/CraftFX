package net.t7seven7t.craftfx.trigger;

import com.google.common.collect.MapMaker;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.data.ConfigDataHolder;
import net.t7seven7t.craftfx.data.trigger.CooldownData;
import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.effect.EffectContext;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class Trigger extends ConfigDataHolder {

    /**
     * The ItemDefinition that causes this trigger
     */
    private final ItemDefinition itemDefinition;
    /**
     * The list of effects that this trigger causes
     */
    private final List<Effect> effectList;
    /**
     * Whether this trigger should only cancel ongoing effects
     */
    private final boolean canceller;
    /**
     * The TriggerSpec for this Trigger
     */
    private final TriggerSpec spec;
    /**
     * The times each player last used this trigger
     */
    private Map<Player, Long> lastUseTimes;

    Trigger(TriggerSpec spec, ItemDefinition itemDefinition, ConfigurationSection config,
            List<Effect> effectList,
            boolean canceller) {
        super(config);
        this.spec = spec;
        this.itemDefinition = itemDefinition;
        this.effectList = effectList;
        this.canceller = canceller;
        spec.addTrigger(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public TriggerSpec getSpec() {
        return spec;
    }

    void setupCooldowns() {
        final CooldownData cooldownData = getData(CooldownData.class).get();
        if (cooldownData.getCooldownMillis() != 0) {
            lastUseTimes = new MapMaker().weakKeys().makeMap();
        } else {
            lastUseTimes = null;
        }
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public boolean isCanceller() {
        return canceller;
    }

    /**
     * Runs all of this trigger's associated effects in the context provided
     *
     * @param context context
     */
    public void run(TriggerContext context) {
        if (lastUseTimes != null) {
            if (!lastUseTimes.containsKey(context.getInitiator())) {
                lastUseTimes.put(context.getInitiator(), System.currentTimeMillis());
            } else {
                final CooldownData data = getData(CooldownData.class).get();
                if (System.currentTimeMillis() - lastUseTimes.get(context.getInitiator())
                        > data.getCooldownMillis()) {
                    lastUseTimes.remove(context.getInitiator());
                } else {
                    return;
                }
            }
        }
        // run all effects for initiator:
        // todo: get targeting params
        for (Effect effect : effectList) {
            final EffectContext effectContext = new EffectContext(effect, context.getInitiator(),
                    context.getTarget(), context.getItemDefinition(), context.getSpec(), this);
            effect.run(effectContext);
        }
    }

    /**
     * Fills a TriggerContext with this Trigger's state
     *
     * @param context context
     */
    public void fill(TriggerContext context) {
        context.holder = this;
        context.itemDefinition = this.itemDefinition;
    }

    public static class Builder {

        private final List<Effect> effectList = new ArrayList<>();
        private final ConfigurationSection config = new MemoryConfiguration();
        private TriggerSpec spec;
        private ItemDefinition itemDefinition;
        private boolean canceller;

        public Builder item(ItemDefinition item) {
            this.itemDefinition = item;
            return this;
        }

        public Builder spec(String alias) {
            return spec(CraftFX.instance().getTriggerRegistry().getSpec(alias).orElse(this.spec));
        }

        public Builder spec(TriggerSpec spec) {
            this.spec = spec;
            return this;
        }

        public Builder cancels(boolean cancels) {
            this.canceller = cancels;
            return this;
        }

        public Builder effect(Effect effect) {
            this.effectList.add(effect);
            return this;
        }

        public Builder property(String propertyName, Object value) {
            config.set(propertyName, value);
            return this;
        }

        public Trigger build() {
            Validate.notNull(itemDefinition, "ItemDefinition cannot be null");
            Validate.notNull(spec, "TriggerSpec cannot be null");
            return new Trigger(spec, itemDefinition, config, effectList, canceller);
        }
    }
}
