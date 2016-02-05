package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.CraftFX;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class EffectLoader {

    /**
     * Creates new Effects from a config
     *
     * @param config a config section containing either an 'effect' or 'effects' tag
     */
    public List<Effect> loadEffects(ConfigurationSection config) throws Exception {
        final List<Effect> effectList = new ArrayList<>();
        if (config.contains("effect")) {
            effectList.add(loadEffect(config.getConfigurationSection("effect")));
        }
        if (config.contains("effects")) {
            for (String key : config.getConfigurationSection("effects").getKeys(false)) {
                effectList.add(loadEffect(config.getConfigurationSection("effects." + key)));
            }
        }
        return effectList;
    }

    /**
     * Creates a new Effect from config section
     *
     * @param config the config section of the effect with the 'type' tag immediately beneath it
     */
    public Effect loadEffect(ConfigurationSection config) throws Exception {
        if (!config.contains("type")) {
            throw new Exception("No effect type specified at '" + config.getCurrentPath() + "'");
        }
        final String type = config.getString("type");
        final Optional<EffectSpec> opt = CraftFX.instance().getEffectRegistry().getSpec(type);
        if (!opt.isPresent()) throw new Exception("Effect type '" + type + "' isn't registered.");
        return opt.get().newEffect(config);
    }

}
