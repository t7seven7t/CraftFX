package net.t7seven7t.craftfx.trigger;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class TriggerLoader {

    /**
     * Creates new Triggers from a config
     *
     * @param item   item definition
     * @param config backing config
     * @return list of triggers
     */
    public List<Trigger> loadTriggers(ItemDefinition item,
                                      ConfigurationSection config) throws Exception {
        final List<Trigger> triggerList = new ArrayList<>();

        if (!config.contains("type")) {
            throw new Exception("No trigger types specified at '" + config.getCurrentPath() + "'");
        }

        if (config.isList("type")) {
            for (String key : config.getStringList("type")) {
                triggerList.add(loadTrigger(key, item, config));
            }
        } else {
            triggerList.add(loadTrigger(config.getString("type"), item, config));
        }

        return triggerList;
    }

    /**
     * Creates a new Trigger and registers it with the specified TriggerSpec type.
     *
     * @param type   the {@link TriggerSpec} type
     * @param config backing config
     * @return the trigger
     * @throws Exception an exception if the trigger type isn't registered
     */
    private Trigger loadTrigger(String type, ItemDefinition item,
                                ConfigurationSection config) throws Exception {
        final Optional<TriggerSpec> opt = CraftFX.instance().getTriggerRegistry().getSpec(type);
        if (!opt.isPresent()) throw new Exception("Trigger type '" + type + "' isn't registered.");
        final Trigger trigger = new Trigger(item, config);
        opt.get().addTrigger(trigger);
        return trigger;
    }

}
