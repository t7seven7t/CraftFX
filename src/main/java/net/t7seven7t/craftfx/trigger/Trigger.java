package net.t7seven7t.craftfx.trigger;

import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.item.ItemConfigurationException;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Class defining how and if an effect should be triggered
 */
public class Trigger {

    /**
     * Type classification of this trigger
     */
    private final TriggerType type;
    /**
     * List of Effects fired when this trigger is triggered
     */
    List<Effect> effectList;
    /**
     * Configuration information
     */
    private ConfigurationSection config;

    /**
     * Constructor for TriggerType supplier
     */
    Trigger(TriggerType type) {
        this.type = type;
    }

    /**
     * Initializes the trigger immediately after it is created
     */
    final void initialize(ConfigurationSection config) throws Exception {
        this.config = config;
        initialize();
    }

    /**
     * Initialize the trigger in here when extending this class. Note that the paths 'effect' and
     * 'effects' are reserved for effects and should not be relied upon.
     *
     * @throws ItemConfigurationException if there is a problem with the configuration
     */
    public void initialize() throws ItemConfigurationException {
    }

    /**
     * Whilst this list is modifiable bare in mind that it may be shared by other Trigger objects
     */
    public List<Effect> getEffects() {
        return effectList;
    }

    public TriggerType getType() {
        return type;
    }
}
