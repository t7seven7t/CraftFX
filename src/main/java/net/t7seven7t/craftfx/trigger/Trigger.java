package net.t7seven7t.craftfx.trigger;

import com.google.common.collect.MapMaker;

import net.t7seven7t.craftfx.data.ConfigDataHolder;
import net.t7seven7t.craftfx.data.trigger.CooldownData;
import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.effect.EffectContext;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
     * The times each player last used this trigger
     */
    private Map<Player, Long> lastUseTimes;

    Trigger(ItemDefinition itemDefinition, ConfigurationSection config, List<Effect> effectList,
            boolean canceller) {
        super(config);
        this.itemDefinition = itemDefinition;
        this.effectList = effectList;
        this.canceller = canceller;
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

}
