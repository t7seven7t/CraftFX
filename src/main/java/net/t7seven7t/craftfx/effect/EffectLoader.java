package net.t7seven7t.craftfx.effect;

import com.google.common.collect.Lists;

import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

import static net.t7seven7t.craftfx.effect.EffectLoader.ConfigPath.EFFECT;
import static net.t7seven7t.craftfx.effect.EffectLoader.ConfigPath.EFFECTS_SECTION;
import static net.t7seven7t.craftfx.effect.EffectLoader.ConfigPath.TYPE;

/**
 *
 */
public class EffectLoader {

    /**
     * Loads effects from a config if the effect or effects_section paths exist immediately below
     * the given config node
     */
    public List<Effect> loadEffects(ConfigurationSection config,
                                           ItemDefinition item) throws Exception {
        List<Effect> effectList = Lists.newArrayList();

        if (config.contains(EFFECT)) {
            effectList.add(loadEffect(config.getConfigurationSection(EFFECT), item));
        }

        if (config.contains(EFFECTS_SECTION)) {
            for (String key : config.getConfigurationSection(EFFECTS_SECTION).getKeys(false)) {
                effectList
                        .add(loadEffect(config.getConfigurationSection(EFFECTS_SECTION + "." + key),
                                item));
            }
        }

        return effectList;
    }

    /**
     * Loads an effect from a config
     *
     * @param config config to load from
     * @param item   item that triggers the effect
     * @throws Exception If an error occurs
     */
    public Effect loadEffect(ConfigurationSection config,
                                    ItemDefinition item) throws Exception {
        if (!config.contains(TYPE)) {
            throw new Exception("Effect type not specified for '" + config.getCurrentPath() + "'");
        }

        EffectType type = EffectType.get(config.getString(TYPE));
        if (type == null) {
            throw new Exception("Effect type '" + config.getString(TYPE) + "' is invalid");
        }

        Effect effect = type.supplier.get();
        effect.initialize(type, item, config);
        return effect;
    }

    public static class ConfigPath {
        public static final String EFFECT = "effect";
        public static final String EFFECTS_SECTION = "effects";
        public static final String TYPE = "type";
    }
}
