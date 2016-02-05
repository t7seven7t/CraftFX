package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.craftfx.trigger.TriggerSpec;

import org.bukkit.entity.Player;

/**
 *
 */
public class EffectContext {

    private final Player initiator;
    // some kind of targeting params
    private final ItemDefinition itemDefinition;
    private final TriggerSpec triggerSpec;

    /**
     * The instance of Effect this context is to be passed to
     */

    public EffectContext(Player initiator, ItemDefinition itemDefinition,
                         TriggerSpec triggerSpec) {
        this.initiator = initiator;
        this.itemDefinition = itemDefinition;
        this.triggerSpec = triggerSpec;
    }

    public TriggerSpec getTriggerSpec() {
        return triggerSpec;
    }

    public Player getInitiator() {
        return initiator;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

}
