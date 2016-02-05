package net.t7seven7t.craftfx.trigger;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import net.t7seven7t.craftfx.data.ConfigDataHolder;
import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.effect.EffectContext;
import net.t7seven7t.craftfx.effect.EffectLoader;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
     * A multimap of all effect context instances being managed by this trigger.
     */
    private final Multimap<Player, EffectContext> playerEffectMultimap = Multimaps.newListMultimap(
            new MapMaker().weakKeys().makeMap(), ArrayList::new);

    public Trigger(ItemDefinition itemDefinition, ConfigurationSection config) throws Exception {
        super(config);
        this.itemDefinition = itemDefinition;
        this.effectList = new EffectLoader().loadEffects(config);
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    /**
     * Runs all of this trigger's associated effects in the context provided
     *
     * @param context context
     */
    public void run(TriggerContext context) {
        // run all effects for initiator:
        // todo: get targeting params
        for (Effect effect : effectList) {
            // effect context will store any state
            final EffectContext effectContext = new EffectContext(context.getInitiator(),
                    context.getItemDefinition(), context.getSpec());
            effect.run(effectContext);
            // todo: store context in the multimap for callbacks or runnable
            // playerEffectMultimap.put(context.getInitiator(), effectContext);
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
