package net.t7seven7t.craftfx.effect;

import org.bukkit.configuration.ConfigurationSection;

/**
 *
 */
public abstract class EffectData {

    private ConfigurationSection config;

    void init(Effect effect) {
        this.config = effect.getConfig();
    }

    public ConfigurationSection getConfig() {
        return config;
    }

}
