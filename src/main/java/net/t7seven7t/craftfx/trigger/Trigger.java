package net.t7seven7t.craftfx.trigger;

import net.t7seven7t.craftfx.data.ConfigDataHolder;
import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.craftfx.util.MessageUtil;

import org.bukkit.configuration.ConfigurationSection;

/**
 *
 */
public class Trigger extends ConfigDataHolder {

    /**
     * The ItemDefinition that causes this trigger
     */
    private final ItemDefinition itemDefinition;

    public Trigger(ItemDefinition itemDefinition, ConfigurationSection config) {
        super(config);
        this.itemDefinition = itemDefinition;
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
        // testing:
        MessageUtil.message(context.getInitiator(), "Triggered %s!", context.getSpec());
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
