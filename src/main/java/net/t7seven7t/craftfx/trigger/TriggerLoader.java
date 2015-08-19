package net.t7seven7t.craftfx.trigger;

import com.google.common.collect.Lists;

import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.effect.EffectLoader;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

import static net.t7seven7t.craftfx.trigger.TriggerLoader.ConfigPath.TYPE;

/**
 *
 */
public class TriggerLoader {

    private final EffectLoader effectLoader;

    public TriggerLoader() {
        this.effectLoader = new EffectLoader();
    }

    /**
     * Creates new triggers from a config
     */
    public List<Trigger> loadTriggers(ConfigurationSection config,
                                      ItemDefinition item) throws Exception {
        List<Trigger> triggerList = Lists.newArrayList();

        if (!config.contains(TYPE)) {
            throw new Exception("No trigger types specified at '" + config.getCurrentPath() + "'");
        }

        if (config.isList(TYPE)) {
            for (String key : config.getStringList(TYPE)) {
                triggerList.add(loadTrigger(key));
            }
        } else {
            triggerList.add(loadTrigger(config.getString(TYPE)));
        }

        // Load each effect that triggers will need to fire and give it to them
        List<Effect> effectList = effectLoader.loadEffects(config, item);
        for (Trigger t : triggerList) {
            t.effectList = effectList;
            t.initialize(config);
        }

        return triggerList;
    }

    /**
     * Creates a new trigger by name. Trigger has not yet been initialized or given its effect list
     *
     * @param name name of the trigger type
     * @throws Exception if the name does not match that of any loaded trigger type
     */
    private Trigger loadTrigger(String name) throws Exception {
        TriggerType type = TriggerType.matches(name);
        if (type == null) {
            throw new Exception("Trigger type '" + name + "' is invalid.");
        }

        return type.supplier.get();
    }

    public static class ConfigPath {
        public static final String TYPE = "type";
    }
}
