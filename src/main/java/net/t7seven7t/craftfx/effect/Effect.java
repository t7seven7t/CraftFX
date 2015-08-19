package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.item.ItemConfigurationException;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Class encloses a process to be run when triggered.
 */
public class Effect {

    /**
     * Configuration information
     */
    private ConfigurationSection config;
    /**
     * EffectType for classification of this effect
     */
    private EffectType type;
    /**
     * ItemDefinition that triggers this effect
     */
    private ItemDefinition item;

    /**
     * Empty constructor for factory
     */
    Effect() {
    }

    /**
     * Initializes the effect immediately after it is created.
     */
    final void initialize(EffectType type, ItemDefinition item,
                          ConfigurationSection config) throws ItemConfigurationException {
        this.config = config;
        this.type = type;
        this.item = item;
    }

    /**
     * Initialize the effect in here when extending this class.
     *
     * @throws ItemConfigurationException if there is a problem with the configuration
     */
    public void initialize() throws ItemConfigurationException {
    }

    /**
     * Get the configuration information for this effect
     */
    public ConfigurationSection getConfig() {
        return config;
    }

    /**
     * Get the EffectType
     */
    public EffectType getType() {
        return type;
    }

    /**
     * Get the item definition
     */
    public ItemDefinition getItem() {
        return item;
    }
}
